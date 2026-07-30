package com.library.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a reservation.
 */
public class CreateReservationRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    /**
     * Constructors
     */
    public CreateReservationRequest() {
    }

    public CreateReservationRequest(Long bookId) {
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
