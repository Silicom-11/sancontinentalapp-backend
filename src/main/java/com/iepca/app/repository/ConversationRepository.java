package com.iepca.app.repository;

import com.iepca.app.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    List<Conversation> findByParticipantsContaining(String userId);

    List<Conversation> findByParticipantsContainingAndIsActiveTrueOrderByUpdatedAtDesc(String userId);

    @Query("{'type': 'direct', 'participants': {$all: [?0, ?1]}}")
    Optional<Conversation> findDirectConversation(String userId1, String userId2);

    List<Conversation> findByTypeAndParticipantsContaining(String type, String userId);
}

