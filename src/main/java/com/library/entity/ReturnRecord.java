package com.library.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ReturnRecord entity representing a book return transaction.
 * Tracks condition of returned book and any fines.
 */
@Entity
@Table(name = "return_records", indexes = {
    @Index(name = "idx_return_borrow_id", columnList = "borrow_id"),
    @Index(name = "idx_return_user_id", columnList = "user_id")
})
public class ReturnRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_id", nullable = false, unique = true)
    private BorrowRecord borrowRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BookCondition bookCondition = BookCondition.GOOD;

    @Column(length = 500)
    private String damageNotes;

    @Column(name = "days_late")
    private Integer daysLate = 0;

    @Column(name = "fine_amount", precision = 10, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @Column(name = "fine_paid")
    private Boolean finePaid = false;

    @Column(name = "fine_paid_date")
    private LocalDateTime finePaidDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * BookCondition enumeration
     */
    public enum BookCondition {
        EXCELLENT,
        GOOD,
        FAIR,
        POOR,
        DAMAGED
    }

    /**
     * Constructors
     */
    public ReturnRecord() {
    }

    public ReturnRecord(BorrowRecord borrowRecord, User user, LocalDateTime returnDate) {
        this.borrowRecord = borrowRecord;
        this.user = user;
        this.returnDate = returnDate;
        this.bookCondition = BookCondition.GOOD;
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

    public BorrowRecord getBorrowRecord() {
        return borrowRecord;
    }

    public void setBorrowRecord(BorrowRecord borrowRecord) {
        this.borrowRecord = borrowRecord;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public BookCondition getBookCondition() {
        return bookCondition;
    }

    public void setBookCondition(BookCondition bookCondition) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
