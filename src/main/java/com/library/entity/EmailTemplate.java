package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for email templates (Phase 8).
 */
@Entity
@Table(name = "email_templates", indexes = @Index(name = "idx_template_name", columnList = "name"))
public class EmailTemplate {
    public enum TemplateType { WELCOME, PASSWORD_RESET, OVERDUE_REMINDER, FINE_NOTICE, RESERVATION_READY }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TemplateType type;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public EmailTemplate() {}
    public EmailTemplate(TemplateType type, String name, String subject, String body) {
        this.type = type;
        this.name = name;
        this.subject = subject;
        this.body = body;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TemplateType getType() { return type; }
    public void setType(TemplateType type) { this.type = type; }
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
