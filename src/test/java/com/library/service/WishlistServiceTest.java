package com.library.service;
import com.library.dto.WishlistDTO;
import com.library.entity.Wishlist;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.repository.WishlistRepository;
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
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {
    @Mock
    private WishlistRepository wishlistRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    
    private WishlistService wishlistService;

    @BeforeEach
    void setUp() {
        wishlistService = new WishlistService(wishlistRepository, userRepository, bookRepository);
    }

    @Test
    void testAddToWishlist() {
        Book book = new Book("Test Book", "Author");
        User user = new User("user1", "user1@email.com", "hashed");
        Wishlist wishlist = new Wishlist(user, book);
        wishlist.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(wishlistRepository.existsByUserIdAndBookId(1L, 1L)).thenReturn(false);
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        WishlistDTO result = wishlistService.addToWishlist(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
    }

    @Test
    void testGetUserWishlist() {
        Book book = new Book("Test Book", "Author");
        User user = new User("user1", "user1@email.com", "hashed");
        Wishlist wishlist = new Wishlist(user, book);
        wishlist.setId(1L);
        Page<Wishlist> page = new PageImpl<>(Arrays.asList(wishlist));

        when(wishlistRepository.findByUserId(1L, PageRequest.of(0, 20))).thenReturn(page);

        Page<WishlistDTO> result = wishlistService.getUserWishlist(1L, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
    }

    @Test
    void testRemoveFromWishlist() {
        wishlistService.removeFromWishlist(1L);
        verify(wishlistRepository, times(1)).deleteById(1L);
    }
}
