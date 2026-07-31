package com.library.dto.ai;

/**
 * Response DTO for the librarian AI insights endpoint.
 */
public class AIInsightResponse {

    /** LLM-generated analysis text. */
    private String analysis;

    /** ISO-8601 timestamp when the analysis was generated. */
    private String generatedAt;

    public AIInsightResponse() {}

    public AIInsightResponse(String analysis, String generatedAt) {
        this.analysis = analysis;
        this.generatedAt = generatedAt;
    }

    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }

    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
}
