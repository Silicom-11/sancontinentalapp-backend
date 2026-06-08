package com.iepca.app.model;

import com.iepca.app.model.embedded.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "students")
@CompoundIndexes({
    @CompoundIndex(name = "idx_grade_section", def = "{'gradeLevel': 1, 'section': 1}"),
    @CompoundIndex(name = "idx_active_grade", def = "{'isActive': 1, 'gradeLevel': 1}")
})
public class Student {

    @Id
    private String id;

    @TextIndexed
    private String firstName;

    @TextIndexed
    private String lastName;

    @Indexed(unique = true)
    private String dni;

    @Indexed(unique = true, sparse = true)
    private String email;

    private String password;
    private String phone;
    private LocalDate birthDate;
    private String gender; // M, F
    private String photo;
    private Address address;

    // Academic
    private String gradeLevel; // e.g. "1Âº Primaria", "5Âº Secundaria"
    @Builder.Default
    private String section = "A";

    @Indexed(unique = true, sparse = true)
    private String studentCode; // Auto-generated: EST-2025-0001

    @Indexed(unique = true, sparse = true)
    private String enrollmentNumber;

    // Guardians
    @Builder.Default
    private List<Guardian> guardians = new ArrayList<>();

    // Medical
    @Builder.Default
    private MedicalInfo medicalInfo = new MedicalInfo();

    // Documents
    @Builder.Default
    private StudentDocuments documents = new StudentDocuments();

    // Status
    @Builder.Default
    private String status = "activo"; // activo, inactivo, egresado, trasladado, retirado

    @Builder.Default
    private Boolean isActive = true;

    // User link (optional)
    private String userId; // ref to User

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}

