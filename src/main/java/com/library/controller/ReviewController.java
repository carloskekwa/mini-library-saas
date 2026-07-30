package com.library.controller;
import com.library.dto.ReviewDTO;
import com.library.entity.User;
import com.library.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for reviews (Phase 10).
 */
@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Management", description = "APIs for book reviews and ratings")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Create review")
    public ResponseEntity<ReviewDTO> createReview(
        @RequestParam Long bookId,
        @RequestParam @Min(1) @Max(5) Integer rating,
        @RequestParam(required = false) String reviewText,
        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        ReviewDTO review = reviewService.createReview(bookId, userId, rating, reviewText);
        return ResponseEntity.status(201).body(review);
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get book reviews")
    public ResponseEntity<Page<ReviewDTO>> getBookReviews(
        @PathVariable Long bookId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ReviewDTO> reviews = reviewService.getBookReviews(bookId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/book/{bookId}/rating")
    @Operation(summary = "Get average rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long bookId) {
        Double rating = reviewService.getBookAverageRating(bookId);
        return ResponseEntity.ok(rating);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    @Operation(summary = "Delete review")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
