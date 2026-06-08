package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Justification;
import com.iepca.app.service.JustificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/justifications")
public class JustificationController {

    private final JustificationService justificationService;

    public JustificationController(JustificationService justificationService) {
        this.justificationService = justificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Justification>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String studentId,
            Authentication authentication) {
        List<Justification> justifications;
        if (studentId != null) {
            justifications = justificationService.findByStudent(studentId);
        } else if (status != null) {
            justifications = justificationService.findByStatus(status);
        } else {
            justifications = justificationService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(justifications));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Justification>>> getByStudent(@PathVariable String studentId) {
        List<Justification> justifications = justificationService.findByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok(justifications));
    }

    @GetMapping("/my-justifications")
    @PreAuthorize("hasRole('padre')")
    public ResponseEntity<ApiResponse<List<Justification>>> getMyJustifications(
            Authentication authentication) {
        String parentId = authentication.getName();
        List<Justification> justifications = justificationService.findByParent(parentId);
        return ResponseEntity.ok(ApiResponse.ok(justifications));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('administrativo', 'docente')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = justificationService.getStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Justification>> getById(@PathVariable String id) {
        Justification justification = justificationService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(justification));
    }

    @PostMapping
    @PreAuthorize("hasRole('padre')")
    public ResponseEntity<ApiResponse<Justification>> create(
            @RequestBody Justification justification,
            Authentication authentication) {
        justification.setParent(authentication.getName());
        Justification created = justificationService.submit(justification);
        return ResponseEntity.ok(ApiResponse.ok("JustificaciÃ³n enviada", created));
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('administrativo', 'docente')")
    public ResponseEntity<ApiResponse<Justification>> review(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        String status = body.get("status");
        String reviewNote = body.get("reviewNote");
        String reviewerId = authentication.getName();

        Justification updated;
        if ("approved".equals(status)) {
            updated = justificationService.approve(id, reviewerId);
        } else {
            updated = justificationService.reject(id, reviewerId, reviewNote);
        }
        return ResponseEntity.ok(ApiResponse.ok("JustificaciÃ³n revisada", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('padre')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        justificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("JustificaciÃ³n eliminada"));
    }
}

