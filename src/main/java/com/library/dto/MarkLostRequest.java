package com.library.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for marking a borrowed book as lost and applying a fine.
 */
public class MarkLostRequest {

    @NotNull(message = "Fine amount is required")
    @DecimalMin(value = "0.01", message = "Fine amount must be greater than zero")
    private BigDecimal fineAmount;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    public MarkLostRequest() {
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}