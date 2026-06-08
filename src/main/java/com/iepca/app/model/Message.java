package com.iepca.app.model;

import com.iepca.app.model.embedded.Attachment;
import com.iepca.app.model.embedded.ReadReceipt;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@CompoundIndexes({
    @CompoundIndex(name = "idx_conversation_created", def = "{'conversation': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "idx_sender", def = "{'sender': 1}"),
    @CompoundIndex(name = "idx_conversation_deleted_created", def = "{'conversation': 1, 'isDeleted': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "idx_conversation_readby", def = "{'conversation': 1, 'readBy.user': 1}")
})
public class Message {

    @Id
    private String id;

    private String conversation; // ref to Conversation
    private String sender; // ref to User
    private String content;

    @Builder.Default
    private String type = "text"; // text, image, file, system

    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    @Builder.Default
    private List<ReadReceipt> readBy = new ArrayList<>();

    @Builder.Default
    private Boolean isDeleted = false;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

