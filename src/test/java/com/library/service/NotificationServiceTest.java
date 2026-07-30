package com.library.service;

import com.library.dto.NotificationDTO;
import com.library.entity.Notification;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.NotificationRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationService.
 */
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    private NotificationService notificationService;

    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, userRepository);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(testUser);
        testNotification.setType(Notification.NotificationType.SYSTEM_ALERT);
        testNotification.setTitle("Test Alert");
        testNotification.setMessage("This is a test notification");
        testNotification.setIsRead(false);
    }

    @Test
    void testCreateNotificationSuccess() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        NotificationDTO result = notificationService.createNotification(
            1L,
            Notification.NotificationType.SYSTEM_ALERT,
            "Test Alert",
            "This is a test notification"
        );

        // Assert
        assertNotNull(result);
        assertEquals("Test Alert", result.getTitle());
        assertEquals("This is a test notification", result.getMessage());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testCreateNotificationUserNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            notificationService.createNotification(99L, Notification.NotificationType.SYSTEM_ALERT, "Title", "Message"));
    }

    @Test
    void testMarkAsReadSuccess() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        NotificationDTO result = notificationService.markAsRead(1L);

        // Assert
        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testMarkAsReadNotFound() {
        // Arrange
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(99L));
    }

    @Test
    void testDeleteNotificationSuccess() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        // Act
        notificationService.deleteNotification(1L);

        // Assert
        verify(notificationRepository, times(1)).delete(any(Notification.class));
    }

    @Test
    void testDeleteNotificationNotFound() {
        // Arrange
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> notificationService.deleteNotification(99L));
    }

}
