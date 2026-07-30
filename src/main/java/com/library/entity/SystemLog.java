package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for system logs (Phase 7).
 */
@Entity
@Table(name = "system_logs", indexes = @Index(name = "idx_timestamp", columnList = "timestamp"))
public class SystemLog {
    public enum LogLevel { INFO, WARNING, ERROR, DEBUG }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private LogLevel level;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "source", length = 100)
    private String source;

    public SystemLog() {}
    public SystemLog(LogLevel level, String message, String source) {
        this.level = level;
        this.message = message;
        this.source = source;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LogLevel getLevel() { return level; }
    public void setLevel(LogLevel level) { this.level = level; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
