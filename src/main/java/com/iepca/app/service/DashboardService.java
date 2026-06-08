package com.iepca.app.service;

import com.iepca.app.dto.response.DashboardStatsResponse;
import com.iepca.app.model.*;
import com.iepca.app.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final CourseRepository courseRepository;
    private final ClassroomRepository classroomRepository;
    private final JustificationRepository justificationRepository;
    private final NotificationRepository notificationRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;

    public DashboardService(StudentRepository studentRepository,
                            TeacherRepository teacherRepository,
                            ParentRepository parentRepository,
                            CourseRepository courseRepository,
                            ClassroomRepository classroomRepository,
                            JustificationRepository justificationRepository,
                            NotificationRepository notificationRepository,
                            AttendanceRepository attendanceRepository,
                            GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.parentRepository = parentRepository;
        this.courseRepository = courseRepository;
        this.classroomRepository = classroomRepository;
        this.justificationRepository = justificationRepository;
        this.notificationRepository = notificationRepository;
        this.attendanceRepository = attendanceRepository;
        this.gradeRepository = gradeRepository;
    }

    public DashboardStatsResponse getAdminStats() {
        return DashboardStatsResponse.builder()
                .totalStudents(studentRepository.countByIsActiveTrue())
                .totalTeachers(teacherRepository.countByIsActiveTrue())
                .totalParents(parentRepository.countByIsActiveTrue())
                .totalCourses(courseRepository.countByIsActiveTrue())
                .totalClassrooms(classroomRepository.countByIsActiveTrue())
                .pendingJustifications(justificationRepository.countByStatus("pending"))
                .attendanceRate(calculateTodayAttendanceRate())
                .averageGrade(calculateAverageGrade())
                .build();
    }

    public DashboardStatsResponse getTeacherStats(String userId) {
        return DashboardStatsResponse.builder()
                .totalCourses(courseRepository.findByTeacherAndIsActiveTrue(userId).size())
                .totalStudents(courseRepository.findByTeacherAndIsActiveTrue(userId).stream()
                        .mapToLong(c -> c.getStudents().size()).sum())
                .unreadNotifications(notificationRepository.countByRecipientAndIsReadFalse(userId))
                .build();
    }

    public DashboardStatsResponse getParentStats(String userId) {
        return DashboardStatsResponse.builder()
                .unreadNotifications(notificationRepository.countByRecipientAndIsReadFalse(userId))
                .pendingJustifications(justificationRepository.countByStatus("pending"))
                .build();
    }

    private double calculateTodayAttendanceRate() {
        ZoneId lima = ZoneId.of("America/Lima");
        Instant start = LocalDate.now(lima).atStartOfDay(lima).toInstant();
        Instant end = start.plusSeconds(86400);

        List<Attendance> records = attendanceRepository.findAll().stream()
                .filter(a -> a.getDate() != null && !a.getDate().isBefore(start) && a.getDate().isBefore(end))
                .toList();

        if (records.isEmpty()) {
            return 0.0;
        }

        long positiveAttendance = records.stream()
                .filter(a -> "present".equals(a.getStatus()) || "late".equals(a.getStatus()))
                .count();
        double rate = (double) positiveAttendance / records.size() * 100.0;
        return Math.round(rate * 100.0) / 100.0;
    }

    private double calculateAverageGrade() {
        List<Grade> grades = gradeRepository.findAll();
        if (grades.isEmpty()) {
            return 0.0;
        }

        double average = grades.stream()
                .filter(g -> g.getAverage() != null)
                .mapToDouble(Grade::getAverage)
                .average()
                .orElse(0.0);
        return Math.round(average * 100.0) / 100.0;
    }
}

