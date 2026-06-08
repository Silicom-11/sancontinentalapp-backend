package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Course;
import com.iepca.app.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        List<Course> courses = courseService.findAll();
        long activeCourses = courses.size();
        int totalStudents = courses.stream()
                .mapToInt(c -> c.getStudents() != null ? c.getStudents().size() : 0)
                .sum();
        double avgStudents = activeCourses > 0 ? (double) totalStudents / activeCourses : 0;

        Map<String, Object> stats = Map.of(
                "totalCourses", activeCourses,
                "activeCourses", activeCourses,
                "totalStudents", totalStudents,
                "avgStudentsPerCourse", Math.round(avgStudents * 100.0) / 100.0
        );
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('docente')")
    public ResponseEntity<ApiResponse<List<Course>>> getMyCourses(Authentication authentication) {
        String teacherId = authentication.getName();
        List<Course> courses = courseService.findByTeacher(teacherId);
        return ResponseEntity.ok(ApiResponse.ok(courses));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> list(
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String teacherId) {
        List<Course> courses;
        if (teacherId != null) {
            courses = courseService.findByTeacher(teacherId);
        } else {
            courses = courseService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> getById(@PathVariable String id) {
        Course course = courseService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(course));
    }

    @PostMapping
    @PreAuthorize("hasRole('administrativo')")
    public ResponseEntity<ApiResponse<Course>> create(@RequestBody Course course) {
        Course created = courseService.create(course);
        return ResponseEntity.ok(ApiResponse.ok("Curso creado exitosamente", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Course>> update(
            @PathVariable String id,
            @RequestBody Course course) {
        Course updated = courseService.update(id, course);
        return ResponseEntity.ok(ApiResponse.ok("Curso actualizado", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        courseService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Curso desactivado"));
    }

    @PostMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Course>> addStudent(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String studentId = body.get("studentId");
        Course course = courseService.addStudent(id, studentId);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante agregado al curso", course));
    }

    @DeleteMapping("/{id}/students/{studentId}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Course>> removeStudent(
            @PathVariable String id,
            @PathVariable String studentId) {
        Course course = courseService.removeStudent(id, studentId);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante removido del curso", course));
    }
}

