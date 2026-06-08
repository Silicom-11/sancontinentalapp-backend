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
@Document(collection = "teachers")
@CompoundIndexes({
    @CompoundIndex(name = "idx_specialty_active", def = "{'specialty': 1, 'isActive': 1}")
})
public class Teacher {

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
    private String secondaryPhone;
    private LocalDate birthDate;
    private String gender; // M, F
    private String photo;
    private Address address;

    // Professional
    @Indexed(unique = true, sparse = true)
    private String employeeCode; // Auto-generated: DOC-2025-0001

    private String specialty;
    private String academicDegree;
    private String professionalTitle;

    @Builder.Default
    private List<Certification> certifications = new ArrayList<>();

    // Contract info
    private String contractType; // contratado, nombrado, CAS
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private Double salary;

    // Banking
    private BankAccount bankAccount;

    // Documents
    @Builder.Default
    private TeacherDocuments documents = new TeacherDocuments();

    // Status
    @Builder.Default
    private Boolean isActive = true;

    // User link (optional)
    private String userId; // ref to User
    private String institution; // ref to Institution

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}

