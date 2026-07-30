package com.library.dto;
import com.library.entity.Report;
import java.time.LocalDateTime;

public class ReportDTO {
    private Long id;
    private String type;
    private String content;
    private LocalDateTime generatedDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public ReportDTO() {}
    public ReportDTO(Long id, String type, String content, LocalDateTime generatedDate, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.generatedDate = generatedDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static ReportDTO from(Report report) {
        return new ReportDTO(report.getId(), report.getType().name(), report.getContent(), report.getGeneratedDate(), report.getStartDate(), report.getEndDate());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}
