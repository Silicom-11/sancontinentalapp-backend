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
    private final int locationRetentionDays;

    public MaintenanceScheduler(LocationService locationService,
                                @Value("${maintenance.location-retention-days:30}") int locationRetentionDays) {
        this.locationService = locationService;
        this.locationRetentionDays = locationRetentionDays;
    }

    @Scheduled(cron = "${maintenance.cleanup-cron:0 0 2 * * *}")
    public void cleanOldLocationRecords() {
        logger.info("Iniciando mantenimiento programado: limpieza de ubicaciones mayores a {} dias",
                locationRetentionDays);
        locationService.cleanOldLocations(locationRetentionDays);
    }
}
