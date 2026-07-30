package com.library.dto;
import com.library.entity.EmailTemplate;
import java.time.LocalDateTime;

public class EmailTemplateDTO {
    private Long id;
    private String type;
    private String name;
    private String subject;
    private String body;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public EmailTemplateDTO() {}
    public EmailTemplateDTO(Long id, String type, String name, String subject, String body, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.subject = subject;
        this.body = body;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public static EmailTemplateDTO from(EmailTemplate template) {
        return new EmailTemplateDTO(template.getId(), template.getType().name(), template.getName(), template.getSubject(), template.getBody(), template.getIsActive(), template.getCreatedAt());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
