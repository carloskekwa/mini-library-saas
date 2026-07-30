package com.library.controller;

import com.library.dto.BookDTO;
import com.library.dto.BookSearchRequest;
import com.library.dto.CreateBookRequest;
import com.library.dto.UpdateBookRequest;
import com.library.entity.Book;
import com.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Book REST Controller.
 * Handles book CRUD operations and advanced search.
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Book management and search endpoints")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Get all books (paginated).
     */
    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieve all books with pagination")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    public ResponseEntity<Page<BookDTO>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<Book> books = bookService.getAllBooks(page, pageSize);
        Page<BookDTO> dtos = books.map(BookDTO::from);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get book by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieve a specific book")
    @ApiResponse(responseCode = "200", description = "Book retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(BookDTO.from(book));
    }

    /**
     * Get available books only.
     */
    @GetMapping("/available")
    @Operation(summary = "Get available books", description = "Retrieve only available books")
    @ApiResponse(responseCode = "200", description = "Available books retrieved successfully")
    public ResponseEntity<Page<BookDTO>> getAvailableBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<Book> books = bookService.getAvailableBooks(page, pageSize);
        Page<BookDTO> dtos = books.map(BookDTO::from);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Search books.
     */
    @PostMapping("/search")
    @Operation(summary = "Search books", description = "Search books by criteria")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<Page<BookDTO>> search(@Valid @RequestBody BookSearchRequest searchRequest) {
        Page<Book> books = bookService.search(searchRequest);
        Page<BookDTO> dtos = books.map(BookDTO::from);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Search books by title.
     */
    @GetMapping("/search/title")
    @Operation(summary = "Search by title", description = "Search books by title")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<Page<BookDTO>> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<Book> books = bookService.searchByTitle(title, page, pageSize);
        Page<BookDTO> dtos = books.map(BookDTO::from);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Search books by author.
     */
    @GetMapping("/search/author")
    @Operation(summary = "Search by author", description = "Search books by author")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<Page<BookDTO>> searchByAuthor(
            @RequestParam String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<Book> books = bookService.searchByAuthor(author, page, pageSize);
        Page<BookDTO> dtos = books.map(BookDTO::from);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Search books by title or author.
     */
    @GetMapping("/search/title-author")
    @Operation(summary = "Search by title or author", description = "Search books by title or author")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<Page<BookDTO>> searchByTitleOrAuthor(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<Book> books = bookService.searchByTitleOrAuthor(keyword, page, pageSize);
        Page<BookDTO> dtos = books.map(BookDTO::from);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get recent books.
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent books", description = "Retrieve recently added books")
    @ApiResponse(responseCode = "200", description = "Recent books retrieved successfully")
    public ResponseEntity<Page<BookDTO>> getRecentBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Book> books = bookService.getRecentBooks(page, pageSize);
        Page<BookDTO> dtos = books.map(BookDTO::from);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Create new book (Admin/Librarian only).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Create new book", description = "Add a new book to the library")
    @ApiResponse(responseCode = "201", description = "Book created successfully",
        content = @Content(schema = @Schema(implementation = BookDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody CreateBookRequest request) {
        Book book = bookService.createBook(request.getTitle(), request.getAuthor(), 
                                          request.getCategoryId(), request.getTotalCopies(), request.getAvailableCopies());
        // Set additional fields
        book.setIsbn(request.getIsbn());
        book.setPublisher(request.getPublisher());
        book.setPublicationYear(request.getPublicationYear());
        book.setDescription(request.getDescription());
        book.setLanguage(request.getLanguage());
        book.setShelfLocation(request.getShelfLocation());
        book.setCoverImageUrl(request.getCoverImageUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(BookDTO.from(book));
    }

    /**
     * Update book (Admin/Librarian only).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Update book", description = "Update book information")
    @ApiResponse(responseCode = "200", description = "Book updated successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody UpdateBookRequest request) {
        Book book = bookService.updateBook(id, request.getTitle(), request.getAuthor(), 
                                          request.getPublisher(), request.getPublicationYear(),
                                          request.getDescription(), request.getLanguage(),
                                          request.getShelfLocation(), request.getTotalCopies(), request.getAvailableCopies(),
                                          request.getCoverImageUrl());
        return ResponseEntity.ok(BookDTO.from(book));
    }

    /**
     * Get books filtered by category (paginated).
     * GET /api/books/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get books by category", description = "Retrieve books filtered by category with optional keyword search")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    public ResponseEntity<Page<BookDTO>> getBooksByCategory(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize) {

        Page<Book> books = bookService.searchByCategory(categoryId, keyword, page, pageSize);
        return ResponseEntity.ok(books.map(BookDTO::from));
    }

    /**
     * Delete book (Admin only).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete book", description = "Delete a book from the library")
    @ApiResponse(responseCode = "204", description = "Book deleted successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

}
