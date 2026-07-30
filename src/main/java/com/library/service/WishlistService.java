package com.library.service;
import com.library.dto.WishlistDTO;
import com.library.entity.Wishlist;
import com.library.entity.User;
import com.library.entity.Book;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.WishlistRepository;
import com.library.repository.UserRepository;
import com.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing wishlist (Phase 11).
 */
@Service
@Transactional
public class WishlistService {
    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public WishlistService(WishlistRepository wishlistRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public WishlistDTO addToWishlist(Long userId, Long bookId) {
        logger.info("Adding to wishlist: userId={}, bookId={}", userId, bookId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (wishlistRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new IllegalStateException("Book already in wishlist");
        }

        Wishlist wishlist = new Wishlist(user, book);
        Wishlist saved = wishlistRepository.save(wishlist);
        return WishlistDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<WishlistDTO> getUserWishlist(Long userId, Pageable pageable) {
        return wishlistRepository.findByUserId(userId, pageable).map(WishlistDTO::from);
    }

    public void removeFromWishlist(Long wishlistId) {
        logger.info("Removing from wishlist: {}", wishlistId);
        wishlistRepository.deleteById(wishlistId);
    }
}
