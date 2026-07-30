package com.library.controller;
import com.library.dto.WishlistDTO;
import com.library.entity.User;
import com.library.service.WishlistService;
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
 * REST controller for wishlist (Phase 11).
 */
@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist Management", description = "APIs for managing book wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Add to wishlist")
    public ResponseEntity<WishlistDTO> addToWishlist(
        @RequestParam Long bookId,
        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        WishlistDTO wishlist = wishlistService.addToWishlist(userId, bookId);
        return ResponseEntity.status(201).body(wishlist);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get user wishlist")
    public ResponseEntity<Page<WishlistDTO>> getUserWishlist(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<WishlistDTO> wishlist = wishlistService.getUserWishlist(userId, pageable);
        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping("/{wishlistId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Remove from wishlist")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long wishlistId) {
        wishlistService.removeFromWishlist(wishlistId);
        return ResponseEntity.noContent().build();
    }
}
