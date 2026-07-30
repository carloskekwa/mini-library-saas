package com.library.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Reservation entity for tracking book reservations.
 */
@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_reservation_user_id", columnList = "user_id"),
    @Index(name = "idx_reservation_book_id", columnList = "book_id"),
    @Index(name = "idx_reservation_status", columnList = "status")
})
public class Reservation {

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
    private LocalDateTime reservationDate = LocalDateTime.now();

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(name = "position_in_queue")
    private Integer positionInQueue = 1;

    @Column(name = "notification_sent")
    private Boolean notificationSent = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * ReservationStatus enumeration
     */
    public enum ReservationStatus {
        PENDING,
        NOTIFIED,
        CANCELLED,
        FULFILLED,
        EXPIRED
    }

    /**
     * Constructors
     */
    public Reservation() {
    }

    public Reservation(User user, Book book) {
        this.user = user;
        this.book = book;
        this.reservationDate = LocalDateTime.now();
        this.expiryDate = LocalDateTime.now().plusDays(30);
        this.status = ReservationStatus.PENDING;
        this.positionInQueue = 1;
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

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Integer getPositionInQueue() {
        return positionInQueue;
    }

    public void setPositionInQueue(Integer positionInQueue) {
        this.positionInQueue = positionInQueue;
    }

    public Boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
