package com.library.controller;
import com.library.dto.DashboardDTO;
import com.library.entity.User;
import com.library.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for dashboard (Phase 7).
 */
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard Management", description = "APIs for admin dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        DashboardDTO dashboard = dashboardService.getUserDashboard(userId);
        return ResponseEntity.ok(dashboard);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update dashboard")
    public ResponseEntity<DashboardDTO> updateDashboard(
        @RequestBody String widgetConfig,
        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        DashboardDTO dashboard = dashboardService.updateDashboard(userId, widgetConfig);
        return ResponseEntity.ok(dashboard);
    }
}
