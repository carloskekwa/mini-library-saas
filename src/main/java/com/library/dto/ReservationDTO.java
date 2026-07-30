package com.library.dto;

import com.library.entity.Reservation;

import java.time.LocalDateTime;

/**
 * DTO for reservation response.
 */
public class ReservationDTO {

    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime reservationDate;
    private LocalDateTime expiryDate;
    private String status;
    private Integer positionInQueue;
    private Boolean notificationSent;

    /**
     * Constructors
     */
    public ReservationDTO() {
    }

    public ReservationDTO(Long id, Long userId, String username, Long bookId, String bookTitle,
                         LocalDateTime reservationDate, LocalDateTime expiryDate, String status,
                         Integer positionInQueue, Boolean notificationSent) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.reservationDate = reservationDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.positionInQueue = positionInQueue;
        this.notificationSent = notificationSent;
    }

    /**
     * Convert Reservation entity to ReservationDTO.
     */
    public static ReservationDTO from(Reservation reservation) {
        return new ReservationDTO(
            reservation.getId(),
            reservation.getUser().getId(),
            reservation.getUser().getUsername(),
            reservation.getBook().getId(),
            reservation.getBook().getTitle(),
            reservation.getReservationDate(),
            reservation.getExpiryDate(),
            reservation.getStatus().name(),
            reservation.getPositionInQueue(),
            reservation.getNotificationSent()
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

}
