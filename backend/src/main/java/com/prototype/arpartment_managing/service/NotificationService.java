package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.exception.NotificationNotFoundException;
import com.prototype.arpartment_managing.exception.NotificationNotFoundTypeException;
import com.prototype.arpartment_managing.model.Notification;
import com.prototype.arpartment_managing.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.HashSet;

@Primary
@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications(){
        return notificationRepository.findAll();
    }

    public ResponseEntity<?> getNotificationById(Long id){
        if(id != null) {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new NotificationNotFoundException(id));
            return ResponseEntity.ok(notification);
        } else {
            return ResponseEntity.badRequest().body("Must provide id");
        }
    }

    public ResponseEntity<?> getNotificationByType(String type){
        Notification notification = notificationRepository.findByType(type)
                .orElseThrow(() -> new NotificationNotFoundTypeException(type));
        return ResponseEntity.ok(notification);
    }

    @Transactional
    public Notification createNotification(Notification notification){
        Notification newNotification = new Notification();
        newNotification.setTitle(notification.getTitle());
        newNotification.setContent(notification.getContent());
        newNotification.setType(notification.getType());
        newNotification.setCreatedAt(notification.getCreatedAt());
        return notificationRepository.save(newNotification);
    }

    @Transactional
    public void deleteNotification(Long id){
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        // Handle both sides of the relationship
        if (notification.getUsers() != null) {
            // Remove this notification from each user's notification list
            notification.getUsers().forEach(user -> {
                if (user.getNotifications() != null) {
                    user.getNotifications().remove(notification);
                }
            });
            // Clear the users set in the notification
            notification.getUsers().clear();
        }

        // Save the notification to update the relationships
        notificationRepository.save(notification);

        // Now we can safely delete the notification
        notificationRepository.delete(notification);
    }

    @Transactional
    public Notification updateNotification(Long id, Notification updatedNotification) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        notification.setTitle(updatedNotification.getTitle());
        notification.setContent(updatedNotification.getContent());
        notification.setType(updatedNotification.getType());

        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification markNotificationAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification markNotificationAsUnread(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        notification.markAsUnread();
        return notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalse();
    }

    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUsersId(userId);
    }
}
