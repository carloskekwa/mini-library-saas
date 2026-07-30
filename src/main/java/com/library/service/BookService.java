package com.library.service;

import com.library.dto.BookSearchRequest;
import com.library.entity.Book;
import com.library.entity.Book.BookStatus;
import com.library.entity.Category;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for book management operations.
 * Handles book CRUD, search, and availability management.
 */
@Service
@Transactional
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Get all books (paginated).
     */
    public Page<Book> getAllBooks(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return bookRepository.findAll(pageable);
    }

    /**
     * Get book by ID.
     */
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
    }

    /**
     * Get book by ISBN.
     */
    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
    }

    /**
     * Get books by category.
     */
    public List<Book> getBooksByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return bookRepository.findByCategory(category);
    }

    /**
     * Get available books (paginated).
     */
    public Page<Book> getAvailableBooks(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return bookRepository.findByStatus(BookStatus.AVAILABLE, pageable);
    }

    /**
     * Create a new book.
     */
    public Book createBook(String title, String author, Long categoryId, Integer totalCopies, Integer availableCopies) {
        // Validate category exists
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        Book book = new Book(title, author, category);
        int resolvedTotalCopies = totalCopies != null ? totalCopies : 1;
        int resolvedAvailableCopies = availableCopies != null ? availableCopies : resolvedTotalCopies;

        if (resolvedAvailableCopies > resolvedTotalCopies) {
            throw new IllegalStateException("Available copies cannot be greater than total copies");
        }

        book.setTotalCopies(resolvedTotalCopies);
        book.setAvailableCopies(resolvedAvailableCopies);
        syncAvailabilityStatus(book);

        Book savedBook = bookRepository.save(book);
        logger.info("Book created successfully: {} by {}", title, author);
        return savedBook;
    }

    /**
     * Update book information.
     */
    public Book updateBook(Long bookId, String title, String author, Long categoryId, String isbn, String publisher,
                          Integer publicationYear, String description, String language,
                          String shelfLocation, Integer totalCopies, Integer availableCopies, String coverImageUrl) {
        Book book = getBookById(bookId);

        int resolvedTotalCopies = totalCopies != null ? totalCopies : book.getTotalCopies();
        int resolvedAvailableCopies = availableCopies != null ? availableCopies : book.getAvailableCopies();

        if (resolvedAvailableCopies > resolvedTotalCopies) {
            throw new IllegalStateException("Available copies cannot be greater than total copies");
        }

        if (title != null) {
            book.setTitle(title);
        }
        if (author != null) {
            book.setAuthor(author);
        }
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
            book.setCategory(category);
        }
        if (isbn != null) {
            book.setIsbn(isbn);
        }
        if (publisher != null) {
            book.setPublisher(publisher);
        }
        if (publicationYear != null) {
            book.setPublicationYear(publicationYear);
        }
        if (description != null) {
            book.setDescription(description);
        }
        if (language != null) {
            book.setLanguage(language);
        }
        if (shelfLocation != null) {
            book.setShelfLocation(shelfLocation);
        }
        if (totalCopies != null) {
            book.setTotalCopies(totalCopies);
        }
        if (availableCopies != null) {
            book.setAvailableCopies(availableCopies);
        }
        if (coverImageUrl != null) {
            book.setCoverImageUrl(coverImageUrl);
        }

        syncAvailabilityStatus(book);

        Book updatedBook = bookRepository.save(book);
        logger.info("Book updated successfully: {}", book.getTitle());
        return updatedBook;
    }

    /**
     * Delete book.
     */
    public void deleteBook(Long bookId) {
        Book book = getBookById(bookId);
        bookRepository.delete(book);
        logger.info("Book deleted: {}", book.getTitle());
    }

    /**
     * Search books with advanced criteria.
     */
    public Page<Book> search(BookSearchRequest searchRequest) {
        Pageable pageable = createPageable(searchRequest.getPage(), searchRequest.getPageSize(), 
                                          searchRequest.getSortBy(), searchRequest.getSortDirection());

        if (searchRequest.getQuery() != null && !searchRequest.getQuery().isEmpty()) {
            return bookRepository.search(searchRequest.getQuery(), pageable);
        }

        return bookRepository.findAll(pageable);
    }

    /**
     * Search books by category and keyword.
     */
    public Page<Book> searchByCategory(Long categoryId, String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return bookRepository.searchByCategory(categoryId, keyword, pageable);
    }

    /**
     * Search books by title.
     */
    public Page<Book> searchByTitle(String title, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return bookRepository.findByTitleIgnoreCaseContaining(title, pageable);
    }

    /**
     * Search books by author.
     */
    public Page<Book> searchByAuthor(String author, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return bookRepository.findByAuthorIgnoreCaseContaining(author, pageable);
    }

    /**
     * Search books by title or author.
     */
    public Page<Book> searchByTitleOrAuthor(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return bookRepository.findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(keyword, keyword, pageable);
    }

    /**
     * Get recently added books.
     */
    public Page<Book> getRecentBooks(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return bookRepository.findRecentBooks(pageable);
    }

    /**
     * Decrement available copies when book is borrowed.
     */
    public void decrementAvailableCopies(Long bookId) {
        Book book = getBookById(bookId);
        if (book.getAvailableCopies() > 0) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            if (book.getAvailableCopies() == 0) {
                book.setStatus(BookStatus.BORROWED);
            }
            bookRepository.save(book);
            logger.info("Available copies decremented for book: {}", book.getTitle());
        } else {
            throw new IllegalStateException("No available copies for book: " + book.getTitle());
        }
    }

    /**
     * Increment available copies when book is returned.
     */
    public void incrementAvailableCopies(Long bookId) {
        Book book = getBookById(bookId);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        if (book.getAvailableCopies() > 0 && book.getStatus() == BookStatus.BORROWED) {
            book.setStatus(BookStatus.AVAILABLE);
        }
        bookRepository.save(book);
        logger.info("Available copies incremented for book: {}", book.getTitle());
    }

    /**
     * Create pageable object from search parameters.
     */
    private Pageable createPageable(Integer page, Integer pageSize, String sortBy, String sortDirection) {
        int pageNum = page != null ? page : 0;
        int size = pageSize != null ? pageSize : 20;
        String by = sortBy != null && !sortBy.isEmpty() ? sortBy : "title";
        Sort.Direction direction = sortDirection != null && sortDirection.equalsIgnoreCase("DESC") 
            ? Sort.Direction.DESC : Sort.Direction.ASC;

        return PageRequest.of(pageNum, size, Sort.by(direction, by));
    }

    private void syncAvailabilityStatus(Book book) {
        Integer availableCopies = book.getAvailableCopies();
        if (availableCopies == null) {
            return;
        }

        if (availableCopies <= 0) {
            book.setStatus(BookStatus.BORROWED);
            return;
        }

        if (book.getStatus() == BookStatus.BORROWED || book.getStatus() == BookStatus.OUT_OF_STOCK) {
            book.setStatus(BookStatus.AVAILABLE);
        }
    }

}
