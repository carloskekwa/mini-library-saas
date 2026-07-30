package com.library.dto;
import com.library.entity.Review;
import java.time.LocalDateTime;

public class ReviewDTO {
    private Long id;
    private Long bookId;
    private Long userId;
    private Integer rating;
    private String reviewText;
    private Integer helpfulCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewDTO() {}
    public ReviewDTO(Long id, Long bookId, Long userId, Integer rating, String reviewText, Integer helpfulCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.helpfulCount = helpfulCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReviewDTO from(Review review) {
        return new ReviewDTO(review.getId(), review.getBook().getId(), review.getUser().getId(), review.getRating(), review.getReviewText(), review.getHelpfulCount(), review.getCreatedAt(), review.getUpdatedAt());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public Integer getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(Integer helpfulCount) { this.helpfulCount = helpfulCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
