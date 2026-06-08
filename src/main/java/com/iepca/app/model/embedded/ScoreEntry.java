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
public class ScoreEntry {
    private String evaluation; // ObjectId reference
    private Double score; // 0-20
    private String comments;
    @Builder.Default
    private Instant gradedAt = Instant.now();
    private String gradedBy;
}

