package com.library.repository;

import com.library.entity.BorrowRecord;
import com.library.entity.BorrowRecord.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * BorrowRecord repository for database operations on BorrowRecord entity.
 */
@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    /**
     * Find borrow records by user ID.
     */
    List<BorrowRecord> findByUserId(Long userId);

    /**
     * Find borrow records by user ID (paginated).
     */
    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);

    /**
     * Find borrow records by book ID.
     */
    List<BorrowRecord> findByBookId(Long bookId);

    /**
     * Find borrow records by status.
     */
    List<BorrowRecord> findByStatus(BorrowStatus status);

    /**
     * Find borrow records by status (paginated).
     */
    Page<BorrowRecord> findByStatus(BorrowStatus status, Pageable pageable);

    /**
     * Find overdue borrow records.
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < CURRENT_TIMESTAMP")
    List<BorrowRecord> findOverdueRecords();

    /**
     * Find active borrowed inventory for staff oversight.
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.status IN ('PENDING', 'BORROWED', 'OVERDUE')")
    Page<BorrowRecord> findBorrowedInventory(Pageable pageable);

    /**
     * Find pending borrow requests.
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'PENDING'")
    Page<BorrowRecord> findPendingBorrowRequests(Pageable pageable);

    /**
     * Find active borrows for a user (not yet returned).
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND br.status IN ('PENDING', 'BORROWED', 'OVERDUE')")
    List<BorrowRecord> findActiveBorrowsByUserId(@Param("userId") Long userId);

    /**
     * Find active borrow by user and book.
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND br.book.id = :bookId AND br.status IN ('PENDING', 'BORROWED', 'OVERDUE')")
    Optional<BorrowRecord> findActiveByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * Count active borrows for a user.
     */
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.user.id = :userId AND br.status IN ('PENDING', 'BORROWED', 'OVERDUE')")
    Integer countActiveBorrows(@Param("userId") Long userId);

}
