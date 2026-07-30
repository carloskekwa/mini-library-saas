package com.library.controller;
import com.library.dto.ReportDTO;
import com.library.entity.Report;
import com.library.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

/**
 * REST controller for reports (Phase 6).
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Report Management", description = "APIs for analytics and reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/popular-books")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Generate popular books report")
    public ResponseEntity<ReportDTO> generatePopularBooksReport() {
        ReportDTO report = reportService.generatePopularBooksReport();
        return ResponseEntity.status(201).body(report);
    }

    @PostMapping("/circulation-stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Generate circulation stats report")
    public ResponseEntity<ReportDTO> generateCirculationStats(
        @RequestParam LocalDateTime start,
        @RequestParam LocalDateTime end) {
        ReportDTO report = reportService.generateCirculationStatsReport(start, end);
        return ResponseEntity.status(201).body(report);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get all reports")
    public ResponseEntity<Page<ReportDTO>> getAllReports(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ReportDTO> reports = reportService.getAllReports(pageable);
        return ResponseEntity.ok(reports);
    }

    @DeleteMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete report")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}
