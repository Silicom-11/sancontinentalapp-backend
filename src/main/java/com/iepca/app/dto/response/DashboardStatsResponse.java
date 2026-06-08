package com.iepca.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalStudents;
    private long totalTeachers;
    private long totalParents;
    private long totalCourses;
    private long totalClassrooms;
    private long pendingJustifications;
    private long unreadNotifications;
    private double attendanceRate;
    private double averageGrade;
}

