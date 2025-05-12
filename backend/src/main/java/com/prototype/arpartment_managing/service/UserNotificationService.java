package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.exception.NotificationNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.model.Notification;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.repository.NotificationRepository;
import com.prototype.arpartment_managing.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserNotificationService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public ResponseEntity<?> addNotificationToUser(Long userId, Long notificationId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new NotificationNotFoundException(notificationId));

            if (user.getNotifications() == null) {
                user.setNotifications(new HashSet<>());
            }
            if (notification.getUsers() == null) {
                notification.setUsers(new HashSet<>());
            }
            
            user.getNotifications().add(notification);
            notification.getUsers().add(user);
            
            userRepository.save(user);
            notificationRepository.save(notification);
            
            return ResponseEntity.ok(Map.of(
                "message", "Notification added to user successfully",
                "userId", userId,
                "notificationId", notificationId
            ));
        } catch (UserNotFoundException | NotificationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add notification to user: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> removeNotificationFromUser(Long userId, Long notificationId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new NotificationNotFoundException(notificationId));

            if (user.getNotifications() != null) {
                user.getNotifications().remove(notification);
                userRepository.save(user);
            }
            if (notification.getUsers() != null) {
                notification.getUsers().remove(user);
                notificationRepository.save(notification);
            }
            
            return ResponseEntity.ok(Map.of(
                "message", "Notification removed from user successfully",
                "userId", userId,
                "notificationId", notificationId
            ));
        } catch (UserNotFoundException | NotificationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove notification from user: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> addNotificationToUsers(Long notificationId, List<Long> userIds) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new NotificationNotFoundException(notificationId));
            
            if (notification.getUsers() == null) {
                notification.setUsers(new HashSet<>());
            }

            List<User> users = userRepository.findAllById(userIds);
            if (users.size() != userIds.size()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Some users were not found"));
            }

            users.forEach(user -> {
                if (user.getNotifications() == null) {
                    user.setNotifications(new HashSet<>());
                }
                user.getNotifications().add(notification);
                notification.getUsers().add(user);
                userRepository.save(user);
            });
            
            notificationRepository.save(notification);
            
            return ResponseEntity.ok(Map.of(
                "message", "Notification added to users successfully",
                "notificationId", notificationId,
                "userCount", users.size()
            ));
        } catch (NotificationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add notification to users: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> removeNotificationFromUsers(Long notificationId, List<Long> userIds) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new NotificationNotFoundException(notificationId));

            List<User> users = userRepository.findAllById(userIds);
            if (users.size() != userIds.size()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Some users were not found"));
            }

            users.forEach(user -> {
                if (user.getNotifications() != null) {
                    user.getNotifications().remove(notification);
                    userRepository.save(user);
                }
                if (notification.getUsers() != null) {
                    notification.getUsers().remove(user);
                }
            });
            
            notificationRepository.save(notification);
            
            return ResponseEntity.ok(Map.of(
                "message", "Notification removed from users successfully",
                "notificationId", notificationId,
                "userCount", users.size()
            ));
        } catch (NotificationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove notification from users: " + e.getMessage()));
        }
    }

    public Set<Notification> getUserNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return user.getNotifications();
    }

    public Set<Notification> getUserUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Set<Notification> notifications = user.getNotifications();
        if (notifications != null) {
            notifications.removeIf(Notification::isRead);
        }
        return notifications;
    }

    @Transactional
    public ResponseEntity<?> addNotificationToUsersByRole(Long notificationId, String role) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new NotificationNotFoundException(notificationId));

            if (notification.getUsers() == null) {
                notification.setUsers(new HashSet<>());
            }

            List<User> usersWithRole = userRepository.findByRole(role);
            if (usersWithRole.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No users found with role: " + role));
            }
            
            usersWithRole.forEach(user -> {
                if (user.getNotifications() == null) {
                    user.setNotifications(new HashSet<>());
                }
                user.getNotifications().add(notification);
                notification.getUsers().add(user);
                userRepository.save(user);
            });

            notificationRepository.save(notification);
            
            return ResponseEntity.ok(Map.of(
                "message", "Notification added to users with role successfully",
                "notificationId", notificationId,
                "role", role,
                "userCount", usersWithRole.size()
            ));
        } catch (NotificationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add notification to users by role: " + e.getMessage()));
        }
    }
    @Transactional
    public ResponseEntity<?> updateUsersinNotiList(Long notificationId, List<Long> userIds){
        try{
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(()-> new NotificationNotFoundException(notificationId));
            if (notification.getUsers() == null) {
                notification.setUsers(new HashSet<>());
            }
            Set<User> userNoti = notification.getUsers();
            List<User> users = new ArrayList<>(userNoti);
            List<User> userss = userRepository.findAllById(userIds);
            users.forEach(user -> {
                if (user.getNotifications() != null) {
                    user.getNotifications().remove(notification);
                    userRepository.save(user);
                }
                if (notification.getUsers() != null) {
                    notification.getUsers().remove(user);
                    notificationRepository.save(notification);
                }
            });

            userss.forEach(user -> {
                if (user.getNotifications() == null) {
                    user.setNotifications(new HashSet<>());
                }
                user.getNotifications().add(notification);
                notification.getUsers().add(user);
                userRepository.save(user);
                notificationRepository.save(notification);
            });
            return ResponseEntity.ok(Map.of(
                    "message", "Notification added to users successfully",
                    "notificationId", notificationId,
                    "userCount", userss.size()
            ));
        } catch (NotificationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}