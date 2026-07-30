package com.library.dto;
import com.library.entity.BookRequest;
import java.time.LocalDateTime;

public class BookRequestDTO {
    private Long id;
    private Long userId;
    private String bookTitle;
    private String author;
    private String isbn;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    public BookRequestDTO() {}
    public BookRequestDTO(Long id, Long userId, String bookTitle, String author, String isbn, String status, LocalDateTime requestedAt, LocalDateTime processedAt) {
        this.id = id;
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    public static BookRequestDTO from(BookRequest request) {
        return new BookRequestDTO(request.getId(), request.getUser().getId(), request.getBookTitle(), request.getAuthor(), request.getIsbn(), request.getStatus().name(), request.getRequestedAt(), request.getProcessedAt());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
