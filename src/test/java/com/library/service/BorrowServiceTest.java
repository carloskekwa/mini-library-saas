package com.library.service;

import com.library.dto.BorrowRecordDTO;
import com.library.entity.*;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.ReturnRecordRepository;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BorrowService.
 */
@ExtendWith(MockitoExtension.class)
public class BorrowServiceTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private ReturnRecordRepository returnRecordRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    private BorrowService borrowService;

    private User testUser;
    private Book testBook;
    private BorrowRecord testBorrowRecord;

    @BeforeEach
    void setUp() {
        borrowService = new BorrowService(
            borrowRecordRepository,
            returnRecordRepository,
            bookRepository,
            userRepository
        );

        // Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setAvailableCopies(5);
        testBook.setTotalCopies(5);
        testBook.setStatus(Book.BookStatus.AVAILABLE);

        testBorrowRecord = new BorrowRecord();
        testBorrowRecord.setId(1L);
        testBorrowRecord.setUser(testUser);
        testBorrowRecord.setBook(testBook);
        testBorrowRecord.setBorrowDate(LocalDateTime.now());
        testBorrowRecord.setDueDate(LocalDateTime.now().plusDays(14));
        testBorrowRecord.setStatus(BorrowRecord.BorrowStatus.BORROWED);
    }

    @Test
    void testBorrowBookSuccess() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(borrowRecordRepository.countActiveBorrows(1L)).thenReturn(0);
        when(borrowRecordRepository.findActiveByUserAndBook(1L, 1L)).thenReturn(Optional.empty());
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(testBorrowRecord);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        BorrowRecordDTO result = borrowService.borrowBook(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("Test Book", result.getBookTitle());
        verify(borrowRecordRepository, times(1)).save(any(BorrowRecord.class));
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testBorrowBookUserNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowService.borrowBook(99L, 1L));
    }

    @Test
    void testBorrowBookNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> borrowService.borrowBook(1L, 99L));
    }

    @Test
    void testBorrowBookNotAvailable() {
        // Arrange
        testBook.setAvailableCopies(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> borrowService.borrowBook(1L, 1L));
    }

    @Test
    void testBorrowBookMaxLimitReached() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(borrowRecordRepository.countActiveBorrows(1L)).thenReturn(5); // Max is 5

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> borrowService.borrowBook(1L, 1L));
    }

    @Test
    void testBorrowBookAlreadyBorrowed() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(borrowRecordRepository.countActiveBorrows(1L)).thenReturn(0);
        when(borrowRecordRepository.findActiveByUserAndBook(1L, 1L)).thenReturn(Optional.of(testBorrowRecord));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> borrowService.borrowBook(1L, 1L));
    }

    @Test
    void testReturnBookSuccess() {
        // Arrange
        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(testBorrowRecord));
        when(returnRecordRepository.save(any(ReturnRecord.class))).thenReturn(new ReturnRecord());
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        borrowService.returnBook(1L, ReturnRecord.BookCondition.GOOD, null);

        // Assert
        verify(returnRecordRepository, times(1)).save(any(ReturnRecord.class));
        verify(borrowRecordRepository, times(1)).save(any(BorrowRecord.class));
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testReturnBookAlreadyReturned() {
        // Arrange
        testBorrowRecord.setStatus(BorrowRecord.BorrowStatus.RETURNED);
        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(testBorrowRecord));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            borrowService.returnBook(1L, ReturnRecord.BookCondition.GOOD, null));
    }

    @Test
    void testReturnBookNotFound() {
        // Arrange
        when(borrowRecordRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            borrowService.returnBook(99L, ReturnRecord.BookCondition.GOOD, null));
    }

    @Test
    void testRenewBorrowSuccess() {
        // Arrange
        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(testBorrowRecord));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(testBorrowRecord);

        // Act
        BorrowRecordDTO result = borrowService.renewBorrow(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, testBorrowRecord.getRenewalCount());
        verify(borrowRecordRepository, times(1)).save(any(BorrowRecord.class));
    }

    @Test
    void testRenewBorrowMaxLimitReached() {
        // Arrange
        testBorrowRecord.setRenewalCount(3);
        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(testBorrowRecord));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> borrowService.renewBorrow(1L));
    }

}
