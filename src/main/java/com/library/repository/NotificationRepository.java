package com.library.repository;

import com.library.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Notification repository for database operations on Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications by user ID.
     */
    List<Notification> findByUserId(Long userId);

    /**
     * Find notifications by user ID (paginated).
     */
    Page<Notification> findByUserId(Long userId, Pageable pageable);

    /**
     * Find unread notifications by user ID.
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    /**
     * Find unread notifications by user ID (paginated).
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Page<Notification> findUnreadByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Count unread notifications for a user.
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Integer countUnreadByUserId(@Param("userId") Long userId);

    /**
     * Find notifications by type.
     */
    List<Notification> findByType(Notification.NotificationType type);

    /**
     * Find notifications by related book ID.
     */
    List<Notification> findByRelatedBookId(Long bookId);

}
