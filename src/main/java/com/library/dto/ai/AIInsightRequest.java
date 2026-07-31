package com.library.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for the librarian AI insights endpoint.
 */
public class AIInsightRequest {

    @NotBlank(message = "Question must not be blank")
    @Size(max = 500, message = "Question must not exceed 500 characters")
    private String question;

    public AIInsightRequest() {}

    public AIInsightRequest(String question) {
        this.question = question;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}
