package com.library.repository;

import com.library.entity.ReturnRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ReturnRecord repository for database operations on ReturnRecord entity.
 */
@Repository
public interface ReturnRecordRepository extends JpaRepository<ReturnRecord, Long> {

    /**
     * Find return records by user ID.
     */
    List<ReturnRecord> findByUserId(Long userId);

    /**
     * Find return records by user ID (paginated).
     */
    Page<ReturnRecord> findByUserId(Long userId, Pageable pageable);

    /**
     * Find return record by borrow record ID.
     */
    Optional<ReturnRecord> findByBorrowRecordId(Long borrowRecordId);

    /**
     * Find unpaid fines for a user.
     */
    @Query("SELECT rr FROM ReturnRecord rr WHERE rr.user.id = :userId AND rr.finePaid = false AND rr.fineAmount > 0")
    List<ReturnRecord> findUnpaidFinesByUserId(@Param("userId") Long userId);

    /**
     * Calculate total unpaid fines for a user.
     */
    @Query("SELECT SUM(rr.fineAmount) FROM ReturnRecord rr WHERE rr.user.id = :userId AND rr.finePaid = false")
    java.math.BigDecimal calculateTotalUnpaidFines(@Param("userId") Long userId);

    /**
     * Find return records with late returns.
     */
    @Query("SELECT rr FROM ReturnRecord rr WHERE rr.daysLate > 0")
    Page<ReturnRecord> findLateReturns(Pageable pageable);

    /**
     * Find damaged books.
     */
    @Query("SELECT rr FROM ReturnRecord rr WHERE rr.bookCondition IN ('POOR', 'DAMAGED')")
    List<ReturnRecord> findDamagedBooks();

}
