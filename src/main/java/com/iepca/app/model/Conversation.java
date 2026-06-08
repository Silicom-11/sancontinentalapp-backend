package com.iepca.app.model;

import com.iepca.app.model.embedded.LastMessage;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversations")
@CompoundIndexes({
    @CompoundIndex(name = "idx_participants", def = "{'participants': 1}"),
    @CompoundIndex(name = "idx_last_message", def = "{'lastMessage.sentAt': -1}"),
    @CompoundIndex(name = "idx_type_participants", def = "{'type': 1, 'participants': 1}"),
    @CompoundIndex(name = "idx_participants_active_last", def = "{'participants': 1, 'isActive': 1, 'lastMessage.sentAt': -1}")
})
public class Conversation {

    @Id
    private String id;

    @Builder.Default
    private List<String> participants = new ArrayList<>(); // refs to User

    @Builder.Default
    private String type = "direct"; // direct, group, support

    private String name;

    private LastMessage lastMessage;

    @Builder.Default
    private Map<String, Integer> unreadCount = new HashMap<>(); // userId -> count

    @Builder.Default
    private Boolean isActive = true;

    // Metadata
    private ConversationMetadata metadata;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationMetadata {
        private String studentId; // ref to Student
        private String courseId; // ref to Course
    }

    public String getOtherParticipant(String userId) {
        if (participants == null) return null;
        return participants.stream()
                .filter(p -> !p.equals(userId))
                .findFirst()
                .orElse(null);
    }
}

