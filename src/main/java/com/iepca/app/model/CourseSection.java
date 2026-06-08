package com.iepca.app.model;

import com.iepca.app.model.embedded.*;
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
@Document(collection = "coursesections")
@CompoundIndexes({
    @CompoundIndex(name = "idx_subject_classroom_year", def = "{'subject': 1, 'classroom': 1, 'academicYear': 1}", unique = true),
    @CompoundIndex(name = "idx_teacher", def = "{'teacher': 1}"),
    @CompoundIndex(name = "idx_academic_year", def = "{'academicYear': 1}")
})
public class CourseSection {

    @Id
    private String id;

    private String subject; // ref to Subject
    private String classroom; // ref to Classroom
    private String teacher; // ref to User
    private String academicYear; // ref to AcademicYear

    @Builder.Default
    private List<Schedule> schedule = new ArrayList<>();

    @Builder.Default
    private EvaluationWeights evaluationWeights = new EvaluationWeights();

    @Builder.Default
    private List<PeriodEvaluation> periodEvaluations = new ArrayList<>();

    @Builder.Default
    private CourseSectionStats stats = new CourseSectionStats();

    @Builder.Default
    private List<Resource> resources = new ArrayList<>();

    @Builder.Default
    private List<String> students = new ArrayList<>(); // refs to Student

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

