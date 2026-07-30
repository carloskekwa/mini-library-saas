package com.library.dto;

import com.library.entity.Notification;

import java.time.LocalDateTime;

/**
 * DTO for notification response.
 */
public class NotificationDTO {

    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String message;
    private Boolean isRead;
    private Long relatedBookId;
    private Long relatedBorrowId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    /**
     * Constructors
     */
    public NotificationDTO() {
    }

    public NotificationDTO(Long id, Long userId, String type, String title, String message,
                          Boolean isRead, Long relatedBookId, Long relatedBorrowId,
                          LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.relatedBookId = relatedBookId;
        this.relatedBorrowId = relatedBorrowId;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    /**
     * Convert Notification entity to NotificationDTO.
     */
    public static NotificationDTO from(Notification notification) {
        return new NotificationDTO(
            notification.getId(),
            notification.getUser().getId(),
            notification.getType().name(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getIsRead(),
            notification.getRelatedBookId(),
            notification.getRelatedBorrowId(),
            notification.getCreatedAt(),
            notification.getReadAt()
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Long getRelatedBookId() {
        return relatedBookId;
    }

    public void setRelatedBookId(Long relatedBookId) {
        this.relatedBookId = relatedBookId;
    }

    public Long getRelatedBorrowId() {
        return relatedBorrowId;
    }

    public void setRelatedBorrowId(Long relatedBorrowId) {
        this.relatedBorrowId = relatedBorrowId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

}
