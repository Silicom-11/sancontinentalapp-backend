package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.service.JavaResourcesStatusService;
import com.iepca.app.service.SystemHealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final SystemHealthService systemHealthService;
    private final JavaResourcesStatusService javaResourcesStatusService;

    public SystemController(SystemHealthService systemHealthService,
                            JavaResourcesStatusService javaResourcesStatusService) {
        this.systemHealthService = systemHealthService;
        this.javaResourcesStatusService = javaResourcesStatusService;
    }

    @GetMapping("/health-details")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthDetails() {
        return ResponseEntity.ok(ApiResponse.ok(systemHealthService.getSnapshot()));
    }

    @GetMapping("/resources-status")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resourcesStatus(Authentication authentication) {
        String requestedBy = authentication != null ? authentication.getName() : "anonymous";
        return ResponseEntity.ok(ApiResponse.ok(
                "Estado de recursos verificado correctamente",
                javaResourcesStatusService.buildStatusSnapshot(requestedBy)));
    }
}
