package com.library.dto;
import com.library.entity.Wishlist;
import java.time.LocalDateTime;

public class WishlistDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime addedAt;

    public WishlistDTO() {}
    public WishlistDTO(Long id, Long userId, Long bookId, String bookTitle, LocalDateTime addedAt) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.addedAt = addedAt;
    }

    public static WishlistDTO from(Wishlist wishlist) {
        return new WishlistDTO(wishlist.getId(), wishlist.getUser().getId(), wishlist.getBook().getId(), wishlist.getBook().getTitle(), wishlist.getAddedAt());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
