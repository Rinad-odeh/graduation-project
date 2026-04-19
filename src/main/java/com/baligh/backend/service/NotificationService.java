package com.baligh.backend.service;

import com.baligh.backend.dto.response.NotificationResponse;
import com.baligh.backend.dto.response.PageResponse;
import com.baligh.backend.exception.ResourceNotFoundException;
import com.baligh.backend.model.Issue;
import com.baligh.backend.model.Notification;
import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.NotificationType;
import com.baligh.backend.repository.NotificationRepository;
import com.baligh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String EXPO_PUSH_URL = "https://exp.host/api/v2/push/send";

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public void send(User recipient, Issue issue, NotificationType type, String titleAr, String bodyAr) {
        if (recipient == null) {
            log.warn("Skipping notification: recipient is null (type={})", type);
            return;
        }
        Notification notification = Notification.builder()
                .recipient(recipient)
                .issue(issue)
                .type(type)
                .titleAr(titleAr)
                .bodyAr(bodyAr)
                .build();
        notificationRepository.save(notification);

        sendExpoPush(recipient.getFcmToken(), issue != null ? issue.getId() : null, titleAr, bodyAr);
    }

    private void sendExpoPush(String expoPushToken, Long issueId, String title, String body) {
        if (expoPushToken == null || expoPushToken.isBlank()) return;
        try {
            Map<String, Object> payload = Map.of(
                    "to", expoPushToken,
                    "title", title,
                    "body", body,
                    "sound", "default",
                    "data", Map.of("issueId", issueId != null ? String.valueOf(issueId) : "")
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            restTemplate.postForObject(EXPO_PUSH_URL, entity, String.class);
        } catch (Exception e) {
            log.warn("Failed to send Expo push notification to token {}: {}", expoPushToken, e.getMessage());
        }
    }

    public PageResponse<NotificationResponse> getMyNotifications(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> result = notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user, pageable)
                .map(NotificationResponse::from);
        return PageResponse.from(result);
    }

    public long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ResourceNotFoundException.of("Notification", notificationId));
        if (!notification.getRecipient().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        notificationRepository.markAllReadByRecipient(user);
    }
}
