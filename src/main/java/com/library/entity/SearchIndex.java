package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for advanced search indexing (Phase 9).
 */
@Entity
@Table(name = "search_indices", indexes = @Index(name = "idx_keywords", columnList = "keywords"))
public class SearchIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;

    @Column(name = "relevance_score")
    private Double relevanceScore = 0.0;

    @Column(name = "indexed_at", nullable = false, updatable = false)
    private LocalDateTime indexedAt;

    public SearchIndex() {}
    public SearchIndex(Book book, String keywords) {
        this.book = book;
        this.keywords = keywords;
        this.indexedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    public Double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(Double relevanceScore) { this.relevanceScore = relevanceScore; }
    public LocalDateTime getIndexedAt() { return indexedAt; }
    public void setIndexedAt(LocalDateTime indexedAt) { this.indexedAt = indexedAt; }
}
