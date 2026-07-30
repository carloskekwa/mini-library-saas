package com.library.service;

import com.library.dto.ReservationDTO;
import com.library.entity.Book;
import com.library.entity.Reservation;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.ReservationRepository;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationService.
 */
@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BorrowService borrowService;

    private ReservationService reservationService;

    private User testUser;
    private Book testBook;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(
            reservationRepository,
            bookRepository,
            userRepository,
            notificationService,
            borrowService
        );

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setUser(testUser);
        testReservation.setBook(testBook);
        testReservation.setStatus(Reservation.ReservationStatus.PENDING);
        testReservation.setPositionInQueue(1);
    }

    @Test
    void testReserveBookSuccess() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(reservationRepository.findActiveByUserAndBook(1L, 1L)).thenReturn(Optional.empty());
        when(reservationRepository.countActiveByUserId(1L)).thenReturn(0);
        when(reservationRepository.findActiveByBookIdOrdered(1L)).thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // Act
        ReservationDTO result = reservationService.reserveBook(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("Test Book", result.getBookTitle());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testReserveBookUserNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reservationService.reserveBook(99L, 1L));
    }

    @Test
    void testReserveBookBookNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reservationService.reserveBook(1L, 99L));
    }

    @Test
    void testReserveBookAlreadyReserved() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(reservationRepository.findActiveByUserAndBook(1L, 1L)).thenReturn(Optional.of(testReservation));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> reservationService.reserveBook(1L, 1L));
    }

    @Test
    void testReserveBookMaxLimitReached() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(reservationRepository.findActiveByUserAndBook(1L, 1L)).thenReturn(Optional.empty());
        when(reservationRepository.countActiveByUserId(1L)).thenReturn(10); // Max is 10

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> reservationService.reserveBook(1L, 1L));
    }

    @Test
    void testCancelReservationSuccess() {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.findActiveByBookIdOrdered(1L)).thenReturn(new ArrayList<>());

        // Act
        reservationService.cancelReservation(1L);

        // Assert
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testCancelReservationNotFound() {
        // Arrange
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reservationService.cancelReservation(99L));
    }

    @Test
    void testFulfillReservationSuccess() {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        when(reservationRepository.findActiveByBookIdOrdered(1L)).thenReturn(new ArrayList<>());

        // Act
        ReservationDTO result = reservationService.fulfillReservation(1L);

        // Assert
        assertNotNull(result);
        verify(borrowService, times(1)).borrowBook(1L, 1L);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testFulfillReservationNotFound() {
        // Arrange
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reservationService.fulfillReservation(99L));
    }

}
