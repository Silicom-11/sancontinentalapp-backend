package com.iepca.app.model;

import com.iepca.app.model.embedded.NotificationData;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
@CompoundIndexes({
    @CompoundIndex(name = "idx_recipient_read", def = "{'recipient': 1, 'isRead': 1}"),
    @CompoundIndex(name = "idx_recipient_created", def = "{'recipient': 1, 'createdAt': -1}")
})
public class Notification {

    @Id
    private String id;

    private String recipient; // ref to User
    private String title;
    private String message;

    @Builder.Default
    private String type = "info"; // info, warning, success, error, grade, attendance, event, payment

    @Builder.Default
    private NotificationData data = new NotificationData();

    @Builder.Default
    private Boolean isRead = false;

    private Instant readAt;
    private Instant expiresAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

