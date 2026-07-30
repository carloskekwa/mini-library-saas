package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for scheduled tasks (Phase 17).
 */
@Entity
@Table(name = "scheduled_tasks", indexes = @Index(name = "idx_task_name", columnList = "task_name"))
public class ScheduledTask {
    public enum TaskStatus { PENDING, RUNNING, COMPLETED, FAILED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", length = 100, nullable = false, unique = true)
    private String taskName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "cron_expression", length = 100)
    private String cronExpression;

    @Column(name = "last_execution")
    private LocalDateTime lastExecution;

    @Column(name = "next_execution")
    private LocalDateTime nextExecution;

    @Column(name = "execution_log", columnDefinition = "TEXT")
    private String executionLog;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ScheduledTask() {}
    public ScheduledTask(String taskName, String description, String cronExpression) {
        this.taskName = taskName;
        this.description = description;
        this.cronExpression = cronExpression;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    public LocalDateTime getLastExecution() { return lastExecution; }
    public void setLastExecution(LocalDateTime lastExecution) { this.lastExecution = lastExecution; }
    public LocalDateTime getNextExecution() { return nextExecution; }
    public void setNextExecution(LocalDateTime nextExecution) { this.nextExecution = nextExecution; }
    public String getExecutionLog() { return executionLog; }
    public void setExecutionLog(String executionLog) { this.executionLog = executionLog; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
