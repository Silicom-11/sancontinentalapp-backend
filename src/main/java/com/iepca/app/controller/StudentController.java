package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Student;
import com.iepca.app.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Student>>> list(
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String search) {
        List<Student> students;
        if (gradeLevel != null && section != null) {
            students = studentService.findByGradeAndSection(gradeLevel, section);
        } else {
            students = studentService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(students));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> getById(@PathVariable String id) {
        Student student = studentService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(student));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('administrativo', 'padre')")
    public ResponseEntity<ApiResponse<Student>> create(@RequestBody Student student) {
        Student created = studentService.create(student);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante registrado", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Student>> update(
            @PathVariable String id,
            @RequestBody Student student) {
        Student updated = studentService.update(id, student);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante actualizado", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        studentService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Estudiante desactivado"));
    }

    @GetMapping("/my-children")
    @PreAuthorize("hasRole('padre')")
    public ResponseEntity<ApiResponse<List<Student>>> getMyChildren(Authentication authentication) {
        String userId = authentication.getName();
        // Delegate to parent service to find children
        List<Student> children = studentService.findByParentUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok(children));
    }
}

