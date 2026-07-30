package com.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing book.
 */
public class UpdateBookRequest {

    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    private String author;

    @Size(max = 255, message = "Publisher must be at most 255 characters")
    private String publisher;

    @Min(value = 1000, message = "Publication year must be 1000 or later")
    @Max(value = 2100, message = "Publication year must be 2100 or earlier")
    private Integer publicationYear;

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    @Size(max = 50, message = "Language must be at most 50 characters")
    private String language;

    @Size(max = 255, message = "Shelf location must be at most 255 characters")
    private String shelfLocation;

    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;

    @Min(value = 0, message = "Available copies cannot be negative")
    private Integer availableCopies;

    @Size(max = 500, message = "Cover image URL must be at most 500 characters")
    private String coverImageUrl;

    private String status;

    /**
     * Constructors
     */
    public UpdateBookRequest() {
    }

    /**
     * Getters and Setters
     */
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

}
