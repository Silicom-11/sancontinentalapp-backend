package com.iepca.app.repository;

import com.iepca.app.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findByConversation(String conversationId);

    List<Message> findByConversationAndIsDeletedFalseOrderByCreatedAtDesc(String conversationId);

    List<Message> findByConversationOrderByCreatedAtAsc(String conversationId);

    List<Message> findBySender(String senderId);

    long countByConversationAndIsDeletedFalse(String conversationId);
}

