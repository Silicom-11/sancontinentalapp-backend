package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Evaluation;
import com.iepca.app.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Evaluation>>> getByCourse(
            @PathVariable String courseId,
            @RequestParam(required = false) Integer bimester) {
        List<Evaluation> evaluations;
        if (bimester != null) {
            evaluations = evaluationService.findByCourseAndBimester(courseId, bimester);
        } else {
            evaluations = evaluationService.findByCourse(courseId);
        }
        return ResponseEntity.ok(ApiResponse.ok(evaluations));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('docente', 'administrativo', 'director')")
    public ResponseEntity<ApiResponse<Evaluation>> create(@RequestBody Evaluation evaluation) {
        Evaluation created = evaluationService.create(evaluation);
        return ResponseEntity.ok(ApiResponse.ok("EvaluaciÃ³n creada", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('docente', 'administrativo', 'director')")
    public ResponseEntity<ApiResponse<Evaluation>> update(
            @PathVariable String id,
            @RequestBody Evaluation evaluation) {
        Evaluation updated = evaluationService.update(id, evaluation);
        return ResponseEntity.ok(ApiResponse.ok("EvaluaciÃ³n actualizada", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('docente', 'administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        evaluationService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("EvaluaciÃ³n desactivada"));
    }
}

