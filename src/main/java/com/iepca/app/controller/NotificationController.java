package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Notification;
import com.iepca.app.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> list(
            Authentication authentication,
            @RequestParam(required = false) String unread) {
        String userId = authentication.getName();
        List<Notification> notifications = notificationService.findByRecipient(userId);
        if ("true".equals(unread)) {
            notifications = notifications.stream()
                    .filter(n -> !Boolean.TRUE.equals(n.getIsRead()))
                    .toList();
        }
        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            Authentication authentication) {
        String userId = authentication.getName();
        long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("count", count)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(@PathVariable String id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok(notification));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Authentication authentication) {
        String userId = authentication.getName();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Todas las notificaciones marcadas como leÃ­das"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Notification>> create(@RequestBody Notification notification) {
        Notification created = notificationService.create(notification);
        return ResponseEntity.ok(ApiResponse.ok("NotificaciÃ³n creada", created));
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasAnyRole('administrativo', 'director', 'docente')")
    public ResponseEntity<ApiResponse<Void>> broadcast(@RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        String message = (String) body.get("message");
        String type = (String) body.getOrDefault("type", "general");

        @SuppressWarnings("unchecked")
        List<String> recipientIds = (List<String>) body.get("recipientIds");

        if (recipientIds != null && !recipientIds.isEmpty()) {
            for (String recipientId : recipientIds) {
                Notification notification = new Notification();
                notification.setRecipient(recipientId);
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setType(type);
                notificationService.create(notification);
            }
        }
        return ResponseEntity.ok(ApiResponse.<Void>ok("Notificaciones enviadas"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        notificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("NotificaciÃ³n eliminada"));
    }
}

