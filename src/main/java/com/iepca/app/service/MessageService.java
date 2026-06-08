package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Conversation;
import com.iepca.app.model.Message;
import com.iepca.app.model.embedded.LastMessage;
import com.iepca.app.repository.ConversationRepository;
import com.iepca.app.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public MessageService(MessageRepository messageRepository,
                          ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
    }

    public List<Conversation> getUserConversations(String userId) {
        return conversationRepository.findByParticipantsContainingAndIsActiveTrueOrderByUpdatedAtDesc(userId);
    }

    public Conversation findOrCreateDirectConversation(String userId1, String userId2) {
        return conversationRepository.findDirectConversation(userId1, userId2)
                .orElseGet(() -> {
                    Conversation conv = Conversation.builder()
                            .participants(List.of(userId1, userId2))
                            .type("direct")
                            .build();
                    return conversationRepository.save(conv);
                });
    }

    public List<Message> getConversationMessages(String conversationId) {
        return messageRepository.findByConversationOrderByCreatedAtAsc(conversationId);
    }

    public Message sendMessage(String conversationId, String senderId, String content, String type) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("ConversaciÃ³n", "id", conversationId));

        Message message = Message.builder()
                .conversation(conversationId)
                .sender(senderId)
                .content(content)
                .type(type != null ? type : "text")
                .build();

        message = messageRepository.save(message);

        // Update last message in conversation
        conversation.setLastMessage(LastMessage.builder()
                .content(content)
                .sender(senderId)
                .sentAt(Instant.now())
                .build());

        // Increment unread count for other participants
        for (String participant : conversation.getParticipants()) {
            if (!participant.equals(senderId)) {
                conversation.getUnreadCount().merge(participant, 1, Integer::sum);
            }
        }

        conversationRepository.save(conversation);

        logger.info("Mensaje enviado en conversaciÃ³n: {}", conversationId);
        return message;
    }

    public void markConversationAsRead(String conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("ConversaciÃ³n", "id", conversationId));

        conversation.getUnreadCount().put(userId, 0);
        conversationRepository.save(conversation);
    }

    public Message sendMessage(String conversationId, String senderId, String content) {
        return sendMessage(conversationId, senderId, content, "text");
    }

    public void deleteMessage(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje", "id", messageId));
        messageRepository.delete(message);
        logger.info("Mensaje eliminado: {}", messageId);
    }
}

