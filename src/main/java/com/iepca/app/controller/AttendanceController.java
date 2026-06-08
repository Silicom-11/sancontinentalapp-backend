package com.iepca.app.controller;

import com.iepca.app.dto.request.AttendanceRequest;
import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Attendance;
import com.iepca.app.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(
            @RequestParam(required = false) String date) {
        Map<String, Object> stats = attendanceService.getStats(date);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Attendance>>> list(
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status) {
        List<Attendance> records;
        if (courseId != null && date != null) {
            records = attendanceService.findByCourseAndDate(courseId, date);
        } else if (studentId != null) {
            records = attendanceService.findByStudent(studentId);
        } else {
            records = attendanceService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(records));
    }

    @GetMapping("/course/{courseId}/date/{date}")
    @PreAuthorize("hasAnyRole('docente', 'administrativo', 'director')")
    public ResponseEntity<ApiResponse<List<Attendance>>> getByCourseAndDate(
            @PathVariable String courseId,
            @PathVariable String date) {
        List<Attendance> records = attendanceService.findByCourseAndDate(courseId, date);
        return ResponseEntity.ok(ApiResponse.ok(records));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('docente', 'administrativo', 'director')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAttendance(
            @Valid @RequestBody AttendanceRequest request,
            Authentication authentication) {
        String teacherId = authentication.getName();
        Map<String, Object> result = attendanceService.markAttendance(request, teacherId);
        return ResponseEntity.ok(ApiResponse.ok("Asistencia registrada", result));
    }

    @GetMapping("/stats/{studentId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentStats(
            @PathVariable String studentId,
            @RequestParam(required = false) Integer year) {
        Map<String, Object> stats = attendanceService.getStudentStats(studentId);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('docente', 'administrativo', 'director')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveBulkAttendance(
            @Valid @RequestBody AttendanceRequest request,
            Authentication authentication) {
        String teacherId = authentication.getName();
        Map<String, Object> result = attendanceService.markAttendance(request, teacherId);
        int count = request.getRecords() != null ? request.getRecords().size() : 0;
        return ResponseEntity.ok(ApiResponse.ok("Asistencia registrada: " + count + " registros",
                Map.of("saved", count, "result", result)));
    }
}

