package com.iepca.app.model;

import com.iepca.app.model.embedded.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "institutions")
public class Institution {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String code; // cÃ³digo modular

    private String logo;
    private Address address;
    private String phone;
    private String email;
    private String website;
    private String director; // ref to User

    // Evaluation system
    @Builder.Default
    private InstitutionEvaluationSystem evaluationSystem = new InstitutionEvaluationSystem();

    // Grade scale
    @Builder.Default
    private GradeScale gradeScale = new GradeScale();

    // Shifts
    @Builder.Default
    private List<String> shifts = List.of("MaÃ±ana", "Tarde");

    @Builder.Default
    private ShiftSchedules shiftSchedules = new ShiftSchedules();

    // Academic levels
    @Builder.Default
    private AcademicLevels academicLevels = new AcademicLevels();

    @Builder.Default
    private Integer maxSectionsPerGrade = 4;

    @Builder.Default
    private Integer defaultClassroomCapacity = 35;

    // Evaluation types and weights
    @Builder.Default
    private List<String> evaluationTypes = List.of("examen", "tarea", "proyecto", "participacion", "practica", "exposicion");

    @Builder.Default
    private EvaluationWeights defaultEvaluationWeights = new EvaluationWeights();

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

