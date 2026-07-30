package com.library.dto;
import com.library.entity.BookRequest;
import java.time.LocalDateTime;

public class BookRequestDTO {
    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private String bookTitle;
    private String author;
    private String isbn;
    private String justification;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    public BookRequestDTO() {}
    public BookRequestDTO(Long id, Long userId, String username, String userEmail, String bookTitle, String author, String isbn, String justification, String status, LocalDateTime requestedAt, LocalDateTime processedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.userEmail = userEmail;
        this.bookTitle = bookTitle;
        this.author = author;
        this.isbn = isbn;
        this.justification = justification;
        this.status = status;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    public static BookRequestDTO from(BookRequest request) {
        return new BookRequestDTO(
            request.getId(),
            request.getUser().getId(),
            request.getUser().getUsername(),
            request.getUser().getEmail(),
            request.getBookTitle(),
            request.getAuthor(),
            request.getIsbn(),
            request.getJustification(),
            request.getStatus().name(),
            request.getRequestedAt(),
            request.getProcessedAt()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
