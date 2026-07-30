package com.library.dto;

import com.library.entity.BorrowRecord;

import java.time.LocalDateTime;

/**
 * DTO for borrow record response.
 */
public class BorrowRecordDTO {

    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private Integer totalCopies;
    private Integer availableCopies;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status;
    private Boolean isOverdue;
    private Integer renewalCount;

    /**
     * Constructors
     */
    public BorrowRecordDTO() {
    }

    public BorrowRecordDTO(Long id, Long userId, String username, Long bookId, String bookTitle,
                          String bookAuthor, Integer totalCopies, Integer availableCopies,
                          LocalDateTime borrowDate, LocalDateTime dueDate, LocalDateTime returnDate,
                          String status, Boolean isOverdue, Integer renewalCount) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.isOverdue = isOverdue;
        this.renewalCount = renewalCount;
    }

    /**
     * Convert BorrowRecord entity to BorrowRecordDTO.
     */
    public static BorrowRecordDTO from(BorrowRecord borrowRecord) {
        return new BorrowRecordDTO(
            borrowRecord.getId(),
            borrowRecord.getUser().getId(),
            borrowRecord.getUser().getUsername(),
            borrowRecord.getBook().getId(),
            borrowRecord.getBook().getTitle(),
            borrowRecord.getBook().getAuthor(),
            borrowRecord.getBook().getTotalCopies(),
            borrowRecord.getBook().getAvailableCopies(),
            borrowRecord.getBorrowDate(),
            borrowRecord.getDueDate(),
            borrowRecord.getReturnDate(),
            borrowRecord.getStatus().name(),
            borrowRecord.getIsOverdue(),
            borrowRecord.getRenewalCount()
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

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public Integer getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(Integer renewalCount) {
        this.renewalCount = renewalCount;
    }

}
