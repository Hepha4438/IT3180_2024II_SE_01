package com.prototype.arpartment_managing.dto;

import com.prototype.arpartment_managing.model.Notification;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class NotificationDTO {

    private Long id;
    private String title;
    private String content;
    private String type;
    private LocalDateTime createdAt;
    private Boolean isRead;

    private Set<String> usernames;

    public NotificationDTO() {}

    public NotificationDTO(Notification notification){
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.type = notification.getType();
        this.createdAt = notification.getCreatedAt();
        this.isRead = notification.isRead();
        this.usernames = notification.getUsers().stream()
            .map(user -> user.getUsername())
            .collect(Collectors.toSet());
    }
}
