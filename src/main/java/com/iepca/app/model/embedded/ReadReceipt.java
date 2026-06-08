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
public class ReadReceipt {
    private String user; // User ObjectId reference
    @Builder.Default
    private Instant readAt = Instant.now();
}

