package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Teacher;
import com.iepca.app.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Teacher>>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        List<Teacher> teachers;
        if (specialty != null) {
            teachers = teacherService.findBySpecialty(specialty);
        } else {
            teachers = teacherService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(teachers));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        long totalActive = teacherService.countActive();
        List<Teacher> all = teacherService.findAll();
        Map<String, Object> stats = Map.of(
                "total", all.size(),
                "active", totalActive
        );
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Teacher>> getById(@PathVariable String id) {
        Teacher teacher = teacherService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(teacher));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Teacher>> create(@RequestBody Teacher teacher) {
        Teacher created = teacherService.create(teacher);
        return ResponseEntity.ok(ApiResponse.ok("Docente creado", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Teacher>> update(
            @PathVariable String id,
            @RequestBody Teacher teacher) {
        Teacher updated = teacherService.update(id, teacher);
        return ResponseEntity.ok(ApiResponse.ok("Docente actualizado", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        teacherService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Docente desactivado"));
    }

    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Teacher>> reactivate(@PathVariable String id) {
        Teacher teacher = teacherService.findById(id);
        teacher.setIsActive(true);
        Teacher updated = teacherService.update(id, teacher);
        return ResponseEntity.ok(ApiResponse.ok("Docente reactivado", updated));
    }
}

