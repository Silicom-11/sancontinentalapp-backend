package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.AcademicYear;
import com.iepca.app.model.GradeLevel;
import com.iepca.app.model.Institution;
import com.iepca.app.model.Subject;
import com.iepca.app.service.AcademicYearService;
import com.iepca.app.service.GradeLevelService;
import com.iepca.app.service.InstitutionService;
import com.iepca.app.service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institution")
public class InstitutionController {

    private final InstitutionService institutionService;
    private final AcademicYearService academicYearService;
    private final GradeLevelService gradeLevelService;
    private final SubjectService subjectService;

    public InstitutionController(InstitutionService institutionService,
                                  AcademicYearService academicYearService,
                                  GradeLevelService gradeLevelService,
                                  SubjectService subjectService) {
        this.institutionService = institutionService;
        this.academicYearService = academicYearService;
        this.gradeLevelService = gradeLevelService;
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Institution>> getInstitution() {
        List<Institution> institutions = institutionService.findAll();
        Institution institution = institutions.isEmpty() ? null : institutions.get(0);
        return ResponseEntity.ok(ApiResponse.ok(institution));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Institution>> updateInstitution(@RequestBody Institution institution) {
        List<Institution> existing = institutionService.findAll();
        Institution updated;
        if (!existing.isEmpty()) {
            updated = institutionService.update(existing.get(0).getId(), institution);
        } else {
            updated = institutionService.create(institution);
        }
        return ResponseEntity.ok(ApiResponse.ok("InstituciÃ³n actualizada", updated));
    }

    // --- Academic Years ---

    @GetMapping("/academic-years")
    public ResponseEntity<ApiResponse<List<AcademicYear>>> getAcademicYears() {
        List<AcademicYear> years = academicYearService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(years));
    }

    @GetMapping("/academic-years/current")
    public ResponseEntity<ApiResponse<AcademicYear>> getCurrentAcademicYear() {
        List<Institution> institutions = institutionService.findAll();
        if (institutions.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(null));
        }
        AcademicYear current = academicYearService.findCurrentByInstitution(institutions.get(0).getId());
        return ResponseEntity.ok(ApiResponse.ok(current));
    }

    @PostMapping("/academic-years")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<AcademicYear>> createAcademicYear(@RequestBody AcademicYear academicYear) {
        AcademicYear created = academicYearService.create(academicYear);
        return ResponseEntity.ok(ApiResponse.ok("AÃ±o acadÃ©mico creado", created));
    }

    @PutMapping("/academic-years/{id}/activate")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<AcademicYear>> activateAcademicYear(@PathVariable String id) {
        AcademicYear activated = academicYearService.activatePeriod(id, 1);
        return ResponseEntity.ok(ApiResponse.ok("AÃ±o acadÃ©mico activado", activated));
    }

    // --- Grade Levels ---

    @GetMapping("/grade-levels")
    public ResponseEntity<ApiResponse<List<GradeLevel>>> getGradeLevels(
            @RequestParam(required = false) String type) {
        List<GradeLevel> levels;
        if (type != null) {
            levels = gradeLevelService.findByType(type);
        } else {
            levels = gradeLevelService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(levels));
    }

    // --- Subjects ---

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<Subject>>> getSubjects(
            @RequestParam(required = false) String area) {
        List<Subject> subjects;
        if (area != null) {
            subjects = subjectService.findByArea(area);
        } else {
            subjects = subjectService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(subjects));
    }

    @PostMapping("/subjects")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Subject>> createSubject(@RequestBody Subject subject) {
        Subject created = subjectService.create(subject);
        return ResponseEntity.ok(ApiResponse.ok("Materia creada", created));
    }

    @PutMapping("/subjects/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Subject>> updateSubject(
            @PathVariable String id,
            @RequestBody Subject subject) {
        Subject updated = subjectService.update(id, subject);
        return ResponseEntity.ok(ApiResponse.ok("Materia actualizada", updated));
    }
}

