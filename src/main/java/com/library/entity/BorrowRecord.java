package com.library.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * BorrowRecord entity representing a book borrow transaction.
 * Tracks who borrowed what book and when.
 */
@Entity
@Table(name = "borrow_records", indexes = {
    @Index(name = "idx_borrow_user_id", columnList = "user_id"),
    @Index(name = "idx_borrow_book_id", columnList = "book_id"),
    @Index(name = "idx_borrow_status", columnList = "status")
})
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private LocalDateTime borrowDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BorrowStatus status = BorrowStatus.BORROWED;

    @Column(name = "is_overdue")
    private Boolean isOverdue = false;

    @Column(name = "renewal_count")
    private Integer renewalCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * BorrowRecord status enumeration
     */
    public enum BorrowStatus {
        PENDING,
        REJECTED,
        BORROWED,
        RETURNED,
        OVERDUE,
        LOST
    }

    /**
     * Constructors
     */
    public BorrowRecord() {
    }

    public BorrowRecord(User user, Book book, LocalDateTime borrowDate, LocalDateTime dueDate) {
        this.user = user;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = BorrowStatus.BORROWED;
        this.isOverdue = false;
        this.renewalCount = 0;
        this.createdAt = LocalDateTime.now();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
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

    public BorrowStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowStatus status) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
