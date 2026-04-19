package com.baligh.backend.controller;

import com.baligh.backend.dto.response.NotificationResponse;
import com.baligh.backend.dto.response.PageResponse;
import com.baligh.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>> getMyNotifications(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.getMyNotifications(userId, page, size));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(userId)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        notificationService.markRead(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@RequestHeader("X-User-Id") Long userId) {
        notificationService.markAllRead(userId);
        return ResponseEntity.noContent().build();
    }
}
