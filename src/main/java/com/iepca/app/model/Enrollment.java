package com.iepca.app.model;

import com.iepca.app.model.embedded.EnrollmentDocuments;
import com.iepca.app.model.embedded.StatusHistoryEntry;
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
@Document(collection = "enrollments")
@CompoundIndexes({
    @CompoundIndex(name = "idx_student_year", def = "{'student': 1, 'academicYear': 1}", unique = true),
    @CompoundIndex(name = "idx_classroom", def = "{'classroom': 1}"),
    @CompoundIndex(name = "idx_year_status", def = "{'academicYear': 1, 'status': 1}")
})
public class Enrollment {

    @Id
    private String id;

    private String student; // ref to Student
    private String classroom; // ref to Classroom
    private String academicYear; // ref to AcademicYear

    @Builder.Default
    private Instant enrollmentDate = Instant.now();

    @Indexed(unique = true)
    private String enrollmentNumber; // auto-generated

    @Builder.Default
    private String status = "matriculado"; // matriculado, retirado, trasladado, promovido, repitente, reservado

    @Builder.Default
    private Instant statusDate = Instant.now();

    private String statusReason;

    @Builder.Default
    private String enrollmentType = "regular"; // regular, traslado, reingreso, extemporanea

    @Builder.Default
    private List<StatusHistoryEntry> statusHistory = new ArrayList<>();

    @Builder.Default
    private EnrollmentDocuments documents = new EnrollmentDocuments();

    private String previousClassroom; // ref to Classroom
    private String previousSchool;
    private String observations;
    private String enrolledBy; // ref to User

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public boolean isCurrentlyEnrolled() {
        return "matriculado".equals(status) && Boolean.TRUE.equals(isActive);
    }
}

