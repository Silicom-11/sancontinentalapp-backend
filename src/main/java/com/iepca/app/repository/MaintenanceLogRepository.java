package com.iepca.app.repository;

import com.iepca.app.model.MaintenanceLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface MaintenanceLogRepository extends MongoRepository<MaintenanceLog, String> {

    List<MaintenanceLog> findByTypeOrderByCreatedAtDesc(String type);

    List<MaintenanceLog> findByStatusOrderByCreatedAtDesc(String status);

    List<MaintenanceLog> findByTypeAndStatusOrderByCreatedAtDesc(String type, String status);

    long countByType(String type);

    long countByStatus(String status);

    long countByTypeAndStatus(String type, String status);

    List<MaintenanceLog> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to);

    void deleteByCreatedAtBefore(Instant before);
}
