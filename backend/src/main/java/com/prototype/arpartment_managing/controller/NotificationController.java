package com.prototype.arpartment_managing.controller;

import com.prototype.arpartment_managing.model.Notification;
import com.prototype.arpartment_managing.service.NotificationService;
import com.prototype.arpartment_managing.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin("http://localhost:5000")
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserNotificationService userNotificationService;

    // Get all notifications - Admin only
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    // Get notification by ID - Admin only
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id);
    }

    // Get notification by type - Admin only
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getNotificationByType(@PathVariable String type) {
        return notificationService.getNotificationByType(type);
    }

    // Create new notification - Admin only
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }

    // Update notification - Admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Notification updateNotification(@PathVariable Long id, @RequestBody Notification notification) {
        return notificationService.updateNotification(id, notification);
    }

    // Delete notification - Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    // User-Notification relationship endpoints

    // Add notification to a user - Admin only
    @PostMapping("/user/{userId}/notification/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addNotificationToUser(@PathVariable Long userId, @PathVariable Long notificationId) {
        return userNotificationService.addNotificationToUser(userId, notificationId);
    }

    // Remove notification from a user - Admin only
    @DeleteMapping("/user/{userId}/notification/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeNotificationFromUser(@PathVariable Long userId, @PathVariable Long notificationId) {
        return userNotificationService.removeNotificationFromUser(userId, notificationId);
    }

    // Add notification to multiple users - Admin only
    @PostMapping("/notification/{notificationId}/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addNotificationToUsers(@PathVariable Long notificationId, @RequestBody List<Long> userIds) {
        return userNotificationService.addNotificationToUsers(notificationId, userIds);
    }

    // Remove notification from multiple users - Admin only
    @DeleteMapping("/notification/{notificationId}/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeNotificationFromUsers(@PathVariable Long notificationId, @RequestBody List<Long> userIds) {
        return userNotificationService.removeNotificationFromUsers(notificationId, userIds);
    }

    // Get all notifications for a user - Admin or own notifications
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public Set<Notification> getUserNotifications(@PathVariable Long userId) {
        return userNotificationService.getUserNotifications(userId);
    }

    // Get unread notifications for a user - Admin or own notifications
    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public Set<Notification> getUserUnreadNotifications(@PathVariable Long userId) {
        return userNotificationService.getUserUnreadNotifications(userId);
    }

    // Add notification to all users with specific role - Admin only
    @PostMapping("/notification/{notificationId}/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addNotificationToUsersByRole(@PathVariable Long notificationId, @PathVariable String role) {
        return userNotificationService.addNotificationToUsersByRole(notificationId, role);
    }
}
