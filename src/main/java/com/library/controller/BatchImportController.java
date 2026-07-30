package com.library.controller;
import com.library.dto.BatchImportJobDTO;
import com.library.entity.User;
import com.library.service.BatchImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for batch operations (Phase 16).
 */
@RestController
@RequestMapping("/api/batch-import")
@Tag(name = "Batch Import Management", description = "APIs for batch import operations")
public class BatchImportController {
    private final BatchImportService batchImportService;

    public BatchImportController(BatchImportService batchImportService) {
        this.batchImportService = batchImportService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Create import job")
    public ResponseEntity<BatchImportJobDTO> createImportJob(
        @RequestParam String filePath,
        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        BatchImportJobDTO job = batchImportService.createImportJob(userId, filePath);
        return ResponseEntity.status(201).body(job);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get import jobs")
    public ResponseEntity<Page<BatchImportJobDTO>> getUserJobs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<BatchImportJobDTO> jobs = batchImportService.getUserJobs(userId, pageable);
        return ResponseEntity.ok(jobs);
    }

    @PostMapping("/{jobId}/start")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Start import")
    public ResponseEntity<Void> startImport(@PathVariable Long jobId) {
        batchImportService.startImport(jobId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobId}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Complete import")
    public ResponseEntity<Void> completeImport(@PathVariable Long jobId) {
        batchImportService.completeImport(jobId);
        return ResponseEntity.noContent().build();
    }
}
