package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Wishlist entity for user favorites (Phase 11).
 */
@Entity
@Table(name = "wishlists", indexes = {
    @Index(name = "idx_wishlist_user_id", columnList = "user_id"),
    @Index(name = "idx_wishlist_book_id", columnList = "book_id")
})
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, updatable = false)
    private LocalDateTime addedAt = LocalDateTime.now();

    public Wishlist() {}
    public Wishlist(User user, Book book) {
        this.user = user;
        this.book = book;
        this.addedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
