package com.library.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * BookSummary entity for storing generated book summaries.
 * One summary per book.
 */
@Entity
@Table(name = "book_summaries", indexes = {
    @Index(name = "idx_book_summary_book_id", columnList = "book_id")
})
public class BookSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, unique = true)
    private Book book;

    @Column(name = "summary_text", columnDefinition = "LONGTEXT")
    private String summaryText;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    /**
     * Constructors
     */
    public BookSummary() {
    }

    public BookSummary(Book book, String summaryText) {
        this.book = book;
        this.summaryText = summaryText;
        this.generatedAt = LocalDateTime.now();
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

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

}
