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
@Document(collection = "attendances")
@CompoundIndexes({
    @CompoundIndex(name = "idx_student_course_date", def = "{'student': 1, 'course': 1, 'date': 1}", unique = true),
    @CompoundIndex(name = "idx_course_date", def = "{'course': 1, 'date': 1}")
})
public class Attendance {

    @Id
    private String id;

    private String student; // ref to Student
    private String course; // ref to Course
    private String teacher; // ref to User

    @Indexed
    private Instant date;

    private String status; // present, absent, late, justified
    private String arrivalTime; // HH:mm format
    private String observations;
    private String justification; // ref to Justification

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

