package com.iepca.app.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String title;

    @Indexed
    private String date; // date string

    private String time;

    @Indexed
    @Builder.Default
    private String type = "activity"; // exam, meeting, holiday, activity, deadline

    private String description;
    private String location;
    private String participants;

    @Builder.Default
    private Boolean notifyStudents = false;

    @Builder.Default
    private Boolean notifyParents = false;

    @Builder.Default
    private Boolean notifyTeachers = false;

    private String createdBy; // ref to User

    @Indexed
    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

