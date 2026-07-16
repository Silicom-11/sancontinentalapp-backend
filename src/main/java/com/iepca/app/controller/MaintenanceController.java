package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.MaintenanceLog;
import com.iepca.app.service.MaintenanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
@PreAuthorize("hasAnyRole('administrativo', 'director')")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    // ===== MANTENIMIENTO CORRECTIVO =====

    @GetMapping("/correctivo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> correctiveDiagnostics() {
        return ResponseEntity.ok(ApiResponse.ok(
                "Diagnóstico correctivo ejecutado",
                maintenanceService.runCorrectiveDiagnostics()));
    }

    @PostMapping("/correctivo/unlock-accounts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> unlockAccounts() {
        return ResponseEntity.ok(ApiResponse.ok(
                "Cuentas desbloqueadas exitosamente",
                maintenanceService.unlockAllAccounts()));
    }

    // ===== MANTENIMIENTO PREVENTIVO =====

    @GetMapping("/preventivo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> preventiveMaintenance() {
        return ResponseEntity.ok(ApiResponse.ok(
                "Mantenimiento preventivo ejecutado",
                maintenanceService.runPreventiveMaintenance()));
    }

    @PostMapping("/preventivo/clean-notifications")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cleanNotifications(
            @RequestParam(defaultValue = "90") int daysOld) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Limpieza de notificaciones completada",
                maintenanceService.cleanOldNotifications(daysOld)));
    }

    // ===== MANTENIMIENTO PERFECTIVO =====

    @GetMapping("/perfectivo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> perfectiveAnalysis() {
        return ResponseEntity.ok(ApiResponse.ok(
                "Análisis perfectivo ejecutado",
                maintenanceService.runPerfectiveAnalysis()));
    }

    // ===== MANTENIMIENTO ADAPTATIVO =====

    @GetMapping("/adaptativo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> adaptiveCheck() {
        return ResponseEntity.ok(ApiResponse.ok(
                "Verificación adaptativa ejecutada",
                maintenanceService.runAdaptiveCheck()));
    }

    // ===== DASHBOARD Y REPORTES =====

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(
                "Dashboard de mantenimiento",
                maintenanceService.getMaintenanceDashboard()));
    }

    @GetMapping("/logs/{type}")
    public ResponseEntity<ApiResponse<List<MaintenanceLog>>> logsByType(@PathVariable String type) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Logs de mantenimiento " + type,
                maintenanceService.getLogsByType(type)));
    }

    @PostMapping("/full")
    public ResponseEntity<ApiResponse<Map<String, Object>>> fullMaintenance() {
        return ResponseEntity.ok(ApiResponse.ok(
                "Mantenimiento completo ejecutado exitosamente",
                maintenanceService.runFullMaintenance()));
    }
}
