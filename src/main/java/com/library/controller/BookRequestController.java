package com.library.controller;
import com.library.dto.BookRequestDTO;
import com.library.entity.User;
import com.library.service.BookRequestService;
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
 * REST controller for book requests (Phase 15).
 */
@RestController
@RequestMapping("/api/book-requests")
@Tag(name = "Book Request Management", description = "APIs for book acquisition requests")
public class BookRequestController {
    private final BookRequestService bookRequestService;

    public BookRequestController(BookRequestService bookRequestService) {
        this.bookRequestService = bookRequestService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Create book request")
    public ResponseEntity<BookRequestDTO> createRequest(
        @RequestParam String bookTitle,
        @RequestParam String author,
        @RequestParam(required = false) String isbn,
        @RequestParam(required = false) String justification,
        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        BookRequestDTO request = bookRequestService.createRequest(userId, bookTitle, author, isbn, justification);
        return ResponseEntity.status(201).body(request);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get pending requests")
    public ResponseEntity<Page<BookRequestDTO>> getPendingRequests(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<BookRequestDTO> requests = bookRequestService.getPendingRequests(pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get user requests")
    public ResponseEntity<Page<BookRequestDTO>> getUserRequests(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<BookRequestDTO> requests = bookRequestService.getUserRequests(userId, pageable);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve request")
    public ResponseEntity<Void> approveRequest(@PathVariable Long requestId) {
        bookRequestService.approveRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject request")
    public ResponseEntity<Void> rejectRequest(@PathVariable Long requestId) {
        bookRequestService.rejectRequest(requestId);
        return ResponseEntity.noContent().build();
    }
}
