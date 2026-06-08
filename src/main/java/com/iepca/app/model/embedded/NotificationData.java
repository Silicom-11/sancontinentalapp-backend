package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationData {
    private String studentId;
    private String courseId;
    private String gradeId;
    private String attendanceId;
    private String link;
}

