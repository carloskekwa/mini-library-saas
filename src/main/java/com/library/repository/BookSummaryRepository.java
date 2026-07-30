package com.library.repository;

import com.library.entity.BookSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BookSummary repository for database operations on BookSummary entity.
 */
@Repository
public interface BookSummaryRepository extends JpaRepository<BookSummary, Long> {

    /**
     * Find summary by book ID.
     */
    Optional<BookSummary> findByBookId(Long bookId);

}
