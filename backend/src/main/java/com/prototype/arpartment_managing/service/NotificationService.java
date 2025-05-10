package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.exception.NotificationNotFoundException;
import com.prototype.arpartment_managing.exception.NotificationNotFoundTypeException;
import com.prototype.arpartment_managing.model.Notification;
import com.prototype.arpartment_managing.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Primary
@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAllNotification(){
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




}
