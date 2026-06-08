package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Notification;
import com.iepca.app.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> findByRecipient(String recipientId) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(recipientId);
    }

    public List<Notification> findUnread(String recipientId) {
        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(recipientId);
    }

    public Notification findById(String id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NotificaciÃ³n", "id", id));
    }

    public Notification create(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void createBulk(List<String> recipientIds, String title, String message, String type) {
        for (String recipientId : recipientIds) {
            notificationRepository.save(Notification.builder()
                    .recipient(recipientId)
                    .title(title)
                    .message(message)
                    .type(type)
                    .build());
        }
        logger.info("Notificaciones creadas en bloque: {} destinatarios", recipientIds.size());
    }

    public Notification markAsRead(String id) {
        Notification notification = findById(id);
        notification.setIsRead(true);
        notification.setReadAt(Instant.now());
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(String recipientId) {
        List<Notification> unread = notificationRepository.findByRecipientAndIsReadFalse(recipientId);
        Instant now = Instant.now();
        unread.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(now);
        });
        notificationRepository.saveAll(unread);
        logger.info("Todas las notificaciones marcadas como leÃ­das para usuario: {}", recipientId);
    }

    public void delete(String id) {
        notificationRepository.deleteById(id);
    }

    public long countUnread(String recipientId) {
        return notificationRepository.countByRecipientAndIsReadFalse(recipientId);
    }
}

