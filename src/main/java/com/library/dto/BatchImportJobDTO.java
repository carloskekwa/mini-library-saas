package com.library.dto;
import com.library.entity.BatchImportJob;
import java.time.LocalDateTime;

public class BatchImportJobDTO {
    private Long id;
    private Long userId;
    private String status;
    private String filePath;
    private Integer totalRecords;
    private Integer processedRecords;
    private Integer failedRecords;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public BatchImportJobDTO() {}
    public BatchImportJobDTO(Long id, Long userId, String status, String filePath, Integer totalRecords, Integer processedRecords, Integer failedRecords, LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.filePath = filePath;
        this.totalRecords = totalRecords;
        this.processedRecords = processedRecords;
        this.failedRecords = failedRecords;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public static BatchImportJobDTO from(BatchImportJob job) {
        return new BatchImportJobDTO(job.getId(), job.getUser().getId(), job.getStatus().name(), job.getFilePath(), job.getTotalRecords(), job.getProcessedRecords(), job.getFailedRecords(), job.getStartedAt(), job.getCompletedAt());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Integer getTotalRecords() { return totalRecords; }
    public void setTotalRecords(Integer totalRecords) { this.totalRecords = totalRecords; }
    public Integer getProcessedRecords() { return processedRecords; }
    public void setProcessedRecords(Integer processedRecords) { this.processedRecords = processedRecords; }
    public Integer getFailedRecords() { return failedRecords; }
    public void setFailedRecords(Integer failedRecords) { this.failedRecords = failedRecords; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
