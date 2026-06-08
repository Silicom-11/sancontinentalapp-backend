package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushToken {
    private String token;
    private String device;
    private String platform; // android, ios, web
    @Builder.Default
    private Instant lastUsed = Instant.now();
    @Builder.Default
    private Boolean isActive = true;
}

