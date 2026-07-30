package com.library.service;

import com.library.dto.BorrowRecordDTO;
import com.library.dto.ReturnRecordDTO;
import com.library.entity.*;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing book borrowing and returning operations.
 */
@Service
@Transactional
public class BorrowService {

    private static final Logger logger = LoggerFactory.getLogger(BorrowService.class);

    private final BorrowRecordRepository borrowRecordRepository;
    private final ReturnRecordRepository returnRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private static final int MAX_BORROW_DAYS = 14;
    private static final int MAX_ACTIVE_BORROWS = 5;
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("1.00");

    /**
     * Constructor with dependency injection
     */
    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                         ReturnRecordRepository returnRecordRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.returnRecordRepository = returnRecordRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Borrow a book for a user.
     */
    public BorrowRecordDTO borrowBook(Long userId, Long bookId) {
        logger.info("Processing borrow request: userId={}, bookId={}", userId, bookId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // Check if book is available
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("Book is not available for borrowing");
        }

        // Check active borrow limit
        Integer activeBorrows = borrowRecordRepository.countActiveBorrows(userId);
        if (activeBorrows >= MAX_ACTIVE_BORROWS) {
            throw new IllegalStateException("Maximum borrow limit (" + MAX_ACTIVE_BORROWS + ") reached");
        }

        // Check if user already has this book borrowed
        if (borrowRecordRepository.findActiveByUserAndBook(userId, bookId).isPresent()) {
            throw new IllegalStateException("User already has this book borrowed");
        }

        // Create borrow record
        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusDays(MAX_BORROW_DAYS);

        BorrowRecord borrowRecord = new BorrowRecord(user, book, borrowDate, dueDate);
        borrowRecord.setStatus(BorrowRecord.BorrowStatus.BORROWED);

        BorrowRecord saved = borrowRecordRepository.save(borrowRecord);

        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) {
            book.setStatus(Book.BookStatus.BORROWED);
        }
        bookRepository.save(book);

        logger.info("Book borrowed successfully: borrowId={}, bookId={}, userId={}", saved.getId(), bookId, userId);

