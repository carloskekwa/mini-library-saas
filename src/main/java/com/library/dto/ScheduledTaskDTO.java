package com.library.dto;
import com.library.entity.ScheduledTask;
import java.time.LocalDateTime;

public class ScheduledTaskDTO {
    private Long id;
    private String taskName;
    private String description;
    private String status;
    private String cronExpression;
    private LocalDateTime lastExecution;
    private LocalDateTime nextExecution;
    private Boolean isActive;

    public ScheduledTaskDTO() {}
    public ScheduledTaskDTO(Long id, String taskName, String description, String status, String cronExpression, LocalDateTime lastExecution, LocalDateTime nextExecution, Boolean isActive) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.cronExpression = cronExpression;
        this.lastExecution = lastExecution;
        this.nextExecution = nextExecution;
        this.isActive = isActive;
    }

    public static ScheduledTaskDTO from(ScheduledTask task) {
        return new ScheduledTaskDTO(task.getId(), task.getTaskName(), task.getDescription(), task.getStatus().name(), task.getCronExpression(), task.getLastExecution(), task.getNextExecution(), task.getIsActive());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    public LocalDateTime getLastExecution() { return lastExecution; }
    public void setLastExecution(LocalDateTime lastExecution) { this.lastExecution = lastExecution; }
    public LocalDateTime getNextExecution() { return nextExecution; }
    public void setNextExecution(LocalDateTime nextExecution) { this.nextExecution = nextExecution; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
