package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * BookRequest entity for book acquisition requests (Phase 15).
 */
@Entity
@Table(name = "book_requests", indexes = {
    @Index(name = "idx_request_user_id", columnList = "user_id"),
    @Index(name = "idx_request_status", columnList = "status")
})
public class BookRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String bookTitle;

    @Column(nullable = false, length = 255)
    private String author;

    @Column(length = 20)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public enum RequestStatus { PENDING, APPROVED, REJECTED, ORDERED, FULFILLED }

    public BookRequest() {}
    public BookRequest(User user, String bookTitle, String author, String isbn) {
        this.user = user;
        this.bookTitle = bookTitle;
        this.author = author;
        this.isbn = isbn;
        this.status = RequestStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
