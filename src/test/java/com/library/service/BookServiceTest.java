package com.library.service;

import com.library.entity.Book;
import com.library.entity.Book.BookStatus;
import com.library.entity.Category;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookService.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Fiction", "Fiction books");
        testCategory.setId(1L);

        testBook = new Book("The Great Gatsby", "F. Scott Fitzgerald", testCategory);
        testBook.setId(1L);
        testBook.setIsbn("978-0-7432-7356-5");
        testBook.setStatus(BookStatus.AVAILABLE);
    }

    @Test
    void testGetBookByIdSuccess() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // Act
        Book result = bookService.getBookById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("The Great Gatsby", result.getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void testGetBookByIdThrowsNotFoundException() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(999L));
    }

    @Test
    void testGetBookByIsbnSuccess() {
        // Arrange
        when(bookRepository.findByIsbn("978-0-7432-7356-5")).thenReturn(Optional.of(testBook));

        // Act
        Book result = bookService.getBookByIsbn("978-0-7432-7356-5");

        // Assert
        assertNotNull(result);
        assertEquals("The Great Gatsby", result.getTitle());
        verify(bookRepository).findByIsbn("978-0-7432-7356-5");
    }

    @Test
    void testCreateBookSuccess() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        Book result = bookService.createBook("The Great Gatsby", "F. Scott Fitzgerald", 1L, 5, 4);

        // Assert
        assertNotNull(result);
        assertEquals("The Great Gatsby", result.getTitle());
        verify(categoryRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testCreateBookWithInvalidCategoryThrowsException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> bookService.createBook("Test Book", "Test Author", 999L, 1, 1));
    }

    @Test
    void testCreateBookThrowsExceptionWhenAvailableExceedsTotal() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act & Assert
        assertThrows(IllegalStateException.class,
            () -> bookService.createBook("Test Book", "Test Author", 1L, 2, 3));
    }

    @Test
    void testUpdateBookSuccess() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        Book result = bookService.updateBook(1L, "New Title", "New Author", "Publisher", 2024, 
                                            "Description", "English", "Shelf A", 10, 8, "URL");

        // Assert
        assertNotNull(result);
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testUpdateBookThrowsExceptionWhenAvailableExceedsTotal() {
        // Arrange
        testBook.setTotalCopies(5);
        testBook.setAvailableCopies(5);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // Act & Assert
        assertThrows(IllegalStateException.class,
            () -> bookService.updateBook(1L, null, null, null, null,
                null, null, null, 4, 6, null));
    }

    @Test
    void testDecrementAvailableCopiesSuccess() {
        // Arrange
        testBook.setAvailableCopies(5);
        testBook.setStatus(BookStatus.AVAILABLE);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        bookService.decrementAvailableCopies(1L);

        // Assert
        assertEquals(4, testBook.getAvailableCopies());
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testDecrementAvailableCopiesThrowsExceptionWhenZero() {
        // Arrange
        testBook.setAvailableCopies(0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> bookService.decrementAvailableCopies(1L));
    }

    @Test
    void testIncrementAvailableCopiesSuccess() {
        // Arrange
        testBook.setAvailableCopies(3);
        testBook.setStatus(BookStatus.BORROWED);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        bookService.incrementAvailableCopies(1L);

        // Assert
        assertEquals(4, testBook.getAvailableCopies());
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testDeleteBookSuccess() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // Act
        bookService.deleteBook(1L);

        // Assert
        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(testBook);
    }

}
