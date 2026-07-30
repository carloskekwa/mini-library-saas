package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for admin dashboard (Phase 7).
 */
@Entity
@Table(name = "dashboards", indexes = @Index(name = "idx_user_id", columnList = "user_id"))
public class Dashboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "widget_config", columnDefinition = "TEXT")
    private String widgetConfig;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Dashboard() {}
    public Dashboard(User user, String widgetConfig) {
        this.user = user;
        this.widgetConfig = widgetConfig;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getWidgetConfig() { return widgetConfig; }
    public void setWidgetConfig(String widgetConfig) { this.widgetConfig = widgetConfig; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
