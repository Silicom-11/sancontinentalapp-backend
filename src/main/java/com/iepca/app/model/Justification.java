package com.iepca.app.model;

import com.iepca.app.model.embedded.DocumentFile;
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
@Document(collection = "justifications")
@CompoundIndexes({
    @CompoundIndex(name = "idx_student_status", def = "{'student': 1, 'status': 1}"),
    @CompoundIndex(name = "idx_parent", def = "{'parent': 1}")
})
public class Justification {

    @Id
    private String id;

    private String student; // ref to Student
    private String parent; // ref to Parent
    
    @Builder.Default
    private List<Instant> dates = new ArrayList<>(); // affected dates

    private String reason; // Enfermedad, Cita mÃ©dica, Emergencia familiar, TrÃ¡mites oficiales, Otros
    private String observations;

    @Builder.Default
    private List<DocumentFile> documents = new ArrayList<>();

    @Builder.Default
    private List<String> coursesAffected = new ArrayList<>(); // refs to Course

    @Indexed
    @Builder.Default
    private String status = "approved"; // pending, approved, rejected

    private String reviewedBy; // ref to User
    private Instant reviewedAt;
    private String reviewNote;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

