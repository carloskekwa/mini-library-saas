package com.library.dto;
import com.library.entity.SearchIndex;
import java.time.LocalDateTime;

public class SearchIndexDTO {
    private Long id;
    private Long bookId;
    private String keywords;
    private Double relevanceScore;
    private LocalDateTime indexedAt;

    public SearchIndexDTO() {}
    public SearchIndexDTO(Long id, Long bookId, String keywords, Double relevanceScore, LocalDateTime indexedAt) {
        this.id = id;
        this.bookId = bookId;
        this.keywords = keywords;
        this.relevanceScore = relevanceScore;
        this.indexedAt = indexedAt;
    }

    public static SearchIndexDTO from(SearchIndex index) {
        return new SearchIndexDTO(index.getId(), index.getBook().getId(), index.getKeywords(), index.getRelevanceScore(), index.getIndexedAt());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    public Double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(Double relevanceScore) { this.relevanceScore = relevanceScore; }
    public LocalDateTime getIndexedAt() { return indexedAt; }
    public void setIndexedAt(LocalDateTime indexedAt) { this.indexedAt = indexedAt; }
}
