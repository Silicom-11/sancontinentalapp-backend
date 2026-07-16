package com.iepca.app.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "maintenance_logs")
public class MaintenanceLog {

    @Id
    private String id;

    @Indexed
    private String type; // CORRECTIVO, PREVENTIVO, PERFECTIVO, ADAPTATIVO

    @Indexed
    private String category;

    private String description;

    private String status; // PENDIENTE, EN_PROGRESO, COMPLETADO, FALLIDO

    private String severity; // CRITICO, ALTO, MEDIO, BAJO

    private String component;

    private Map<String, Object> details;

    private String resolvedBy;

    private Instant resolvedAt;

    @CreatedDate
    private Instant createdAt;
}
