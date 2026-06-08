package com.iepca.app.model;

import com.iepca.app.model.embedded.Competency;
import com.iepca.app.model.embedded.EvaluationWeights;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subjects")
@CompoundIndexes({
    @CompoundIndex(name = "idx_institution_code", def = "{'institution': 1, 'code': 1}", unique = true),
    @CompoundIndex(name = "idx_institution_area", def = "{'institution': 1, 'area': 1}"),
    @CompoundIndex(name = "idx_grade_levels", def = "{'gradeLevels': 1}")
})
public class Subject {

    @Id
    private String id;

    private String institution; // ref to Institution
    private String name;
    private String code;
    private String description;

    @Builder.Default
    private List<String> gradeLevels = new ArrayList<>(); // refs to GradeLevel

    @Builder.Default
    private String applicableTo = "personalizado"; // todos, primaria, secundaria, personalizado

    @Builder.Default
    private Integer hoursPerWeek = 4;

    @Builder.Default
    private Boolean isRequired = true;

    @Builder.Default
    private String area = "otros"; // matematica, comunicacion, ciencias, personal_social, etc.

    @Builder.Default
    private EvaluationWeights defaultWeights = new EvaluationWeights();

    @Builder.Default
    private List<Competency> competencies = new ArrayList<>();

    @Builder.Default
    private String color = "#0066CC";

    @Builder.Default
    private String icon = "book";

    @Builder.Default
    private Integer order = 0;

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

