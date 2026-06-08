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
public class DocumentFile {
    private String filename;
    private String originalName;
    private String path;
    private String url;
    private String key;
    @Builder.Default
    private String storage = "local"; // local, r2
    private String mimetype;
    private Long size;
    @Builder.Default
    private Instant uploadedAt = Instant.now();
}

