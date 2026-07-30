package com.library.service;
import com.library.dto.BookRequestDTO;
import com.library.entity.BookRequest;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRequestRepository;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * Service for managing book requests (Phase 15).
 */
@Service
@Transactional
public class BookRequestService {
    private static final Logger logger = LoggerFactory.getLogger(BookRequestService.class);
    private final BookRequestRepository bookRequestRepository;
    private final UserRepository userRepository;

    public BookRequestService(BookRequestRepository bookRequestRepository, UserRepository userRepository) {
        this.bookRequestRepository = bookRequestRepository;
        this.userRepository = userRepository;
    }

    public BookRequestDTO createRequest(Long userId, String bookTitle, String author, String isbn, String justification) {
        logger.info("Creating book request: userId={}, title={}", userId, bookTitle);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BookRequest request = new BookRequest(user, bookTitle, author, isbn);
        request.setJustification(justification);
        BookRequest saved = bookRequestRepository.save(request);
        return BookRequestDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<BookRequestDTO> getPendingRequests(Pageable pageable) {
        return bookRequestRepository.findByStatus(BookRequest.RequestStatus.PENDING, pageable).map(BookRequestDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<BookRequestDTO> getUserRequests(Long userId, Pageable pageable) {
        return bookRequestRepository.findByUserId(userId, pageable).map(BookRequestDTO::from);
    }

    public void approveRequest(Long requestId) {
        logger.info("Approving request: {}", requestId);
        BookRequest request = bookRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        request.setStatus(BookRequest.RequestStatus.APPROVED);
        request.setProcessedAt(LocalDateTime.now());
        bookRequestRepository.save(request);
    }

    public void rejectRequest(Long requestId) {
        logger.info("Rejecting request: {}", requestId);
        BookRequest request = bookRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        request.setStatus(BookRequest.RequestStatus.REJECTED);
        request.setProcessedAt(LocalDateTime.now());
        bookRequestRepository.save(request);
    }
}
