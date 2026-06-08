package com.iepca.app.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "gradelevels")
@CompoundIndexes({
    @CompoundIndex(name = "idx_institution_order", def = "{'institution': 1, 'order': 1}"),
    @CompoundIndex(name = "idx_institution_type_level", def = "{'institution': 1, 'type': 1, 'level': 1}", unique = true)
})
public class GradeLevel {

    @Id
    private String id;

    private String institution; // ref to Institution
    private String name;
    private String shortName;
    private Integer level; // 1-11
    private String type; // inicial, primaria, secundaria
    private Integer order;
    private String description;

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public String getFullName() {
        String typeName = switch (type) {
            case "primaria" -> "Primaria";
            case "secundaria" -> "Secundaria";
            case "inicial" -> "Inicial";
            default -> type;
        };
        return level + "Â° " + typeName;
    }
}

