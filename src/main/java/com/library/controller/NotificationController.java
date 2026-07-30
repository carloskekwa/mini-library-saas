package com.library.controller;

import com.library.dto.NotificationDTO;
import com.library.entity.User;
import com.library.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Management", description = "APIs for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Constructor with dependency injection
     */
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Get all notifications for current user.
     * GET /api/notifications
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get all notifications", description = "Retrieve all notifications for current user")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    public ResponseEntity<Page<NotificationDTO>> getAllNotifications(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(page, pageSize);
        
        Page<NotificationDTO> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for current user.
     * GET /api/notifications/unread
     */
    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get unread notifications", description = "Retrieve unread notifications for current user")
    @ApiResponse(responseCode = "200", description = "Unread notifications retrieved successfully")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        
        List<NotificationDTO> unreadNotifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(unreadNotifications);
    }

    /**
     * Get unread notification count for current user.
     * GET /api/notifications/count/unread
     */
    @GetMapping("/count/unread")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications for current user")
    @ApiResponse(responseCode = "200", description = "Unread count retrieved successfully")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Integer unreadCount = notificationService.getUnreadCount(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("unreadCount", unreadCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark a notification as read.
     * PUT /api/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Mark as read", description = "Mark a notification as read")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notification marked as read"),
        @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationDTO> markAsRead(
        @PathVariable Long notificationId) {
        
        NotificationDTO notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(notification);
    }

    /**
     * Mark all notifications as read for current user.
     * PUT /api/notifications/read-all
     */
    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read for current user")
    @ApiResponse(responseCode = "200", description = "All notifications marked as read")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        notificationService.markAllAsRead(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "All notifications marked as read");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a notification.
     * DELETE /api/notifications/{notificationId}
     */
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Delete notification", description = "Delete a notification")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notification deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<Void> deleteNotification(
        @PathVariable Long notificationId) {
        
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete all notifications for current user.
     * DELETE /api/notifications
     */
    @DeleteMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Delete all notifications", description = "Delete all notifications for current user")
    @ApiResponse(responseCode = "204", description = "All notifications deleted successfully")
    public ResponseEntity<Void> deleteAllNotifications(
        Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        notificationService.deleteAllUserNotifications(userId);
        
        return ResponseEntity.noContent().build();
    }

}
