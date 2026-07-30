package com.library.repository;

import com.library.entity.Reservation;
import com.library.entity.Reservation.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Reservation repository for database operations on Reservation entity.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Find reservations by user ID.
     */
    List<Reservation> findByUserId(Long userId);

    /**
     * Find reservations by user ID (paginated).
     */
    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    /**
     * Find active reservations for a user.
     */
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.status IN ('PENDING', 'NOTIFIED')")
    List<Reservation> findActiveByUserId(@Param("userId") Long userId);

    /**
     * Find reservations by book ID.
     */
    List<Reservation> findByBookId(Long bookId);

    /**
     * Find active reservations by book ID ordered by queue position.
     */
    @Query("SELECT r FROM Reservation r WHERE r.book.id = :bookId AND r.status IN ('PENDING', 'NOTIFIED') ORDER BY r.positionInQueue ASC")
    List<Reservation> findActiveByBookIdOrdered(@Param("bookId") Long bookId);

    /**
     * Find if user already reserved a book.
     */
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.book.id = :bookId AND r.status IN ('PENDING', 'NOTIFIED')")
    Optional<Reservation> findActiveByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * Count active reservations for a user.
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId AND r.status IN ('PENDING', 'NOTIFIED')")
    Integer countActiveByUserId(@Param("userId") Long userId);

    /**
     * Find reservations by status.
     */
    List<Reservation> findByStatus(ReservationStatus status);

    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

    /**
     * Find expired reservations.
     */
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.expiryDate < CURRENT_TIMESTAMP")
    List<Reservation> findExpiredReservations();

}
