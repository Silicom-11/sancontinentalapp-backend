package com.iepca.app.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "evaluations")
@CompoundIndexes({
    @CompoundIndex(name = "idx_course_bimester_year", def = "{'course': 1, 'bimester': 1, 'academicYear': 1}"),
    @CompoundIndex(name = "idx_teacher", def = "{'teacher': 1}"),
    @CompoundIndex(name = "idx_course_order", def = "{'course': 1, 'order': 1}")
})
public class Evaluation {

    @Id
    private String id;

    private String course; // ref to Course
    private String teacher; // ref to User/Teacher
    private String name;
    private String type; // examen, tarea, practica, proyecto, participacion, exposicion, otro
    private Integer bimester; // 1-4

    @Builder.Default
    private Double maxGrade = 20.0;

    @Builder.Default
    private Double weight = 1.0;

    private Instant date;
    private String description;

    @Builder.Default
    private Integer academicYear = java.time.Year.now().getValue();

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Integer order = 0;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

