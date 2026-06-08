package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Enrollment;
import com.iepca.app.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Enrollment>>> list(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String status) {
        List<Enrollment> enrollments;
        if (academicYear != null) {
            enrollments = enrollmentService.findByAcademicYear(academicYear);
        } else if (status != null) {
            enrollments = enrollmentService.findByStatus(status);
        } else {
            enrollments = enrollmentService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(enrollments));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Enrollment>>> getByStudent(@PathVariable String studentId) {
        List<Enrollment> enrollments = enrollmentService.findByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok(enrollments));
    }

    @GetMapping("/student/{studentId}/current")
    public ResponseEntity<ApiResponse<Enrollment>> getCurrentByStudent(@PathVariable String studentId) {
        List<Enrollment> enrollments = enrollmentService.findByStudent(studentId);
        Enrollment current = enrollments.stream()
                .filter(e -> "activa".equals(e.getStatus()))
                .findFirst()
                .orElse(null);
        return ResponseEntity.ok(ApiResponse.ok(current));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Enrollment>> create(@RequestBody Enrollment enrollment) {
        Enrollment created = enrollmentService.create(enrollment);
        return ResponseEntity.ok(ApiResponse.ok("MatrÃ­cula creada exitosamente", created));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Enrollment>> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            org.springframework.security.core.Authentication authentication) {
        String newStatus = body.get("status");
        String reason = body.get("reason");
        String changedBy = authentication.getName();
        Enrollment updated = enrollmentService.updateStatus(id, newStatus, reason, changedBy);
        return ResponseEntity.ok(ApiResponse.ok("Estado de matrÃ­cula actualizado", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        enrollmentService.updateStatus(id, "anulada", "Eliminada por administrador", null);
        return ResponseEntity.ok(ApiResponse.<Void>ok("MatrÃ­cula anulada"));
    }
}

