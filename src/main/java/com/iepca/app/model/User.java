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
@Document(collection = "users")
@CompoundIndexes({
    @CompoundIndex(name = "idx_role_active", def = "{'role': 1, 'isActive': 1}"),
    @CompoundIndex(name = "idx_children_student", def = "{'children.student': 1}")
})
public class User {

    @Id
    private String id;

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true, sparse = true)
    private String dni;

    private String password;
    private String phone;

    @Indexed
    private String role; // padre, docente, estudiante, administrativo, director

    private String photo;
    private Address address;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isVerified = false;

    private Instant lastLogin;

    // Student-specific
    private String studentProfile; // ObjectId ref to Student

    // Parent-specific children references
    @Builder.Default
    private List<ChildReference> children = new ArrayList<>();

    // Permissions
    @Builder.Default
    private List<String> permissions = new ArrayList<>();

    // Push notification tokens
    @Builder.Default
    private List<PushToken> pushTokens = new ArrayList<>();

    // User settings
    @Builder.Default
    private UserSettings settings = new UserSettings();

    // Location tracking
    private Coordinates lastKnownLocation;
    private Instant lastLocationUpdate;
    @Builder.Default
    private Boolean isOnline = false;
    private Instant lastActive;

    // Password reset
    private String passwordResetToken;
    private Instant passwordResetExpires;
    @Builder.Default
    private Integer loginAttempts = 0;
    private Instant lockUntil;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isLocked() {
        return lockUntil != null && lockUntil.isAfter(Instant.now());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChildReference {
        private String student; // ObjectId reference
        private String relationship; // padre, madre, tutor, abuelo, etc.
        @Builder.Default
        private Boolean isPrimaryContact = false;
    }
}

