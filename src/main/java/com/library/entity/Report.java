package com.library.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Report entity for analytics and statistics (Phase 6).
 */
@Entity
@Table(name = "reports", indexes = {
    @Index(name = "idx_report_type", columnList = "report_type"),
    @Index(name = "idx_report_generated_date", columnList = "generated_date")
})
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 50)
    private ReportType type;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime generatedDate = LocalDateTime.now();

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    public enum ReportType {
        POPULAR_BOOKS, USER_ACTIVITY, CIRCULATION_STATS, OVERDUE_SUMMARY, FINE_SUMMARY, MEMBERSHIP_GROWTH, RESERVATION_TRENDS, SYSTEM_PERFORMANCE
    }

    public Report() {}
    public Report(ReportType type, String content) {
        this.type = type;
        this.content = content;
        this.generatedDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ReportType getType() { return type; }
    public void setType(ReportType type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}
