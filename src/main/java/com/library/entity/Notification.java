package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for notifications (Phase 5 - updated).
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_is_read", columnList = "is_read")
})
public class Notification {
    public enum NotificationType {
        BOOK_AVAILABLE, OVERDUE_REMINDER, FINE_NOTIFICATION, BORROW_CONFIRMATION,
        RETURN_CONFIRMATION, RESERVATION_CONFIRMATION, SYSTEM_ALERT, PENALTY_APPLIED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "related_book_id")
    private Long relatedBookId;

    @Column(name = "related_borrow_id")
    private Long relatedBorrowId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public Notification() {}
    public Notification(User user, NotificationType type, String title, String message) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public Long getRelatedBookId() { return relatedBookId; }
    public void setRelatedBookId(Long relatedBookId) { this.relatedBookId = relatedBookId; }
    public Long getRelatedBorrowId() { return relatedBorrowId; }
    public void setRelatedBorrowId(Long relatedBorrowId) { this.relatedBorrowId = relatedBorrowId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
