package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Parent;
import com.iepca.app.model.Student;
import com.iepca.app.model.embedded.Guardian;
import com.iepca.app.service.AttendanceService;
import com.iepca.app.service.CourseService;
import com.iepca.app.service.GradeService;
import com.iepca.app.service.ParentService;
import com.iepca.app.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parent")
@PreAuthorize("hasRole('padre')")
public class ParentController {

    private final ParentService parentService;
    private final StudentService studentService;
    private final GradeService gradeService;
    private final AttendanceService attendanceService;
    private final CourseService courseService;

    public ParentController(ParentService parentService, StudentService studentService,
                           GradeService gradeService, AttendanceService attendanceService,
                           CourseService courseService) {
        this.parentService = parentService;
        this.studentService = studentService;
        this.gradeService = gradeService;
        this.attendanceService = attendanceService;
        this.courseService = courseService;
    }

    @GetMapping("/children")
    public ResponseEntity<ApiResponse<List<Student>>> getChildren(Authentication authentication) {
        String userId = authentication.getName();
        Parent parent = parentService.findByUserId(userId);
        List<Student> children = new ArrayList<>();
        if (parent.getChildren() != null) {
            for (Guardian guardian : parent.getChildren()) {
                try {
                    Student student = studentService.findById(guardian.getStudent());
                    children.add(student);
                } catch (Exception ignored) {
                    // Student may not exist
                }
            }
        }
        return ResponseEntity.ok(ApiResponse.ok(children));
    }

    @GetMapping("/children/{childId}")
    public ResponseEntity<ApiResponse<Student>> getChild(
            @PathVariable String childId,
            Authentication authentication) {
        Student child = studentService.findById(childId);
        return ResponseEntity.ok(ApiResponse.ok(child));
    }

    @GetMapping("/children/{childId}/grades")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getChildGrades(
            @PathVariable String childId,
            @RequestParam(required = false) Integer period) {
        var grades = gradeService.findByStudent(childId);
        List<Map<String, Object>> response = new ArrayList<>();
        for (var grade : grades) {
            if (period != null && !period.equals(grade.getBimester())) {
                continue;
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", grade.getId());
            item.put("student", grade.getStudent());
            item.put("course", grade.getCourse());
            item.put("bimester", grade.getBimester());
            item.put("academicYear", grade.getAcademicYear());
            item.put("scores", grade.getScores());
            item.put("average", grade.getAverage());
            item.put("status", grade.getStatus());
            item.put("teacher", grade.getTeacher());
            item.put("createdAt", grade.getCreatedAt());
            item.put("updatedAt", grade.getUpdatedAt());

            try {
                var course = courseService.findById(grade.getCourse());
                item.put("courseName", course.getName());
                item.put("courseLabel", course.getFullName());
                item.put("gradeLevel", course.getGradeLevel());
                item.put("section", course.getSection());
            } catch (Exception ignored) {
                item.put("courseName", "Curso academico");
                item.put("courseLabel", "Curso academico");
            }

            response.add(item);
        }
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/children/{childId}/attendance")
    public ResponseEntity<ApiResponse<List<?>>> getChildAttendance(
            @PathVariable String childId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        var attendance = attendanceService.findByStudent(childId);
        return ResponseEntity.ok(ApiResponse.ok(attendance));
    }
}

