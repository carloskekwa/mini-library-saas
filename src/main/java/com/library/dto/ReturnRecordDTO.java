package com.library.dto;

import com.library.entity.ReturnRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for return record response.
 */
public class ReturnRecordDTO {

    private Long id;
    private Long borrowRecordId;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime returnDate;
    private String bookCondition;
    private String damageNotes;
    private Integer daysLate;
    private BigDecimal fineAmount;
    private Boolean finePaid;
    private LocalDateTime finePaidDate;

    /**
     * Constructors
     */
    public ReturnRecordDTO() {
    }

    public ReturnRecordDTO(Long id, Long borrowRecordId, Long userId, String username, Long bookId,
                          String bookTitle, LocalDateTime returnDate, String bookCondition,
                          String damageNotes, Integer daysLate, BigDecimal fineAmount,
                          Boolean finePaid, LocalDateTime finePaidDate) {
        this.id = id;
        this.borrowRecordId = borrowRecordId;
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.returnDate = returnDate;
        this.bookCondition = bookCondition;
        this.damageNotes = damageNotes;
        this.daysLate = daysLate;
        this.fineAmount = fineAmount;
        this.finePaid = finePaid;
        this.finePaidDate = finePaidDate;
    }

    /**
     * Convert ReturnRecord entity to ReturnRecordDTO.
     */
    public static ReturnRecordDTO from(ReturnRecord returnRecord) {
        return new ReturnRecordDTO(
            returnRecord.getId(),
            returnRecord.getBorrowRecord().getId(),
            returnRecord.getUser().getId(),
            returnRecord.getUser().getUsername(),
            returnRecord.getBorrowRecord().getBook().getId(),
            returnRecord.getBorrowRecord().getBook().getTitle(),
            returnRecord.getReturnDate(),
            returnRecord.getBookCondition().name(),
            returnRecord.getDamageNotes(),
            returnRecord.getDaysLate(),
            returnRecord.getFineAmount(),
            returnRecord.getFinePaid(),
            returnRecord.getFinePaidDate()
        );
    }

    /**
     * Getters and Setters
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBorrowRecordId() {
        return borrowRecordId;
    }

    public void setBorrowRecordId(Long borrowRecordId) {
        this.borrowRecordId = borrowRecordId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
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

    public Integer getDaysLate() {
        return daysLate;
    }

    public void setDaysLate(Integer daysLate) {
        this.daysLate = daysLate;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public Boolean getFinePaid() {
        return finePaid;
    }

    public void setFinePaid(Boolean finePaid) {
        this.finePaid = finePaid;
    }

    public LocalDateTime getFinePaidDate() {
        return finePaidDate;
    }

    public void setFinePaidDate(LocalDateTime finePaidDate) {
        this.finePaidDate = finePaidDate;
    }

}
