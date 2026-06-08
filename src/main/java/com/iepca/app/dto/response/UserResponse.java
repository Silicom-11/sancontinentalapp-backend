package com.iepca.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    @JsonProperty("_id")
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String dni;
    private String phone;
    private String role;
    @JsonProperty("avatar")
    private String photo;
    private Boolean isActive;
    private Boolean isVerified;
    private List<String> permissions;
    private String studentProfile;
    private Instant createdAt;
}

