package com.library.service;
import com.library.dto.ReviewDTO;
import com.library.entity.Review;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.repository.ReviewRepository;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, bookRepository, userRepository);
    }

    @Test
    void testCreateReview() {
        Book book = new Book("Test Book", "Author");
        User user = new User("user1", "user1@email.com", "hashed");
        Review review = new Review(book, user, 5, "Great book");
        review.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDTO result = reviewService.createReview(1L, 1L, 5, "Great book");

        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testGetBookReviews() {
        Book book = new Book("Test Book", "Author");
        User user = new User("user1", "user1@email.com", "hashed");
        Review review = new Review(book, user, 4, "Good");
        review.setId(1L);
        Page<Review> page = new PageImpl<>(Arrays.asList(review));

        when(reviewRepository.findByBookId(1L, PageRequest.of(0, 10))).thenReturn(page);

        Page<ReviewDTO> result = reviewService.getBookReviews(1L, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(reviewRepository, times(1)).findByBookId(1L, PageRequest.of(0, 10));
    }

    @Test
    void testGetBookAverageRating() {
        when(reviewRepository.findAverageRatingByBookId(1L)).thenReturn(4.5);

        Double result = reviewService.getBookAverageRating(1L);

        assertEquals(4.5, result);
    }
}
