package com.iepca.app.controller;

import com.iepca.app.dto.request.MessageRequest;
import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Conversation;
import com.iepca.app.model.Message;
import com.iepca.app.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<Conversation>>> getConversations(
            Authentication authentication) {
        String userId = authentication.getName();
        List<Conversation> conversations = messageService.getUserConversations(userId);
        return ResponseEntity.ok(ApiResponse.ok(conversations));
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<ApiResponse<List<Message>>> getConversationMessages(
            @PathVariable String id,
            Authentication authentication) {
        String userId = authentication.getName();
        List<Message> messages = messageService.getConversationMessages(id);
        messageService.markConversationAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(messages));
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Message>> sendMessage(
            @Valid @RequestBody MessageRequest request,
            Authentication authentication) {
        String senderId = authentication.getName();
        Message message;

        if (request.getConversationId() != null) {
            message = messageService.sendMessage(
                    request.getConversationId(), senderId, request.getContent());
        } else if (request.getRecipientId() != null) {
            Conversation conversation = messageService.findOrCreateDirectConversation(
                    senderId, request.getRecipientId());
            message = messageService.sendMessage(
                    conversation.getId(), senderId, request.getContent());
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Se requiere conversationId o recipientId"));
        }

        return ResponseEntity.ok(ApiResponse.ok("Mensaje enviado", message));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            Authentication authentication) {
        String userId = authentication.getName();
        List<Conversation> conversations = messageService.getUserConversations(userId);
        long totalUnread = conversations.stream()
                .mapToLong(c -> c.getUnreadCount() != null && c.getUnreadCount().containsKey(userId)
                        ? c.getUnreadCount().get(userId) : 0)
                .sum();
        return ResponseEntity.ok(ApiResponse.ok(Map.of("count", totalUnread)));
    }

    @PutMapping("/conversations/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable String id,
            Authentication authentication) {
        String userId = authentication.getName();
        messageService.markConversationAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.<Void>ok("ConversaciÃ³n marcada como leÃ­da"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable String id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Mensaje eliminado"));
    }
}

