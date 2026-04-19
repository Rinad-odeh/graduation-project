package com.baligh.backend.dto.response;

import com.baligh.backend.model.Notification;
import com.baligh.backend.model.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String titleAr;
    private String bodyAr;
    private boolean read;
    private Long issueId;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.id = n.getId();
        r.type = n.getType();
        r.titleAr = n.getTitleAr();
        r.bodyAr = n.getBodyAr();
        r.read = n.isRead();
        r.issueId = n.getIssue() != null ? n.getIssue().getId() : null;
        r.createdAt = n.getCreatedAt();
        return r;
    }
}
