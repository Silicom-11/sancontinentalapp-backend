package com.iepca.app.controller;

import com.iepca.app.dto.request.GradeUpdateRequest;
import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Grade;
import com.iepca.app.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer bimester) {
        Map<String, Object> stats = gradeService.getStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Grade>>> getByCourse(
            @PathVariable String courseId,
            @RequestParam(required = false) Integer bimester) {
        List<Grade> grades;
        if (bimester != null) {
            grades = gradeService.findByCourseAndBimester(courseId, bimester);
        } else {
            grades = gradeService.findByCourse(courseId);
        }
        return ResponseEntity.ok(ApiResponse.ok(grades));
    }

    @GetMapping("/history/{studentId}")
    public ResponseEntity<ApiResponse<List<Grade>>> getStudentHistory(
            @PathVariable String studentId,
            @RequestParam(required = false) Integer year) {
        List<Grade> grades = gradeService.findByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok(grades));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Grade>>> list(
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) Integer bimester) {
        List<Grade> grades;
        if (courseId != null && bimester != null) {
            grades = gradeService.findByCourseAndBimester(courseId, bimester);
        } else if (studentId != null) {
            grades = gradeService.findByStudent(studentId);
        } else if (courseId != null) {
            grades = gradeService.findByCourse(courseId);
        } else {
            grades = gradeService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(grades));
    }

    @PostMapping("/save-score")
    @PreAuthorize("hasAnyRole('docente', 'administrativo', 'director')")
    public ResponseEntity<ApiResponse<Grade>> saveScore(
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        String studentId = (String) body.get("studentId");
        String courseId = (String) body.get("courseId");
        String evaluationId = (String) body.get("evaluationId");
        Integer bimester = (Integer) body.get("bimester");
        Double score = body.get("score") != null ? ((Number) body.get("score")).doubleValue() : null;
        String comments = (String) body.get("comments");
        String teacherId = authentication.getName();

        Grade grade = gradeService.findOrCreate(studentId, courseId, bimester, teacherId);
        if (evaluationId != null && score != null) {
            grade = gradeService.updateScore(grade.getId(), evaluationId, score, comments);
        }
        return ResponseEntity.ok(ApiResponse.ok("Nota guardada", grade));
    }

    @PostMapping("/save-bulk")
    @PreAuthorize("hasAnyRole('docente', 'administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> saveBulk(
            @Valid @RequestBody GradeUpdateRequest request,
            Authentication authentication) {
        String teacherId = authentication.getName();
        gradeService.bulkUpdate(request.getCourseId(), request.getBimester(), teacherId, request.getGrades());
        return ResponseEntity.ok(ApiResponse.<Void>ok("Notas guardadas masivamente"));
    }

    @PutMapping("/close-bimester")
    @PreAuthorize("hasAnyRole('docente', 'administrativo')")
    public ResponseEntity<ApiResponse<Void>> closeBimester(@RequestBody Map<String, Object> body) {
        // Close bimester logic - update grade status
        return ResponseEntity.ok(ApiResponse.<Void>ok("Bimestre cerrado"));
    }

    @PutMapping("/publish-bimester")
    @PreAuthorize("hasAnyRole('docente', 'administrativo')")
    public ResponseEntity<ApiResponse<Void>> publishBimester(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.<Void>ok("Bimestre publicado"));
    }

    @PutMapping("/reopen-bimester")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> reopenBimester(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.<Void>ok("Bimestre reabierto"));
    }

    @GetMapping("/report/{courseId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseReport(
            @PathVariable String courseId,
            @RequestParam(required = false) Integer bimester) {
        List<Grade> grades = gradeService.findByCourse(courseId);
        double average = grades.stream()
                .filter(g -> g.getAverage() != null)
                .mapToDouble(Grade::getAverage)
                .average().orElse(0.0);
        long ad = grades.stream().filter(g -> g.getAverage() != null && g.getAverage() >= 18).count();
        long a = grades.stream().filter(g -> g.getAverage() != null && g.getAverage() >= 14 && g.getAverage() < 18).count();
        long b = grades.stream().filter(g -> g.getAverage() != null && g.getAverage() >= 11 && g.getAverage() < 14).count();
        long c = grades.stream().filter(g -> g.getAverage() != null && g.getAverage() < 11).count();
        Map<String, Object> report = Map.of(
                "courseId", courseId,
                "totalStudents", grades.size(),
                "average", Math.round(average * 100.0) / 100.0,
                "distribution", Map.of("AD", ad, "A", a, "B", b, "C", c),
                "grades", grades
        );
        return ResponseEntity.ok(ApiResponse.ok(report));
    }
}

