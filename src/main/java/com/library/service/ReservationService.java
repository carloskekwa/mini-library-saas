package com.library.service;

import com.library.dto.ReservationDTO;
import com.library.entity.*;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing book reservations.
 */
@Service
@Transactional
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final BorrowService borrowService;

    private static final int MAX_ACTIVE_RESERVATIONS = 10;
    private static final int RESERVATION_EXPIRY_DAYS = 30;

    /**
     * Constructor with dependency injection
     */
    public ReservationService(ReservationRepository reservationRepository,
                              BookRepository bookRepository,
                              UserRepository userRepository,
                              NotificationService notificationService,
                              BorrowService borrowService) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.borrowService = borrowService;
    }

    /**
     * Reserve a book for a user.
     */
    public ReservationDTO reserveBook(Long userId, Long bookId) {
        logger.info("Processing reservation: userId={}, bookId={}", userId, bookId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // Check if user already has active reservation for this book
        if (reservationRepository.findActiveByUserAndBook(userId, bookId).isPresent()) {
            throw new IllegalStateException("User already has an active reservation for this book");
        }

        // Check active reservation limit
        Integer activeReservations = reservationRepository.countActiveByUserId(userId);
        if (activeReservations >= MAX_ACTIVE_RESERVATIONS) {
            throw new IllegalStateException("Maximum reservation limit (" + MAX_ACTIVE_RESERVATIONS + ") reached");
        }

        // Get next position in queue
        List<Reservation> activeReservations_list = reservationRepository.findActiveByBookIdOrdered(bookId);
        Integer positionInQueue = activeReservations_list.size() + 1;

        // Create reservation
        LocalDateTime reservationDate = LocalDateTime.now();
        LocalDateTime expiryDate = reservationDate.plusDays(RESERVATION_EXPIRY_DAYS);

        Reservation reservation = new Reservation(user, book);
        reservation.setReservationDate(reservationDate);
        reservation.setExpiryDate(expiryDate);
        reservation.setPositionInQueue(positionInQueue);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        Reservation saved = reservationRepository.save(reservation);

        // Create notification
        String message = "You have reserved '" + book.getTitle() + "'. Position in queue: " + positionInQueue;
        notificationService.createNotificationWithRelated(
            userId,
            Notification.NotificationType.RESERVATION_CONFIRMATION,
            "Book Reserved",
            message,
            bookId,
            null
        );

        logger.info("Book reserved successfully: reservationId={}, bookId={}, userId={}, position={}",
            saved.getId(), bookId, userId, positionInQueue);

        return ReservationDTO.from(saved);
    }

    /**
     * Cancel a reservation.
     */
    public void cancelReservation(Long reservationId) {
        logger.info("Processing reservation cancellation: reservationId={}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        if (!reservation.getStatus().equals(Reservation.ReservationStatus.PENDING) &&
            !reservation.getStatus().equals(Reservation.ReservationStatus.NOTIFIED)) {
            throw new IllegalStateException("Cannot cancel reservation with status: " + reservation.getStatus());
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Reorder remaining reservations
        updateQueuePositions(reservation.getBook().getId());

        logger.info("Reservation cancelled: reservationId={}", reservationId);
    }

    /**
     * Mark reservation as fulfilled (book is now available for pickup).
     */
    public ReservationDTO fulfillReservation(Long reservationId) {
        logger.info("Fulfilling reservation: reservationId={}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        if (!reservation.getStatus().equals(Reservation.ReservationStatus.PENDING) &&
            !reservation.getStatus().equals(Reservation.ReservationStatus.NOTIFIED)) {
            throw new IllegalStateException("Only active reservations can be fulfilled. Current status: " + reservation.getStatus());
        }

        // Convert reservation to an actual borrow transaction for the reserved member.
        borrowService.borrowBook(reservation.getUser().getId(), reservation.getBook().getId());

        reservation.setStatus(Reservation.ReservationStatus.FULFILLED);
        Reservation saved = reservationRepository.save(reservation);

        // Send notification to user
        String message = "Your reserved book '" + reservation.getBook().getTitle() + "' is now available for pickup";
        notificationService.createNotificationWithRelated(
            reservation.getUser().getId(),
            Notification.NotificationType.BOOK_AVAILABLE,
            "Book Available",
            message,
            reservation.getBook().getId(),
            null
        );

        // Promote next reservation
        promoteNextReservation(reservation.getBook().getId());

        logger.info("Reservation fulfilled: reservationId={}", reservationId);

        return ReservationDTO.from(saved);
    }

    /**
     * Get reservations for a user.
     */
    @Transactional(readOnly = true)    public Page<ReservationDTO> getAllReservations(String statusFilter, Pageable pageable) {
        logger.info("Fetching all reservations for staff, statusFilter={}", statusFilter);
        if (statusFilter != null && !statusFilter.isBlank()) {
            Reservation.ReservationStatus status = Reservation.ReservationStatus.valueOf(statusFilter.toUpperCase());
            return reservationRepository.findByStatus(status, pageable).map(ReservationDTO::from);
        }
        return reservationRepository.findAll(pageable).map(ReservationDTO::from);
    }

    @Transactional(readOnly = true)    public Page<ReservationDTO> getUserReservations(Long userId, Pageable pageable) {
        logger.info("Fetching reservations: userId={}", userId);

        Page<Reservation> reservations = reservationRepository.findByUserId(userId, pageable);
        return reservations.map(ReservationDTO::from);
    }

    /**
     * Get active reservations for a user.
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getActiveReservations(Long userId) {
        logger.info("Fetching active reservations: userId={}", userId);

        List<Reservation> reservations = reservationRepository.findActiveByUserId(userId);
        return reservations.stream().map(ReservationDTO::from).collect(Collectors.toList());
    }

    /**
     * Get reservations queue for a book.
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getBookReservationQueue(Long bookId) {
        logger.info("Fetching reservation queue: bookId={}", bookId);

        List<Reservation> reservations = reservationRepository.findActiveByBookIdOrdered(bookId);
        return reservations.stream().map(ReservationDTO::from).collect(Collectors.toList());
    }

    /**
     * Check and mark expired reservations.
     */
    @Transactional
    public void expireExpiredReservations() {
        logger.info("Checking for expired reservations");

        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations();

        for (Reservation reservation : expiredReservations) {
            reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
        }

        reservationRepository.saveAll(expiredReservations);

        logger.info("Expired {} reservations", expiredReservations.size());
    }

    /**
     * Update queue positions after cancellation.
     */
    private void updateQueuePositions(Long bookId) {
        List<Reservation> activeReservations = reservationRepository.findActiveByBookIdOrdered(bookId);

        for (int i = 0; i < activeReservations.size(); i++) {
            activeReservations.get(i).setPositionInQueue(i + 1);
        }

        reservationRepository.saveAll(activeReservations);
    }

    /**
     * Promote next reservation in queue when a book becomes available.
     */
    private void promoteNextReservation(Long bookId) {
        List<Reservation> activeReservations = reservationRepository.findActiveByBookIdOrdered(bookId);

        if (!activeReservations.isEmpty()) {
            Reservation nextReservation = activeReservations.get(0);
            nextReservation.setStatus(Reservation.ReservationStatus.NOTIFIED);
            nextReservation.setNotificationSent(true);
            reservationRepository.save(nextReservation);

            logger.info("Next reservation promoted: reservationId={}", nextReservation.getId());
        }
    }

}
