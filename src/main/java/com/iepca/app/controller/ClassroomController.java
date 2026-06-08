package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Classroom;
import com.iepca.app.service.ClassroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Classroom>>> list(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String gradeLevel) {
        List<Classroom> classrooms;
        if (academicYear != null) {
            classrooms = classroomService.findByAcademicYear(academicYear);
        } else if (gradeLevel != null) {
            classrooms = classroomService.findByGradeLevel(gradeLevel);
        } else {
            classrooms = classroomService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(classrooms));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Classroom>> getById(@PathVariable String id) {
        Classroom classroom = classroomService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(classroom));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Classroom>> create(@RequestBody Classroom classroom) {
        Classroom created = classroomService.create(classroom);
        return ResponseEntity.ok(ApiResponse.ok("Aula creada exitosamente", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Classroom>> update(
            @PathVariable String id,
            @RequestBody Classroom classroom) {
        Classroom updated = classroomService.update(id, classroom);
        return ResponseEntity.ok(ApiResponse.ok("Aula actualizada", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        classroomService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Aula desactivada"));
    }
}

