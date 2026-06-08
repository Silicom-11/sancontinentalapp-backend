package com.iepca.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NotBlank(message = "El contenido es obligatorio")
    private String content;

    private String type; // text, image, file
    private String conversationId;
    private String recipientId; // for new conversations
}

