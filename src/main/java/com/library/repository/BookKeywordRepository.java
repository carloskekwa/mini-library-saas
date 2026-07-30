package com.library.repository;

import com.library.entity.BookKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BookKeyword repository for database operations on BookKeyword entity.
 */
@Repository
public interface BookKeywordRepository extends JpaRepository<BookKeyword, Long> {

    /**
     * Find keywords by book ID.
     */
    List<BookKeyword> findByBookId(Long bookId);

    /**
     * Delete all keywords for a book.
     */
    void deleteByBookId(Long bookId);

}
