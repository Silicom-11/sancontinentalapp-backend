package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Parent;
import com.iepca.app.model.embedded.Guardian;
import com.iepca.app.service.ParentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parents-management")
public class ParentsManagementController {

    private final ParentService parentService;

    public ParentsManagementController(ParentService parentService) {
        this.parentService = parentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Parent>>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        List<Parent> parents = parentService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(parents));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        long totalActive = parentService.countActive();
        List<Parent> allParents = parentService.findAll();
        long withChildren = allParents.stream()
                .filter(p -> p.getChildren() != null && !p.getChildren().isEmpty())
                .count();
        Map<String, Object> stats = Map.of(
                "total", allParents.size(),
                "active", totalActive,
                "withChildren", withChildren,
                "withoutChildren", allParents.size() - withChildren
        );
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Parent>> getById(@PathVariable String id) {
        Parent parent = parentService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(parent));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Parent>> create(@RequestBody Parent parent) {
        Parent created = parentService.create(parent);
        return ResponseEntity.ok(ApiResponse.ok("Padre/Apoderado creado", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Parent>> update(
            @PathVariable String id,
            @RequestBody Parent parent) {
        Parent updated = parentService.update(id, parent);
        return ResponseEntity.ok(ApiResponse.ok("Padre/Apoderado actualizado", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        parentService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Padre/Apoderado desactivado"));
    }

    @PostMapping("/{id}/children")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Parent>> addChild(
            @PathVariable String id,
            @RequestBody Guardian child) {
        Parent updated = parentService.addChild(id, child);
        return ResponseEntity.ok(ApiResponse.ok("Hijo vinculado", updated));
    }

    @DeleteMapping("/{id}/children/{studentId}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Parent>> removeChild(
            @PathVariable String id,
            @PathVariable String studentId) {
        Parent updated = parentService.removeChild(id, studentId);
        return ResponseEntity.ok(ApiResponse.ok("Hijo desvinculado", updated));
    }

    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Parent>> reactivate(@PathVariable String id) {
        Parent parent = parentService.findById(id);
        parent.setIsActive(true);
        Parent updated = parentService.update(id, parent);
        return ResponseEntity.ok(ApiResponse.ok("Padre/Apoderado reactivado", updated));
    }
}

