package com.library.dto;
import com.library.entity.Penalty;
import java.time.LocalDateTime;

public class PenaltyDTO {
    private Long id;
    private Long userId;
    private String type;
    private String reason;
    private String status;
    private LocalDateTime suspendedUntil;
    private LocalDateTime createdAt;

    public PenaltyDTO() {}
    public PenaltyDTO(Long id, Long userId, String type, String reason, String status, LocalDateTime suspendedUntil, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.reason = reason;
        this.status = status;
        this.suspendedUntil = suspendedUntil;
        this.createdAt = createdAt;
    }

    public static PenaltyDTO from(Penalty penalty) {
        return new PenaltyDTO(penalty.getId(), penalty.getUser().getId(), penalty.getType().name(), penalty.getReason(), penalty.getStatus().name(), penalty.getSuspendedUntil(), penalty.getCreatedAt());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSuspendedUntil() { return suspendedUntil; }
    public void setSuspendedUntil(LocalDateTime suspendedUntil) { this.suspendedUntil = suspendedUntil; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
