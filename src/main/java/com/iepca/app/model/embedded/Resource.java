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
public class Resource {
    private String name;
    private String type; // link, file, video
    private String url;
    @Builder.Default
    private Instant uploadedAt = Instant.now();
}

