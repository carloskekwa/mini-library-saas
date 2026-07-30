package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Penalty entity for member penalties and suspension (Phase 14).
 */
@Entity
@Table(name = "penalties", indexes = {
    @Index(name = "idx_penalty_user_id", columnList = "user_id"),
    @Index(name = "idx_penalty_status", columnList = "status")
})
public class Penalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PenaltyType type;

    @Column(nullable = false, length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PenaltyStatus status = PenaltyStatus.ACTIVE;

    @Column(name = "suspended_until")
    private LocalDateTime suspendedUntil;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum PenaltyType { SUSPENSION, WARNING, FINE_DEFAULT, DAMAGE_CHARGE }
    public enum PenaltyStatus { ACTIVE, LIFTED, EXPIRED }

    public Penalty() {}
    public Penalty(User user, PenaltyType type, String reason) {
        this.user = user;
        this.type = type;
        this.reason = reason;
        this.status = PenaltyStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public PenaltyType getType() { return type; }
    public void setType(PenaltyType type) { this.type = type; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public PenaltyStatus getStatus() { return status; }
    public void setStatus(PenaltyStatus status) { this.status = status; }
    public LocalDateTime getSuspendedUntil() { return suspendedUntil; }
    public void setSuspendedUntil(LocalDateTime suspendedUntil) { this.suspendedUntil = suspendedUntil; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
