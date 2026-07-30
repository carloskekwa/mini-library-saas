package com.library.service;

import com.library.dto.NotificationDTO;
import com.library.entity.Notification;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.NotificationRepository;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing notifications.
 */
@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Constructor with dependency injection
     */
    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a notification for a user.
     */
    public NotificationDTO createNotification(Long userId, Notification.NotificationType type,
                                              String title, String message) {
        logger.info("Creating notification: userId={}, type={}", userId, type);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Notification notification = new Notification(user, type, title, message);
        Notification saved = notificationRepository.save(notification);

        logger.info("Notification created: notificationId={}", saved.getId());
        return NotificationDTO.from(saved);
    }

    /**
     * Create a notification with related resource.
     */
    public NotificationDTO createNotificationWithRelated(Long userId, Notification.NotificationType type,
                                                          String title, String message,
                                                          Long relatedBookId, Long relatedBorrowId) {
        logger.info("Creating notification with related: userId={}, bookId={}, borrowId={}", userId, relatedBookId, relatedBorrowId);

        NotificationDTO notification = createNotification(userId, type, title, message);

        // Update related IDs
        Notification notif = notificationRepository.findById(notification.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notif.setRelatedBookId(relatedBookId);
        notif.setRelatedBorrowId(relatedBorrowId);
        notificationRepository.save(notif);

        return NotificationDTO.from(notif);
    }

    /**
     * Get all notifications for a user.
     */
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable) {
        logger.info("Fetching notifications: userId={}", userId);

        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.map(NotificationDTO::from);
    }

    /**
     * Get unread notifications for a user.
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        logger.info("Fetching unread notifications: userId={}", userId);

        List<Notification> notifications = notificationRepository.findUnreadByUserId(userId);
        return notifications.stream().map(NotificationDTO::from).collect(Collectors.toList());
    }

    /**
     * Count unread notifications for a user.
     */
    @Transactional(readOnly = true)
    public Integer getUnreadCount(Long userId) {
        logger.info("Counting unread notifications: userId={}", userId);

        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * Mark a notification as read.
     */
    public NotificationDTO markAsRead(Long notificationId) {
        logger.info("Marking notification as read: notificationId={}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);

        logger.info("Notification marked as read: notificationId={}", notificationId);
        return NotificationDTO.from(saved);
    }

    /**
     * Mark all notifications as read for a user.
     */
    public void markAllAsRead(Long userId) {
        logger.info("Marking all notifications as read: userId={}", userId);

        List<Notification> unreadNotifications = notificationRepository.findUnreadByUserId(userId);

        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        }

        notificationRepository.saveAll(unreadNotifications);

        logger.info("All notifications marked as read: userId={}, count={}", userId, unreadNotifications.size());
    }

    /**
     * Delete a notification.
     */
    public void deleteNotification(Long notificationId) {
        logger.info("Deleting notification: notificationId={}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        notificationRepository.delete(notification);

        logger.info("Notification deleted: notificationId={}", notificationId);
    }

    /**
     * Delete all notifications for a user.
     */
    public void deleteAllUserNotifications(Long userId) {
        logger.info("Deleting all notifications: userId={}", userId);

        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notificationRepository.deleteAll(notifications);

        logger.info("All notifications deleted: userId={}, count={}", userId, notifications.size());
    }

}
