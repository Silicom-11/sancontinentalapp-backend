package com.iepca.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceScheduler.class);

    private final LocationService locationService;
    private final MaintenanceService maintenanceService;
    private final int locationRetentionDays;

    public MaintenanceScheduler(LocationService locationService,
                                MaintenanceService maintenanceService,
                                @Value("${maintenance.location-retention-days:30}") int locationRetentionDays) {
        this.locationService = locationService;
        this.maintenanceService = maintenanceService;
        this.locationRetentionDays = locationRetentionDays;
    }

    // GPS: Auto-timeout - marca offline a estudiantes sin actividad en 5 minutos
    @Scheduled(fixedRate = 300_000)
    public void timeoutStaleGpsLocations() {
        locationService.timeoutStaleLocations(5);
    }

    // PREVENTIVO: Limpieza diaria de ubicaciones antiguas (2:00 AM)
    @Scheduled(cron = "${maintenance.cleanup-cron:0 0 2 * * *}")
    public void cleanOldLocationRecords() {
        logger.info("[PREVENTIVO] Limpieza de ubicaciones mayores a {} dias", locationRetentionDays);
        locationService.cleanOldLocations(locationRetentionDays);
    }

    // PREVENTIVO: Limpieza semanal de notificaciones antiguas (Domingo 3:00 AM)
    @Scheduled(cron = "0 0 3 * * SUN")
    public void cleanOldNotifications() {
        logger.info("[PREVENTIVO] Limpieza semanal de notificaciones antiguas");
        maintenanceService.cleanOldNotifications(90);
    }

    // CORRECTIVO: Diagnóstico diario de integridad de datos (4:00 AM)
    @Scheduled(cron = "0 0 4 * * *")
    public void dailyCorrectiveDiagnostics() {
        logger.info("[CORRECTIVO] Diagnóstico diario de integridad del sistema");
        maintenanceService.runCorrectiveDiagnostics();
    }

    // PREVENTIVO: Verificación de salud del sistema cada 6 horas
    @Scheduled(cron = "0 0 */6 * * *")
    public void periodicHealthCheck() {
        logger.info("[PREVENTIVO] Verificación periódica de salud del sistema");
        maintenanceService.runPreventiveMaintenance();
    }

    // ADAPTATIVO: Verificación semanal de compatibilidad (Lunes 5:00 AM)
    @Scheduled(cron = "0 0 5 * * MON")
    public void weeklyAdaptiveCheck() {
        logger.info("[ADAPTATIVO] Verificación semanal de compatibilidad tecnológica");
        maintenanceService.runAdaptiveCheck();
    }

    // PERFECTIVO: Análisis semanal de rendimiento (Miércoles 5:00 AM)
    @Scheduled(cron = "0 0 5 * * WED")
    public void weeklyPerfectiveAnalysis() {
        logger.info("[PERFECTIVO] Análisis semanal de rendimiento y optimización");
        maintenanceService.runPerfectiveAnalysis();
    }
}
