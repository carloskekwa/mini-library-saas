package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for API rate limiting (Phase 12).
 */
@Entity
@Table(name = "api_rate_limits", indexes = @Index(name = "idx_user_id", columnList = "user_id"))
public class ApiRateLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "endpoint", length = 255)
    private String endpoint;

    @Column(name = "request_count", nullable = false)
    private Integer requestCount = 0;

    @Column(name = "limit", nullable = false)
    private Integer limit = 100;

    @Column(name = "reset_at")
    private LocalDateTime resetAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ApiRateLimit() {}
    public ApiRateLimit(User user, String endpoint, Integer limit) {
        this.user = user;
        this.endpoint = endpoint;
        this.limit = limit;
        this.requestCount = 0;
        this.createdAt = LocalDateTime.now();
        this.resetAt = LocalDateTime.now().plusHours(1);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public Integer getRequestCount() { return requestCount; }
    public void setRequestCount(Integer requestCount) { this.requestCount = requestCount; }
    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
    public LocalDateTime getResetAt() { return resetAt; }
    public void setResetAt(LocalDateTime resetAt) { this.resetAt = resetAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