        return BorrowRecordDTO.from(saved);
    }

    /**
     * Return a borrowed book.
     */
    public ReturnRecordDTO returnBook(Long borrowRecordId, ReturnRecord.BookCondition condition, String damageNotes) {
        logger.info("Processing return request: borrowRecordId={}", borrowRecordId);

        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
            .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with id: " + borrowRecordId));

        if (!borrowRecord.getStatus().equals(BorrowRecord.BorrowStatus.BORROWED) &&
            !borrowRecord.getStatus().equals(BorrowRecord.BorrowStatus.OVERDUE)) {
            throw new IllegalStateException("Book has already been returned");
        }

        // Calculate days late and fine
        LocalDateTime returnDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowRecord.getDueDate();
        Integer daysLate = 0;
        BigDecimal fineAmount = BigDecimal.ZERO;

        if (returnDate.isAfter(dueDate)) {
            daysLate = (int) ChronoUnit.DAYS.between(dueDate, returnDate);
            fineAmount = FINE_PER_DAY.multiply(new BigDecimal(daysLate));

            // Add damage charges for poor/damaged conditions
            if (condition == ReturnRecord.BookCondition.POOR) {
                fineAmount = fineAmount.add(new BigDecimal("10.00"));
            } else if (condition == ReturnRecord.BookCondition.DAMAGED) {
                fineAmount = fineAmount.add(new BigDecimal("25.00"));
            }
        }

        // Create return record
        ReturnRecord returnRecord = new ReturnRecord(borrowRecord, borrowRecord.getUser(), returnDate);
        returnRecord.setBookCondition(condition);
        returnRecord.setDamageNotes(damageNotes);
        returnRecord.setDaysLate(daysLate);
        returnRecord.setFineAmount(fineAmount);

        ReturnRecord saved = returnRecordRepository.save(returnRecord);

        // Update borrow record status
        borrowRecord.setReturnDate(returnDate);
        borrowRecord.setStatus(BorrowRecord.BorrowStatus.RETURNED);
        borrowRecordRepository.save(borrowRecord);

        // Update book availability
        Book book = borrowRecord.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        if (book.getAvailableCopies() > 0 && book.getStatus().equals(Book.BookStatus.BORROWED)) {
            book.setStatus(Book.BookStatus.AVAILABLE);
        }
        bookRepository.save(book);

        logger.info("Book returned successfully: returnId={}, borrowId={}, daysLate={}, fineAmount={}",
            saved.getId(), borrowRecordId, daysLate, fineAmount);

        return ReturnRecordDTO.from(saved);
    }

    /**
     * Renew a borrowed book (extend due date).
     */
    public BorrowRecordDTO renewBorrow(Long borrowRecordId) {
        logger.info("Processing renewal request: borrowRecordId={}", borrowRecordId);

        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
            .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with id: " + borrowRecordId));

        if (!borrowRecord.getStatus().equals(BorrowRecord.BorrowStatus.BORROWED)) {
            throw new IllegalStateException("Only active borrows can be renewed");
        }

        if (borrowRecord.getRenewalCount() >= 3) {
            throw new IllegalStateException("Maximum renewal limit (3) reached");
        }

        // Check if already overdue
        if (LocalDateTime.now().isAfter(borrowRecord.getDueDate())) {
            throw new IllegalStateException("Cannot renew an overdue book");
        }

        // Extend due date by 14 days
        LocalDateTime newDueDate = borrowRecord.getDueDate().plusDays(MAX_BORROW_DAYS);
        borrowRecord.setDueDate(newDueDate);
        borrowRecord.setRenewalCount(borrowRecord.getRenewalCount() + 1);

        BorrowRecord saved = borrowRecordRepository.save(borrowRecord);

        logger.info("Book renewed successfully: borrowId={}, newDueDate={}", borrowRecordId, newDueDate);

        return BorrowRecordDTO.from(saved);
    }

    /**
     * Get borrow history for a user.
     */
    @Transactional(readOnly = true)
    public Page<BorrowRecordDTO> getUserBorrowHistory(Long userId, Pageable pageable) {
        logger.info("Fetching borrow history: userId={}", userId);

        Page<BorrowRecord> records = borrowRecordRepository.findByUserId(userId, pageable);
        return records.map(BorrowRecordDTO::from);
    }

    /**
     * Get borrow history across all users for staff analytics.
     */
    @Transactional(readOnly = true)
    public Page<BorrowRecordDTO> getAllBorrowHistory(Pageable pageable) {
        logger.info("Fetching global borrow history for staff");
        return borrowRecordRepository.findAll(pageable).map(BorrowRecordDTO::from);
    }

    /**
     * Get active borrows for a user.
     */
    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getActiveBorrows(Long userId) {
        logger.info("Fetching active borrows: userId={}", userId);

        List<BorrowRecord> records = borrowRecordRepository.findActiveBorrowsByUserId(userId);
        return records.stream().map(BorrowRecordDTO::from).collect(Collectors.toList());
    }

    /**
     * Get overdue records.
     */
    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getOverdueRecords() {
        logger.info("Fetching overdue records");

        List<BorrowRecord> records = borrowRecordRepository.findOverdueRecords();

        // Update overdue flag
        records.forEach(br -> br.setIsOverdue(true));

        return records.stream().map(BorrowRecordDTO::from).collect(Collectors.toList());
    }

    /**
     * Get active borrowed inventory for librarian/admin oversight.
     */
    @Transactional(readOnly = true)
    public Page<BorrowRecordDTO> getBorrowedInventory(Pageable pageable) {
        logger.info("Fetching borrowed inventory");
        return borrowRecordRepository.findBorrowedInventory(pageable).map(BorrowRecordDTO::from);
    }

    /**
     * Get return history for a user.
     */
    @Transactional(readOnly = true)
    public Page<ReturnRecordDTO> getUserReturnHistory(Long userId, Pageable pageable) {
        logger.info("Fetching return history: userId={}", userId);

        Page<ReturnRecord> records = returnRecordRepository.findByUserId(userId, pageable);
        return records.map(ReturnRecordDTO::from);
    }

    /**
     * Get return history across all users for staff analytics.
     */
    @Transactional(readOnly = true)
    public Page<ReturnRecordDTO> getAllReturnHistory(Pageable pageable) {
        logger.info("Fetching global return history for staff");
        return returnRecordRepository.findAll(pageable).map(ReturnRecordDTO::from);
    }

    /**
     * Pay a fine for a return record.
     */
    public ReturnRecordDTO payFine(Long returnRecordId) {
        logger.info("Processing fine payment: returnRecordId={}", returnRecordId);

        ReturnRecord returnRecord = returnRecordRepository.findById(returnRecordId)
            .orElseThrow(() -> new ResourceNotFoundException("Return record not found with id: " + returnRecordId));

        if (returnRecord.getFinePaid()) {
            throw new IllegalStateException("Fine has already been paid");
        }

        if (returnRecord.getFineAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("No fine to pay");
        }

        returnRecord.setFinePaid(true);
        returnRecord.setFinePaidDate(LocalDateTime.now());

        ReturnRecord saved = returnRecordRepository.save(returnRecord);

        logger.info("Fine paid successfully: returnId={}, amount={}", returnRecordId, returnRecord.getFineAmount());

        return ReturnRecordDTO.from(saved);
    }

    /**
     * Get total unpaid fines for a user.
     */
    @Transactional(readOnly = true)
    public BigDecimal getUserTotalUnpaidFines(Long userId) {
        logger.info("Calculating total unpaid fines: userId={}", userId);

        BigDecimal total = returnRecordRepository.calculateTotalUnpaidFines(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

}
