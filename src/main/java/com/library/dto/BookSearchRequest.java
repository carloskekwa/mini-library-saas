package com.library.dto;

import jakarta.validation.constraints.Min;

/**
 * DTO for advanced book search requests.
 */
public class BookSearchRequest {

    private String query;
    private Long categoryId;
    private String status;

    @Min(value = 0, message = "Page number must be at least 0")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    private Integer pageSize = 20;

    private String sortBy = "title";
    private String sortDirection = "ASC";

    /**
     * Constructors
     */
    public BookSearchRequest() {
    }

    public BookSearchRequest(String query) {
        this.query = query;
    }

    /**
     * Getters and Setters
     */
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page != null ? page : 0;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize != null ? pageSize : 20;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy != null ? sortBy : "title";
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection != null ? sortDirection : "ASC";
    }

}
