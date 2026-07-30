package com.library.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for returning a book.
 */
public class ReturnBookRequest {

    @NotNull(message = "Borrow record ID is required")
    private Long borrowRecordId;

    @NotNull(message = "Book condition is required")
    private String bookCondition;

    @Size(max = 500, message = "Damage notes must be at most 500 characters")
    private String damageNotes;

    /**
     * Constructors
     */
    public ReturnBookRequest() {
    }

    public ReturnBookRequest(Long borrowRecordId, String bookCondition) {
        this.borrowRecordId = borrowRecordId;
        this.bookCondition = bookCondition;
    }

    /**
     * Getters and Setters
     */
    public Long getBorrowRecordId() {
        return borrowRecordId;
    }

    public void setBorrowRecordId(Long borrowRecordId) {
        this.borrowRecordId = borrowRecordId;
    }

    public String getBookCondition() {
        return bookCondition;
    }

    public void setBookCondition(String bookCondition) {
        this.bookCondition = bookCondition;
    }

    public String getDamageNotes() {
        return damageNotes;
    }

    public void setDamageNotes(String damageNotes) {
        this.damageNotes = damageNotes;
    }

}
