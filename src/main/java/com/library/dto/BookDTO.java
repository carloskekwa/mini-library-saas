package com.library.dto;

import com.library.entity.Book;

import java.time.LocalDateTime;

/**
 * DTO for book response (excludes sensitive fields).
 */
public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private CategoryDTO category;
    private String publisher;
    private Integer publicationYear;
    private String description;
    private String language;
    private String shelfLocation;
    private Integer totalCopies;
    private Integer availableCopies;
    private String coverImageUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructors
     */
    public BookDTO() {
    }

    public BookDTO(Long id, String title, String author, String isbn, CategoryDTO category,
                   String publisher, Integer publicationYear, String description, String language,
                   String shelfLocation, Integer totalCopies, Integer availableCopies,
                   String coverImageUrl, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.description = description;
        this.language = language;
        this.shelfLocation = shelfLocation;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.coverImageUrl = coverImageUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Convert Book entity to BookDTO.
     */
    public static BookDTO from(Book book) {
        return new BookDTO(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn(),
            CategoryDTO.from(book.getCategory()),
            book.getPublisher(),
            book.getPublicationYear(),
            book.getDescription(),
            book.getLanguage(),
            book.getShelfLocation(),
            book.getTotalCopies(),
            book.getAvailableCopies(),
            book.getCoverImageUrl(),
            book.getStatus().name(),
            book.getCreatedAt(),
            book.getUpdatedAt()
        );
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
