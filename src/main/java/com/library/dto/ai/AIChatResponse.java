package com.library.dto.ai;

import java.util.List;

/**
 * Response DTO for the AI chat/recommendation endpoint.
 * Contains the LLM-generated narrative answer plus structured book cards.
 */
public class AIChatResponse {

    /** LLM-generated recommendation narrative. */
    private String answer;

    /** Structured book cards retrieved from the vector store (for UI rendering). */
    private List<BookRecommendation> books;

    public AIChatResponse() {}

    public AIChatResponse(String answer, List<BookRecommendation> books) {
        this.answer = answer;
        this.books = books;
    }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<BookRecommendation> getBooks() { return books; }
    public void setBooks(List<BookRecommendation> books) { this.books = books; }
}
