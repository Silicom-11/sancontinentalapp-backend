package com.iepca.app.service;

import com.iepca.app.dto.request.AttendanceRequest;
import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Attendance;
import com.iepca.app.repository.AttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Attendance findById(String id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asistencia", "id", id));
    }

    public List<Attendance> markAttendanceRecords(AttendanceRequest request, String teacherId) {
        Instant date = LocalDate.parse(request.getDate())
                .atStartOfDay(ZoneId.of("America/Lima"))
                .toInstant();

        List<Attendance> results = new ArrayList<>();

        for (AttendanceRequest.AttendanceEntry entry : request.getRecords()) {
            Attendance attendance = attendanceRepository
                    .findByStudentAndCourseAndDate(entry.getStudentId(), request.getCourseId(), date)
                    .orElse(Attendance.builder()
                            .student(entry.getStudentId())
                            .course(request.getCourseId())
                            .teacher(teacherId)
                            .date(date)
                            .build());

            attendance.setStatus(entry.getStatus());
            attendance.setArrivalTime(entry.getArrivalTime());
            attendance.setObservations(entry.getObservations());

            results.add(attendanceRepository.save(attendance));
        }

        logger.info("Asistencia registrada: curso={}, fecha={}, registros={}",
                request.getCourseId(), request.getDate(), results.size());
        return results;
    }

    public List<Attendance> getStudentHistory(String studentId) {
        return attendanceRepository.findByStudent(studentId);
    }

    public List<Attendance> getStudentHistoryBetween(String studentId, Instant start, Instant end) {
        return attendanceRepository.findByStudentAndDateBetween(studentId, start, end);
    }

    public List<Attendance> getCourseAttendance(String courseId, Instant date) {
        return attendanceRepository.findByCourseAndDate(courseId, date);
    }

    public Map<String, Object> getStudentStats(String studentId, Instant start, Instant end) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", attendanceRepository.countByStudentAndDateBetween(studentId, start, end));
        stats.put("present", attendanceRepository.countByStudentAndStatusAndDateBetween(studentId, "present", start, end));
        stats.put("absent", attendanceRepository.countByStudentAndStatusAndDateBetween(studentId, "absent", start, end));
        stats.put("late", attendanceRepository.countByStudentAndStatusAndDateBetween(studentId, "late", start, end));
        stats.put("justified", attendanceRepository.countByStudentAndStatusAndDateBetween(studentId, "justified", start, end));
        return stats;
    }

    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    public List<Attendance> findByStudent(String studentId) {
        return attendanceRepository.findByStudent(studentId);
    }

    public List<Attendance> findByCourseAndDate(String courseId, String dateStr) {
        Instant date = LocalDate.parse(dateStr)
                .atStartOfDay(ZoneId.of("America/Lima"))
                .toInstant();
        return attendanceRepository.findByCourseAndDate(courseId, date);
    }

    public Map<String, Object> markAttendance(AttendanceRequest request, String teacherId) {
        List<Attendance> saved = markAttendanceRecords(request, teacherId);
        Map<String, Object> result = new HashMap<>();
        result.put("saved", saved.size());
        result.put("records", saved);
        return result;
    }

    public Map<String, Object> getStats(String dateStr) {
        String date = dateStr != null ? dateStr : LocalDate.now(ZoneId.of("America/Lima")).toString();
        Instant start = LocalDate.parse(date).atStartOfDay(ZoneId.of("America/Lima")).toInstant();
        Instant end = start.plusSeconds(86400);

        List<Attendance> records = attendanceRepository.findAll().stream()
                .filter(a -> a.getDate() != null && !a.getDate().isBefore(start) && a.getDate().isBefore(end))
                .toList();

        long present = records.stream().filter(a -> "present".equals(a.getStatus())).count();
        long absent = records.stream().filter(a -> "absent".equals(a.getStatus())).count();
        long late = records.stream().filter(a -> "late".equals(a.getStatus())).count();
        long justified = records.stream().filter(a -> "justified".equals(a.getStatus())).count();
        double rate = records.isEmpty() ? 0 : (double) (present + late) / records.size() * 100;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", records.size());
        stats.put("present", present);
        stats.put("absent", absent);
        stats.put("late", late);
        stats.put("justified", justified);
        stats.put("attendanceRate", Math.round(rate * 100.0) / 100.0);
        return stats;
    }

    public Map<String, Object> getStudentStats(String studentId) {
        List<Attendance> records = attendanceRepository.findByStudent(studentId);
        long present = records.stream().filter(a -> "present".equals(a.getStatus())).count();
        long absent = records.stream().filter(a -> "absent".equals(a.getStatus())).count();
        long late = records.stream().filter(a -> "late".equals(a.getStatus())).count();
        long justified = records.stream().filter(a -> "justified".equals(a.getStatus())).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", records.size());
        stats.put("present", present);
        stats.put("absent", absent);
        stats.put("late", late);
        stats.put("justified", justified);
        return stats;
    }
}

