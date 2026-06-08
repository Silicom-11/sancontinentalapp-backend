package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.dto.response.DashboardStatsResponse;
import com.iepca.app.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getAdminDashboard() {
        DashboardStatsResponse stats = dashboardService.getAdminStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('docente')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getTeacherDashboard(
            Authentication authentication) {
        String teacherId = authentication.getName();
        DashboardStatsResponse stats = dashboardService.getTeacherStats(teacherId);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/parent")
    @PreAuthorize("hasRole('padre')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getParentDashboard(
            Authentication authentication) {
        String parentId = authentication.getName();
        DashboardStatsResponse stats = dashboardService.getParentStats(parentId);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}

