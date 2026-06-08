package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Institution;
import com.iepca.app.service.InstitutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academic-settings")
public class AcademicSettingsController {

    private final InstitutionService institutionService;

    public AcademicSettingsController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getSettings() {
        List<Institution> institutions = institutionService.findAll();
        if (!institutions.isEmpty()) {
            Institution inst = institutions.get(0);
            Map<String, Object> settings = Map.of(
                    "evaluationSystem", inst.getEvaluationSystem() != null ? inst.getEvaluationSystem() : Map.of(),
                    "gradeScale", inst.getGradeScale() != null ? inst.getGradeScale() : Map.of(),
                    "shifts", inst.getShifts() != null ? inst.getShifts() : List.of()
            );
            return ResponseEntity.ok(ApiResponse.ok(settings));
        }
        return ResponseEntity.ok(ApiResponse.ok(Map.of()));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Institution>> updateSettings(@RequestBody Institution institution) {
        List<Institution> existing = institutionService.findAll();
        Institution updated;
        if (!existing.isEmpty()) {
            updated = institutionService.update(existing.get(0).getId(), institution);
        } else {
            updated = institutionService.create(institution);
        }
        return ResponseEntity.ok(ApiResponse.ok("ConfiguraciÃ³n acadÃ©mica actualizada", updated));
    }
}

