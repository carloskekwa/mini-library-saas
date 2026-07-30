package com.library.service;
import com.library.dto.ReviewDTO;
import com.library.entity.Review;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.ReviewRepository;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing book reviews (Phase 10).
 */
@Service
@Transactional
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public ReviewDTO createReview(Long bookId, Long userId, Integer rating, String reviewText) {
        logger.info("Creating review: bookId={}, userId={}, rating={}", bookId, userId, rating);
        
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = new Review(book, user, rating, reviewText);
        Review saved = reviewRepository.save(review);
        return ReviewDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDTO> getBookReviews(Long bookId, Pageable pageable) {
        return reviewRepository.findByBookId(bookId, pageable).map(ReviewDTO::from);
    }

    @Transactional(readOnly = true)
    public Double getBookAverageRating(Long bookId) {
        return reviewRepository.findAverageRatingByBookId(bookId);
    }

    public void deleteReview(Long reviewId) {
        logger.info("Deleting review: {}", reviewId);
        reviewRepository.deleteById(reviewId);
    }
}
