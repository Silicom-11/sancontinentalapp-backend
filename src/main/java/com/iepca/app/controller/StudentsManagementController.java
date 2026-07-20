package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Student;
import com.iepca.app.model.embedded.Guardian;
import com.iepca.app.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students-management")
@PreAuthorize("hasAnyRole('administrativo', 'director')")
public class StudentsManagementController {

    private final StudentService studentService;

    public StudentsManagementController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Student>>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        List<Student> students;
        if (gradeLevel != null && section != null) {
            students = studentService.findByGradeAndSection(gradeLevel, section);
        } else {
            students = studentService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(students));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        long totalActive = studentService.countActive();
        List<Student> allStudents = studentService.findAll();
        Map<String, Object> stats = Map.of(
                "total", allStudents.size(),
                "active", totalActive
        );
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> getById(@PathVariable String id) {
        Student student = studentService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(student));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Student>> create(@RequestBody Student student) {
        Student created = studentService.create(student);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante creado", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> update(
            @PathVariable String id,
            @RequestBody Student student) {
        Student updated = studentService.update(id, student);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante actualizado", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        studentService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Estudiante desactivado"));
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<ApiResponse<Student>> reactivate(@PathVariable String id) {
        Student student = studentService.findById(id);
        student.setIsActive(true);
        student.setStatus("activo");
        Student updated = studentService.update(id, student);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante reactivado", updated));
    }

    @PostMapping("/{id}/guardians")
    public ResponseEntity<ApiResponse<Student>> addGuardian(
            @PathVariable String id,
            @RequestBody Guardian guardian) {
        Student student = studentService.findById(id);
        if (student.getGuardians() == null) student.setGuardians(new ArrayList<>());
        student.getGuardians().add(guardian);
        Student updated = studentService.update(id, student);
        return ResponseEntity.ok(ApiResponse.ok("Apoderado vinculado", updated));
    }

    @DeleteMapping("/{id}/guardians/{guardianId}")
    public ResponseEntity<ApiResponse<Student>> removeGuardian(
            @PathVariable String id,
            @PathVariable String guardianId) {
        Student student = studentService.findById(id);
        if (student.getGuardians() != null) {
            student.getGuardians().removeIf(g -> guardianId.equals(g.getStudent()));
        }
        Student updated = studentService.update(id, student);
        return ResponseEntity.ok(ApiResponse.ok("Apoderado desvinculado", updated));
    }
}

