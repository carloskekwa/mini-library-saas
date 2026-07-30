package com.library.controller;
import com.library.dto.AuditLogDTO;
import com.library.service.AuditService;
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
 * REST controller for audit logs (Phase 13).
 */
@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Management", description = "APIs for audit trail and logging")
public class AuditLogController {
    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user audit logs")
    public ResponseEntity<Page<AuditLogDTO>> getUserAuditLogs(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<AuditLogDTO> logs = auditService.getUserAuditLogs(userId, pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by date range")
    public ResponseEntity<Page<AuditLogDTO>> getAuditLogsByDateRange(
        @RequestParam LocalDateTime start,
        @RequestParam LocalDateTime end,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<AuditLogDTO> logs = auditService.getAuditLogsByDateRange(start, end, pageable);
        return ResponseEntity.ok(logs);
    }
}
