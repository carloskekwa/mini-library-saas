package com.library.entity;

import jakarta.persistence.*;

/**
 * BookKeyword entity for search optimization.
 * Stores keywords extracted from book titles, authors, and descriptions.
 */
@Entity
@Table(name = "book_keywords", indexes = {
    @Index(name = "idx_book_keyword_book_id", columnList = "book_id"),
    @Index(name = "idx_book_keyword_keyword", columnList = "keyword")
})
public class BookKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, length = 255)
    private String keyword;

    /**
     * Constructors
     */
    public BookKeyword() {
    }

    public BookKeyword(Book book, String keyword) {
        this.book = book;
        this.keyword = keyword;
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

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

}
