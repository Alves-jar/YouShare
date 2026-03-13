package com.noxus.youshare.controller;

import com.noxus.youshare.dto.notification.NotificationResponseDTO;
import com.noxus.youshare.entity.Notification;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
            @AuthenticationPrincipal User user) {
        List<Notification> notifications = notificationService.getUserNotifications(user.getId());
        List<NotificationResponseDTO> dtos = notifications.stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        Notification notification = notificationService.markAsRead(id, user.getId());
        return ResponseEntity.ok(NotificationResponseDTO.fromEntity(notification));
    }
}
