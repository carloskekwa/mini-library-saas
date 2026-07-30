package com.library.dto;
import com.library.entity.SystemLog;
import java.time.LocalDateTime;

public class SystemLogDTO {
    private Long id;
    private String level;
    private String message;
    private LocalDateTime timestamp;
    private String source;

    public SystemLogDTO() {}
    public SystemLogDTO(Long id, String level, String message, LocalDateTime timestamp, String source) {
        this.id = id;
        this.level = level;
        this.message = message;
        this.timestamp = timestamp;
        this.source = source;
    }

    public static SystemLogDTO from(SystemLog log) {
        return new SystemLogDTO(log.getId(), log.getLevel().name(), log.getMessage(), log.getTimestamp(), log.getSource());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
