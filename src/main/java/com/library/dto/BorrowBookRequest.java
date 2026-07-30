package com.library.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for borrowing a book.
 */
public class BorrowBookRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    /**
     * Constructors
     */
    public BorrowBookRequest() {
    }

    public BorrowBookRequest(Long bookId) {
        this.bookId = bookId;
    }

    /**
     * Getters and Setters
     */
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

}
