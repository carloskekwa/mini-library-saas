package com.library.controller;
import com.library.dto.PenaltyDTO;
import com.library.entity.Penalty;
import com.library.service.PenaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for penalties (Phase 14).
 */
@RestController
@RequestMapping("/api/penalties")
@Tag(name = "Penalty Management", description = "APIs for managing member penalties")
public class PenaltyController {
    private final PenaltyService penaltyService;

    public PenaltyController(PenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Create penalty")
    public ResponseEntity<PenaltyDTO> createPenalty(
        @RequestParam Long userId,
        @RequestParam Penalty.PenaltyType type,
        @RequestParam String reason) {
        PenaltyDTO penalty = penaltyService.createPenalty(userId, type, reason);
        return ResponseEntity.status(201).body(penalty);
    }

    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get user active penalties")
    public ResponseEntity<List<PenaltyDTO>> getUserActivePenalties(@PathVariable Long userId) {
        List<PenaltyDTO> penalties = penaltyService.getUserActivePenalties(userId);
        return ResponseEntity.ok(penalties);
    }

    @PutMapping("/{penaltyId}/lift")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lift penalty")
    public ResponseEntity<Void> liftPenalty(@PathVariable Long penaltyId) {
        penaltyService.liftPenalty(penaltyId);
        return ResponseEntity.noContent().build();
    }
}
