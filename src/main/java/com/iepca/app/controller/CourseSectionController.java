package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.CourseSection;
import com.iepca.app.service.CourseSectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-sections")
public class CourseSectionController {

    private final CourseSectionService courseSectionService;

    public CourseSectionController(CourseSectionService courseSectionService) {
        this.courseSectionService = courseSectionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseSection>>> list(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String classroom,
            @RequestParam(required = false) String teacher) {
        List<CourseSection> sections;
        if (teacher != null) {
            sections = courseSectionService.findByTeacher(teacher);
        } else if (classroom != null) {
            sections = courseSectionService.findByClassroom(classroom);
        } else if (academicYear != null) {
            sections = courseSectionService.findByAcademicYear(academicYear);
        } else {
            sections = courseSectionService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(sections));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<CourseSection>>> getByTeacher(@PathVariable String teacherId) {
        List<CourseSection> sections = courseSectionService.findByTeacher(teacherId);
        return ResponseEntity.ok(ApiResponse.ok(sections));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseSection>> getById(@PathVariable String id) {
        CourseSection section = courseSectionService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(section));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<CourseSection>> create(@RequestBody CourseSection section) {
        CourseSection created = courseSectionService.create(section);
        return ResponseEntity.ok(ApiResponse.ok("SecciÃ³n de curso creada", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director', 'docente')")
    public ResponseEntity<ApiResponse<CourseSection>> update(
            @PathVariable String id,
            @RequestBody CourseSection section) {
        CourseSection updated = courseSectionService.update(id, section);
        return ResponseEntity.ok(ApiResponse.ok("SecciÃ³n de curso actualizada", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        courseSectionService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("SecciÃ³n de curso desactivada"));
    }

    @PostMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<CourseSection>> addStudent(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String studentId = body.get("studentId");
        CourseSection section = courseSectionService.addStudent(id, studentId);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante agregado", section));
    }

    @DeleteMapping("/{id}/students/{studentId}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<CourseSection>> removeStudent(
            @PathVariable String id,
            @PathVariable String studentId) {
        CourseSection section = courseSectionService.removeStudent(id, studentId);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante removido", section));
    }
}

