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
public class StatusHistoryEntry {
    private String status;
    @Builder.Default
    private Instant date = Instant.now();
    private String reason;
    private String changedBy; // User reference
}

