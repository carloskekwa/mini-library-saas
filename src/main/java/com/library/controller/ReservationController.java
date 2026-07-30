package com.library.controller;

import com.library.dto.CreateReservationRequest;
import com.library.dto.ReservationDTO;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing book reservations.
 */
@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation Management", description = "APIs for managing book reservations")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Constructor with dependency injection
     */
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Reserve a book.
     * POST /api/reservations
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Reserve a book", description = "Reserve a book for current user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Book reserved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or book unavailable"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<ReservationDTO> reserveBook(
        @Valid @RequestBody CreateReservationRequest request,
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        
        ReservationDTO reservation = reservationService.reserveBook(userId, request.getBookId());
        return ResponseEntity.status(201).body(reservation);
    }

    /**
     * Cancel a reservation.
     * DELETE /api/reservations/{reservationId}
     */
    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Cancel reservation", description = "Cancel a reservation")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reservation cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel reservation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<Void> cancelReservation(
        @PathVariable Long reservationId) {
        
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get reservations for current user.
     * GET /api/reservations
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get user reservations", description = "Retrieve reservations for current user")
    @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
    public ResponseEntity<Page<ReservationDTO>> getUserReservations(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(page, pageSize);
        
        Page<ReservationDTO> reservations = reservationService.getUserReservations(userId, pageable);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Get active reservations for current user.
     * GET /api/reservations/active
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get active reservations", description = "Retrieve active reservations for current user")
    @ApiResponse(responseCode = "200", description = "Active reservations retrieved successfully")
    public ResponseEntity<List<ReservationDTO>> getActiveReservations(
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        
        List<ReservationDTO> activeReservations = reservationService.getActiveReservations(userId);
        return ResponseEntity.ok(activeReservations);
    }

    /**
     * Get reservation queue for a book.
     * GET /api/reservations/book/{bookId}/queue
     */
    @GetMapping("/book/{bookId}/queue")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get book reservation queue", description = "Get reservation queue for a book (admin/librarian only)")
    @ApiResponse(responseCode = "200", description = "Reservation queue retrieved successfully")
    public ResponseEntity<List<ReservationDTO>> getBookReservationQueue(
        @PathVariable Long bookId) {
        
        List<ReservationDTO> queue = reservationService.getBookReservationQueue(bookId);
        return ResponseEntity.ok(queue);
    }

    /**
     * Get all reservations for staff inventory view.
     * GET /api/reservations/all
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get all reservations", description = "Paginated reservation inventory for librarian/admin")
    @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
    public ResponseEntity<Page<ReservationDTO>> getAllReservations(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ReservationDTO> reservations = reservationService.getAllReservations(status, pageable);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Fulfill a reservation (mark as available for pickup).
     * PUT /api/reservations/{reservationId}/fulfill
     */
    @PutMapping("/{reservationId}/fulfill")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Fulfill reservation", description = "Mark reservation as available for pickup (admin/librarian only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservation fulfilled successfully"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<?> fulfillReservation(
        @PathVariable Long reservationId) {

        try {
            ReservationDTO reservation = reservationService.fulfillReservation(reservationId);
            return ResponseEntity.ok(reservation);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
    }

}
