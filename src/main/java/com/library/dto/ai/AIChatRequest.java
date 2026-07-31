package com.library.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for the AI chat/recommendation endpoint.
 */
public class AIChatRequest {

    @NotBlank(message = "Message must not be blank")
    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;

    /** Optional: include for personalized recommendations based on borrow history. */
    private Long userId;

    public AIChatRequest() {}

    public AIChatRequest(String message, Long userId) {
        this.message = message;
        this.userId = userId;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
