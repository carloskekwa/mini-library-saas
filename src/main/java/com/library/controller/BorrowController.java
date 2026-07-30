package com.library.controller;

import com.library.dto.BorrowBookRequest;
import com.library.dto.BorrowRecordDTO;
import com.library.dto.ReturnBookRequest;
import com.library.dto.ReturnRecordDTO;
import com.library.entity.ReturnRecord;
import com.library.entity.User;
import com.library.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing book borrowing and returning operations.
 */
@RestController
@RequestMapping("/api/borrow")
@Tag(name = "Borrow Management", description = "APIs for borrowing and returning books")
public class BorrowController {

    private final BorrowService borrowService;

    /**
     * Constructor with dependency injection
     */
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * Borrow a book.
     * POST /api/borrow
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Borrow a book", description = "Member borrows a book from the library")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Book borrowed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or book not available"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Book or user not found")
    })
    public ResponseEntity<BorrowRecordDTO> borrowBook(
        @Valid @RequestBody BorrowBookRequest request,
        Authentication authentication) {
        
        // Extract user ID from authenticated User principal
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        
        BorrowRecordDTO borrowRecord = borrowService.borrowBook(userId, request.getBookId());
        return ResponseEntity.status(201).body(borrowRecord);
    }

    /**
     * Return a borrowed book.
     * POST /api/borrow/return
     */
    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Return a borrowed book", description = "Member returns a borrowed book")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book returned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid return request"),
        @ApiResponse(responseCode = "404", description = "Borrow record not found")
    })
    public ResponseEntity<ReturnRecordDTO> returnBook(
        @Valid @RequestBody ReturnBookRequest request) {
        
        ReturnRecord.BookCondition condition = ReturnRecord.BookCondition.valueOf(request.getBookCondition());
        ReturnRecordDTO returnRecord = borrowService.returnBook(
            request.getBorrowRecordId(),
            condition,
            request.getDamageNotes()
        );
        return ResponseEntity.ok(returnRecord);
    }

    /**
     * Renew a borrowed book.
     * POST /api/borrow/{borrowRecordId}/renew
     */
    @PostMapping("/{borrowRecordId}/renew")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Renew a borrowed book", description = "Extend the due date of a borrowed book")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book renewed successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot renew book"),
        @ApiResponse(responseCode = "404", description = "Borrow record not found")
    })
    public ResponseEntity<BorrowRecordDTO> renewBorrow(
        @PathVariable Long borrowRecordId) {
        
        BorrowRecordDTO borrowRecord = borrowService.renewBorrow(borrowRecordId);
        return ResponseEntity.ok(borrowRecord);
    }

    /**
     * Approve pending borrow demand.
     * POST /api/borrow/{borrowRecordId}/approve
     */
    @PostMapping("/{borrowRecordId}/approve")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Approve borrow demand", description = "Approve a pending borrow demand and convert it to active borrow")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Borrow demand approved successfully"),
        @ApiResponse(responseCode = "400", description = "Borrow demand cannot be approved"),
        @ApiResponse(responseCode = "404", description = "Borrow record not found")
    })
    public ResponseEntity<BorrowRecordDTO> approveBorrowDemand(
        @PathVariable Long borrowRecordId) {

        BorrowRecordDTO borrowRecord = borrowService.approveBorrowDemand(borrowRecordId);
        return ResponseEntity.ok(borrowRecord);
    }

    /**
     * Reject pending borrow demand.
     * POST /api/borrow/{borrowRecordId}/reject
     */
    @PostMapping("/{borrowRecordId}/reject")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Reject borrow demand", description = "Reject a pending borrow demand")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Borrow demand rejected successfully"),
        @ApiResponse(responseCode = "400", description = "Borrow demand cannot be rejected"),
        @ApiResponse(responseCode = "404", description = "Borrow record not found")
    })
    public ResponseEntity<BorrowRecordDTO> rejectBorrowDemand(
        @PathVariable Long borrowRecordId) {

        BorrowRecordDTO borrowRecord = borrowService.rejectBorrowDemand(borrowRecordId);
        return ResponseEntity.ok(borrowRecord);
    }

    /**
     * Get borrow history for current user.
     * GET /api/borrow/history
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get borrow history", description = "Retrieve borrow history for current user")
    @ApiResponse(responseCode = "200", description = "Borrow history retrieved successfully")
    public ResponseEntity<Page<BorrowRecordDTO>> getBorrowHistory(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(page, pageSize);
        
        Page<BorrowRecordDTO> history = borrowService.getUserBorrowHistory(userId, pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * Get borrow history across all users for staff analytics.
     * GET /api/borrow/history/all
     */
    @GetMapping("/history/all")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get global borrow history", description = "Retrieve borrow history across all users (admin/librarian only)")
    @ApiResponse(responseCode = "200", description = "Global borrow history retrieved successfully")
    public ResponseEntity<Page<BorrowRecordDTO>> getAllBorrowHistory(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<BorrowRecordDTO> history = borrowService.getAllBorrowHistory(pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * Get active borrows for current user.
     * GET /api/borrow/active
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get active borrows", description = "Retrieve currently active borrows for current user")
    @ApiResponse(responseCode = "200", description = "Active borrows retrieved successfully")
    public ResponseEntity<List<BorrowRecordDTO>> getActiveBorrows(
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        
        List<BorrowRecordDTO> activeBorrows = borrowService.getActiveBorrows(userId);
        return ResponseEntity.ok(activeBorrows);
    }

    /**
     * Get overdue records.
     * GET /api/borrow/overdue
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get overdue records", description = "Retrieve all overdue borrow records (admin/librarian only)")
    @ApiResponse(responseCode = "200", description = "Overdue records retrieved successfully")
    public ResponseEntity<List<BorrowRecordDTO>> getOverdueRecords() {
        List<BorrowRecordDTO> overdueRecords = borrowService.getOverdueRecords();
        return ResponseEntity.ok(overdueRecords);
    }

    /**
     * Get active borrowed inventory for staff.
     * GET /api/borrow/inventory
     */
    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get borrowed inventory", description = "Retrieve active borrowed/overdue records across users")
    @ApiResponse(responseCode = "200", description = "Borrowed inventory retrieved successfully")
    public ResponseEntity<Page<BorrowRecordDTO>> getBorrowedInventory(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<BorrowRecordDTO> inventory = borrowService.getBorrowedInventory(pageable);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Get return history for current user.
     * GET /api/borrow/returns
     */
    @GetMapping("/returns")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get return history", description = "Retrieve return history for current user")
    @ApiResponse(responseCode = "200", description = "Return history retrieved successfully")
    public ResponseEntity<Page<ReturnRecordDTO>> getReturnHistory(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(page, pageSize);
        
        Page<ReturnRecordDTO> history = borrowService.getUserReturnHistory(userId, pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * Get return history across all users for staff analytics.
     * GET /api/borrow/returns/all
     */
    @GetMapping("/returns/all")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get global return history", description = "Retrieve return history across all users (admin/librarian only)")
    @ApiResponse(responseCode = "200", description = "Global return history retrieved successfully")
    public ResponseEntity<Page<ReturnRecordDTO>> getAllReturnHistory(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ReturnRecordDTO> history = borrowService.getAllReturnHistory(pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * Pay a fine.
     * POST /api/borrow/fines/{returnRecordId}/pay
     */
    @PostMapping("/fines/{returnRecordId}/pay")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Pay a fine", description = "Pay fine for overdue or damaged book")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fine paid successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid fine payment request"),
        @ApiResponse(responseCode = "404", description = "Return record not found")
    })
    public ResponseEntity<ReturnRecordDTO> payFine(
        @PathVariable Long returnRecordId) {
        
        ReturnRecordDTO returnRecord = borrowService.payFine(returnRecordId);
        return ResponseEntity.ok(returnRecord);
    }

    /**
     * Get total unpaid fines for current user.
     * GET /api/borrow/fines/total
     */
    @GetMapping("/fines/total")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get total unpaid fines", description = "Retrieve total unpaid fines for current user")
    @ApiResponse(responseCode = "200", description = "Total fines retrieved successfully")
    public ResponseEntity<Map<String, Object>> getTotalUnpaidFines(
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        BigDecimal totalFines = borrowService.getUserTotalUnpaidFines(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("totalUnpaidFines", totalFines);
        
        return ResponseEntity.ok(response);
    }

}
