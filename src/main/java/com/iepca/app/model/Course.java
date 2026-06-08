package com.iepca.app.model;

import com.iepca.app.model.embedded.EvaluationWeights;
import com.iepca.app.model.embedded.Schedule;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
@CompoundIndexes({
    @CompoundIndex(name = "idx_grade_section", def = "{'gradeLevel': 1, 'section': 1}"),
    @CompoundIndex(name = "idx_teacher", def = "{'teacher': 1}")
})
public class Course {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String code;

    private String description;
    private String gradeLevel; // 1Âº Primaria ... 5Âº Secundaria

    @Builder.Default
    private String section = "A"; // A-F

    private String teacher; // ref to User

    @Builder.Default
    private List<String> students = new ArrayList<>(); // refs to Student

    @Builder.Default
    private List<Schedule> schedule = new ArrayList<>();

    @Builder.Default
    private EvaluationWeights evaluationWeights = new EvaluationWeights();

    @Builder.Default
    private Integer academicYear = java.time.Year.now().getValue();

    @Builder.Default
    private String period = "Anual"; // Anual, Bimestre 1-4

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public String getFullName() {
        return name + " - " + gradeLevel + " " + section;
    }

    public int getStudentCount() {
        return students != null ? students.size() : 0;
    }
}

