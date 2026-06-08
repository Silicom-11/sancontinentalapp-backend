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
@Document(collection = "parents")
@CompoundIndexes({
    @CompoundIndex(name = "idx_children_student", def = "{'children.student': 1}"),
    @CompoundIndex(name = "idx_active", def = "{'isActive': 1}")
})
public class Parent {

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

    @Indexed
    private String phone;

    private String secondaryPhone;
    private LocalDate birthDate;
    private String gender; // M, F
    private String photo;
    private Address address;

    // Occupation
    private String occupation;
    private String workplace;
    private String workPhone;

    // Children references
    @Builder.Default
    private List<Guardian> children = new ArrayList<>();

    // Notification preferences
    @Builder.Default
    private NotificationPreferences notifications = new NotificationPreferences();

    // Documents
    @Builder.Default
    private ParentDocuments documents = new ParentDocuments();

    // Push tokens
    @Builder.Default
    private List<PushToken> pushTokens = new ArrayList<>();

    // Status
    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isVerified = false;

    private Instant lastLogin;
    private Instant lastActive;

    @Builder.Default
    private Boolean isOnline = false;

    // User link (optional)
    private String userId; // ref to User

    private String internalNotes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }
}

