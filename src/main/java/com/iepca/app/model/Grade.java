package com.iepca.app.model;

import com.iepca.app.model.embedded.ScoreEntry;
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
@Document(collection = "grades")
@CompoundIndexes({
    @CompoundIndex(name = "idx_student_course_bimester_year", def = "{'student': 1, 'course': 1, 'bimester': 1, 'academicYear': 1}", unique = true),
    @CompoundIndex(name = "idx_course_bimester_year", def = "{'course': 1, 'bimester': 1, 'academicYear': 1}"),
    @CompoundIndex(name = "idx_student_year", def = "{'student': 1, 'academicYear': 1}"),
    @CompoundIndex(name = "idx_teacher", def = "{'teacher': 1}")
})
public class Grade {

    @Id
    private String id;

    private String student; // ref to Student
    private String course; // ref to Course
    private Integer bimester; // 1-4
    @Builder.Default
    private Integer academicYear = java.time.Year.now().getValue();

    @Builder.Default
    private List<ScoreEntry> scores = new ArrayList<>();

    @Builder.Default
    private Double average = 0.0;

    @Builder.Default
    private String status = "abierto"; // abierto, cerrado, publicado

    private Instant closedAt;
    private String closedBy;
    private Instant publishedAt;
    private String teacher; // ref to User

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public void calculateAverage() {
        if (scores == null || scores.isEmpty()) {
            this.average = 0.0;
            return;
        }
        double sum = scores.stream()
                .filter(s -> s.getScore() != null)
                .mapToDouble(ScoreEntry::getScore)
                .sum();
        long count = scores.stream()
                .filter(s -> s.getScore() != null)
                .count();
        this.average = count > 0 ? sum / count : 0.0;
    }
}

