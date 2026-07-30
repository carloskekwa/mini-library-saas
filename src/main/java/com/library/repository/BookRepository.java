package com.library.repository;

import com.library.entity.Book;
import com.library.entity.Book.BookStatus;
import com.library.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Book repository for database operations on Book entity with advanced search.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find book by ISBN.
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Check if ISBN exists.
     */
    boolean existsByIsbn(String isbn);

    /**
     * Find books by category.
     */
    List<Book> findByCategory(Category category);

    /**
     * Find books by status.
     */
    List<Book> findByStatus(BookStatus status);

    /**
     * Find available books (with pagination).
     */
    Page<Book> findByStatus(BookStatus status, Pageable pageable);

    /**
     * Search books by title (case-insensitive).
     */
    Page<Book> findByTitleIgnoreCaseContaining(String title, Pageable pageable);

    /**
     * Search books by author (case-insensitive).
     */
    Page<Book> findByAuthorIgnoreCaseContaining(String author, Pageable pageable);

    /**
     * Advanced search combining title, author, and description.
     */
    @Query("SELECT DISTINCT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Book> search(@Param("query") String query, Pageable pageable);

    /**
     * Search by category and keyword.
     */
    @Query("SELECT b FROM Book b WHERE b.category.id = :categoryId AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> searchByCategory(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable);

    /**
     * Find recently added books.
     */
    @Query(value = "SELECT b FROM Book b ORDER BY b.createdAt DESC")
    Page<Book> findRecentBooks(Pageable pageable);

}
