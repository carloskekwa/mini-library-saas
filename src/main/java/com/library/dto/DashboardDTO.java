package com.library.dto;
import com.library.entity.Dashboard;
import java.time.LocalDateTime;

public class DashboardDTO {
    private Long id;
    private Long userId;
    private String widgetConfig;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DashboardDTO() {}
    public DashboardDTO(Long id, Long userId, String widgetConfig, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.widgetConfig = widgetConfig;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static DashboardDTO from(Dashboard dashboard) {
        return new DashboardDTO(dashboard.getId(), dashboard.getUser().getId(), dashboard.getWidgetConfig(), dashboard.getCreatedAt(), dashboard.getUpdatedAt());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getWidgetConfig() { return widgetConfig; }
    public void setWidgetConfig(String widgetConfig) { this.widgetConfig = widgetConfig; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
