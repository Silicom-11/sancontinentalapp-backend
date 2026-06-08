package com.iepca.app.model;

import com.iepca.app.model.embedded.ClassroomLocation;
import com.iepca.app.model.embedded.ClassroomStats;
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
@Document(collection = "classrooms")
@CompoundIndexes({
    @CompoundIndex(name = "idx_grade_year_section_shift", def = "{'gradeLevel': 1, 'academicYear': 1, 'section': 1, 'shift': 1}", unique = true),
    @CompoundIndex(name = "idx_academic_year", def = "{'academicYear': 1}"),
    @CompoundIndex(name = "idx_tutor", def = "{'tutor': 1}")
})
public class Classroom {

    @Id
    private String id;

    private String gradeLevel; // ref to GradeLevel
    private String academicYear; // ref to AcademicYear
    private String section; // A-F

    @Builder.Default
    private String shift = "MaÃ±ana"; // MaÃ±ana, Tarde

    private String tutor; // ref to User

    @Builder.Default
    private Integer capacity = 35;

    @Builder.Default
    private ClassroomLocation location = new ClassroomLocation();

    @Builder.Default
    private ClassroomStats stats = new ClassroomStats();

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public int getAvailableSpots() {
        return capacity - (stats != null ? stats.getEnrolledStudents() : 0);
    }
}

