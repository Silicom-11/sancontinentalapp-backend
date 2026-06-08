package com.iepca.app.repository;

import com.iepca.app.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByRecipient(String recipientId);

    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipientId);

    List<Notification> findByRecipientAndIsReadFalse(String recipientId);

    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(String recipientId);

    long countByRecipientAndIsReadFalse(String recipientId);
}

