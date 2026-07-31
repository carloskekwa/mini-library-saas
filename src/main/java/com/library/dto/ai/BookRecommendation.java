package com.library.dto.ai;

/**
 * A single book recommendation returned as part of an AI chat response.
 */
public class BookRecommendation {

    private Long bookId;
    private String title;
    private String author;
    private String category;
    private String availability;
    private int availableCopies;

    public BookRecommendation() {}

    public BookRecommendation(Long bookId, String title, String author,
                               String category, String availability, int availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.availability = availability;
        this.availableCopies = availableCopies;
    }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
}
