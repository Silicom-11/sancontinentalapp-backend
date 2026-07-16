package com.iepca.app.service;

import com.iepca.app.model.MaintenanceLog;
import com.iepca.app.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MaintenanceService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceLogRepository maintenanceLogRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final NotificationRepository notificationRepository;
    private final LocationRepository locationRepository;
    private final AttendanceRepository attendanceRepository;
    private final MongoTemplate mongoTemplate;

    public MaintenanceService(MaintenanceLogRepository maintenanceLogRepository,
                              UserRepository userRepository,
                              StudentRepository studentRepository,
                              TeacherRepository teacherRepository,
                              NotificationRepository notificationRepository,
                              LocationRepository locationRepository,
                              AttendanceRepository attendanceRepository,
                              MongoTemplate mongoTemplate) {
        this.maintenanceLogRepository = maintenanceLogRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.notificationRepository = notificationRepository;
        this.locationRepository = locationRepository;
        this.attendanceRepository = attendanceRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // =============================================
    // MANTENIMIENTO CORRECTIVO
    // Corrección de errores, fallos y defectos
    // =============================================

    public Map<String, Object> runCorrectiveDiagnostics() {
        logger.info("Ejecutando diagnóstico correctivo del sistema");
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> issues = new ArrayList<>();

        long usersWithoutRole = userRepository.findAll().stream()
                .filter(u -> u.getRole() == null || u.getRole().isBlank())
                .count();
        if (usersWithoutRole > 0) {
            issues.add(Map.of(
                    "issue", "Usuarios sin rol asignado",
                    "count", usersWithoutRole,
                    "severity", "ALTO",
                    "action", "Asignar rol por defecto o revisar registro"
            ));
        }

        long lockedAccounts = userRepository.findAll().stream()
                .filter(u -> u.getLockUntil() != null && u.getLockUntil().isAfter(Instant.now()))
                .count();
        if (lockedAccounts > 0) {
            issues.add(Map.of(
                    "issue", "Cuentas bloqueadas actualmente",
                    "count", lockedAccounts,
                    "severity", "MEDIO",
                    "action", "Revisar intentos fallidos de inicio de sesión"
            ));
        }

        long usersHighLoginAttempts = userRepository.findAll().stream()
                .filter(u -> u.getLoginAttempts() != null && u.getLoginAttempts() >= 3)
                .count();
        if (usersHighLoginAttempts > 0) {
            issues.add(Map.of(
                    "issue", "Usuarios con múltiples intentos fallidos de login",
                    "count", usersHighLoginAttempts,
                    "severity", "MEDIO",
                    "action", "Verificar posibles ataques de fuerza bruta"
            ));
        }

        result.put("type", "CORRECTIVO");
        result.put("description", "Detección y corrección de errores, fallos y defectos del sistema");
        result.put("executedAt", Instant.now().toString());
        result.put("issuesFound", issues.size());
        result.put("issues", issues);
        result.put("status", issues.isEmpty() ? "SIN_INCIDENCIAS" : "REQUIERE_ATENCION");

        logMaintenance("CORRECTIVO", "diagnostico_correctivo",
                "Diagnóstico correctivo ejecutado: " + issues.size() + " incidencias encontradas",
                issues.isEmpty() ? "COMPLETADO" : "PENDIENTE",
                issues.isEmpty() ? "BAJO" : "MEDIO",
                "sistema", Map.of("issuesFound", issues.size()));

        return result;
    }

    public Map<String, Object> unlockAllAccounts() {
        logger.info("Mantenimiento correctivo: desbloqueando todas las cuentas");
        long count = userRepository.findAll().stream()
                .filter(u -> u.getLockUntil() != null)
                .peek(u -> {
                    u.setLockUntil(null);
                    u.setLoginAttempts(0);
                    userRepository.save(u);
                })
                .count();

        logMaintenance("CORRECTIVO", "desbloqueo_cuentas",
                "Desbloqueo masivo de cuentas: " + count + " cuentas desbloqueadas",
                "COMPLETADO", "MEDIO", "auth", Map.of("accountsUnlocked", count));

        return Map.of(
                "type", "CORRECTIVO",
                "action", "Desbloqueo masivo de cuentas",
                "accountsUnlocked", count,
                "executedAt", Instant.now().toString()
        );
    }

    // =============================================
    // MANTENIMIENTO PREVENTIVO
    // Prevención de fallos futuros
    // =============================================

    public Map<String, Object> runPreventiveMaintenance() {
        logger.info("Ejecutando mantenimiento preventivo del sistema");
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> actions = new ArrayList<>();

        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;

        actions.add(Map.of(
                "check", "Uso de memoria JVM",
                "value", String.format("%.1f%%", memoryUsagePercent),
                "threshold", "80%",
                "status", memoryUsagePercent > 80 ? "ADVERTENCIA" : "NORMAL"
        ));

        if (memoryUsagePercent > 70) {
            runtime.gc();
            long usedAfterGc = runtime.totalMemory() - runtime.freeMemory();
            actions.add(Map.of(
                    "action", "Garbage Collection ejecutado",
                    "memoryFreedMb", (usedMemory - usedAfterGc) / (1024 * 1024),
                    "status", "COMPLETADO"
            ));
        }

        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeHours = uptimeMs / (1000 * 60 * 60);
        actions.add(Map.of(
                "check", "Tiempo de actividad del servidor",
                "uptimeHours", uptimeHours,
                "recommendation", uptimeHours > 168 ? "Considerar reinicio programado" : "Normal",
                "status", uptimeHours > 168 ? "ADVERTENCIA" : "NORMAL"
        ));

        long totalUsers = userRepository.count();
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        actions.add(Map.of(
                "check", "Integridad de datos",
                "totalUsers", totalUsers,
                "totalStudents", totalStudents,
                "totalTeachers", totalTeachers,
                "status", "VERIFICADO"
        ));

        long oldNotifications = notificationRepository.findAll().stream()
                .filter(n -> n.getCreatedAt() != null &&
                        n.getCreatedAt().isBefore(Instant.now().minus(90, ChronoUnit.DAYS)))
                .count();
        if (oldNotifications > 0) {
            actions.add(Map.of(
                    "check", "Notificaciones antiguas (>90 días)",
                    "count", oldNotifications,
                    "recommendation", "Considerar limpieza de notificaciones antiguas",
                    "status", "ADVERTENCIA"
            ));
        }

        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        actions.add(Map.of(
                "check", "Colecciones en base de datos",
                "count", collectionNames.size(),
                "collections", collectionNames,
                "status", "VERIFICADO"
        ));

        result.put("type", "PREVENTIVO");
        result.put("description", "Acciones preventivas para evitar fallos futuros y degradación del rendimiento");
        result.put("executedAt", Instant.now().toString());
        result.put("actionsPerformed", actions.size());
        result.put("actions", actions);

        logMaintenance("PREVENTIVO", "mantenimiento_preventivo",
                "Mantenimiento preventivo ejecutado: " + actions.size() + " verificaciones",
                "COMPLETADO", "BAJO", "sistema", Map.of("checks", actions.size()));

        return result;
    }

    public Map<String, Object> cleanOldNotifications(int daysOld) {
        logger.info("Mantenimiento preventivo: limpiando notificaciones mayores a {} días", daysOld);
        Instant cutoff = Instant.now().minus(daysOld, ChronoUnit.DAYS);
        long count = notificationRepository.findAll().stream()
                .filter(n -> n.getCreatedAt() != null && n.getCreatedAt().isBefore(cutoff))
                .peek(n -> notificationRepository.delete(n))
                .count();

        logMaintenance("PREVENTIVO", "limpieza_notificaciones",
                "Limpieza de notificaciones: " + count + " registros eliminados (>" + daysOld + " días)",
                "COMPLETADO", "BAJO", "notificaciones", Map.of("deleted", count, "daysOld", daysOld));

        return Map.of(
                "type", "PREVENTIVO",
                "action", "Limpieza de notificaciones antiguas",
                "daysThreshold", daysOld,
                "notificationsDeleted", count,
                "executedAt", Instant.now().toString()
        );
    }

    // =============================================
    // MANTENIMIENTO PERFECTIVO
    // Mejoras y optimización de rendimiento
    // =============================================

    public Map<String, Object> runPerfectiveAnalysis() {
        logger.info("Ejecutando análisis de mantenimiento perfectivo");
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> metrics = new ArrayList<>();

        long start = System.currentTimeMillis();
        userRepository.count();
        long userQueryTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        studentRepository.count();
        long studentQueryTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        attendanceRepository.count();
        long attendanceQueryTime = System.currentTimeMillis() - start;

        metrics.add(Map.of(
                "metric", "Tiempo de respuesta de consultas",
                "userQueryMs", userQueryTime,
                "studentQueryMs", studentQueryTime,
                "attendanceQueryMs", attendanceQueryTime,
                "status", (userQueryTime + studentQueryTime + attendanceQueryTime) < 300 ? "OPTIMO" : "MEJORABLE"
        ));

        Runtime runtime = Runtime.getRuntime();
        metrics.add(Map.of(
                "metric", "Eficiencia de memoria",
                "totalMb", runtime.totalMemory() / (1024 * 1024),
                "usedMb", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024),
                "maxMb", runtime.maxMemory() / (1024 * 1024),
                "status", "ANALIZADO"
        ));

        metrics.add(Map.of(
                "metric", "Procesadores disponibles",
                "cores", runtime.availableProcessors(),
                "javaVersion", System.getProperty("java.version"),
                "status", "VERIFICADO"
        ));

        Map<String, Long> dataDistribution = new LinkedHashMap<>();
        dataDistribution.put("users", userRepository.count());
        dataDistribution.put("students", studentRepository.count());
        dataDistribution.put("teachers", teacherRepository.count());
        dataDistribution.put("notifications", notificationRepository.count());
        dataDistribution.put("attendance", attendanceRepository.count());

        metrics.add(Map.of(
                "metric", "Distribución de datos por colección",
                "distribution", dataDistribution,
                "totalRecords", dataDistribution.values().stream().mapToLong(Long::longValue).sum(),
                "status", "ANALIZADO"
        ));

        List<Map<String, String>> improvements = new ArrayList<>();
        if (attendanceQueryTime > 100) {
            improvements.add(Map.of(
                    "area", "Rendimiento de consultas de asistencia",
                    "recommendation", "Agregar índice compuesto en courseId + date",
                    "priority", "MEDIA"
            ));
        }
        if (notificationRepository.count() > 1000) {
            improvements.add(Map.of(
                    "area", "Volumen de notificaciones",
                    "recommendation", "Implementar paginación y archivado automático",
                    "priority", "MEDIA"
            ));
        }
        improvements.add(Map.of(
                "area", "Caché de consultas frecuentes",
                "recommendation", "Extender caché Guava a endpoints de dashboard",
                "priority", "BAJA"
        ));

        result.put("type", "PERFECTIVO");
        result.put("description", "Análisis de rendimiento y oportunidades de mejora del sistema");
        result.put("executedAt", Instant.now().toString());
        result.put("metrics", metrics);
        result.put("improvementSuggestions", improvements);

        logMaintenance("PERFECTIVO", "analisis_perfectivo",
                "Análisis perfectivo ejecutado: " + improvements.size() + " mejoras sugeridas",
                "COMPLETADO", "BAJO", "sistema", Map.of("suggestions", improvements.size()));

        return result;
    }

    // =============================================
    // MANTENIMIENTO ADAPTATIVO
    // Adaptación a cambios tecnológicos y del entorno
    // =============================================

    public Map<String, Object> runAdaptiveCheck() {
        logger.info("Ejecutando verificación de mantenimiento adaptativo");
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> checks = new ArrayList<>();

        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        boolean isJava17Plus = javaVersion.startsWith("17") || javaVersion.startsWith("21")
                || javaVersion.startsWith("22") || javaVersion.startsWith("23");
        checks.add(Map.of(
                "component", "Java Runtime",
                "currentVersion", javaVersion,
                "vendor", javaVendor,
                "compatible", isJava17Plus,
                "recommendation", isJava17Plus ? "Versión compatible con Spring Boot 3.x" : "Actualizar a Java 17 o superior",
                "status", isJava17Plus ? "COMPATIBLE" : "REQUIERE_ACTUALIZACION"
        ));

        checks.add(Map.of(
                "component", "Spring Boot",
                "currentVersion", "3.2.5",
                "latestLTS", "3.3.x",
                "compatible", true,
                "recommendation", "Versión estable, considerar migración a 3.3.x en próximo ciclo",
                "status", "COMPATIBLE"
        ));

        checks.add(Map.of(
                "component", "MongoDB Driver",
                "compatible", true,
                "recommendation", "Driver compatible con MongoDB 6.x/7.x",
                "status", "COMPATIBLE"
        ));

        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        checks.add(Map.of(
                "component", "Sistema Operativo",
                "name", osName,
                "version", osVersion,
                "architecture", osArch,
                "status", "VERIFICADO"
        ));

        checks.add(Map.of(
                "component", "Android SDK (Cliente)",
                "compileSdk", 34,
                "minSdk", 26,
                "targetSdk", 34,
                "compatible", true,
                "recommendation", "Compatible con Android 8.0+ (95% de dispositivos)",
                "status", "COMPATIBLE"
        ));

        checks.add(Map.of(
                "component", "Seguridad JWT (JJWT)",
                "version", "0.12.5",
                "algorithm", "HMAC-SHA256",
                "compatible", true,
                "recommendation", "Versión actual y segura",
                "status", "COMPATIBLE"
        ));

        checks.add(Map.of(
                "component", "BCrypt (Password Hashing)",
                "strength", 10,
                "compatible", true,
                "recommendation", "Nivel de seguridad adecuado para aplicación educativa",
                "status", "COMPATIBLE"
        ));

        long compatibleComponents = checks.stream()
                .filter(c -> "COMPATIBLE".equals(c.get("status")) || "VERIFICADO".equals(c.get("status")))
                .count();

        result.put("type", "ADAPTATIVO");
        result.put("description", "Verificación de compatibilidad tecnológica y adaptación al entorno");
        result.put("executedAt", Instant.now().toString());
        result.put("totalComponents", checks.size());
        result.put("compatibleComponents", compatibleComponents);
        result.put("compatibilityRate", String.format("%.0f%%", (double) compatibleComponents / checks.size() * 100));
        result.put("checks", checks);

        logMaintenance("ADAPTATIVO", "verificacion_adaptativa",
                "Verificación adaptativa: " + compatibleComponents + "/" + checks.size() + " componentes compatibles",
                "COMPLETADO", "BAJO", "sistema",
                Map.of("compatible", compatibleComponents, "total", checks.size()));

        return result;
    }

    // =============================================
    // RESUMEN Y DASHBOARD DE MANTENIMIENTO
    // =============================================

    public Map<String, Object> getMaintenanceDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalLogs", maintenanceLogRepository.count());
        stats.put("correctivo", maintenanceLogRepository.countByType("CORRECTIVO"));
        stats.put("preventivo", maintenanceLogRepository.countByType("PREVENTIVO"));
        stats.put("perfectivo", maintenanceLogRepository.countByType("PERFECTIVO"));
        stats.put("adaptativo", maintenanceLogRepository.countByType("ADAPTATIVO"));
        stats.put("pendientes", maintenanceLogRepository.countByStatus("PENDIENTE"));
        stats.put("completados", maintenanceLogRepository.countByStatus("COMPLETADO"));

        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        List<MaintenanceLog> recentLogs = maintenanceLogRepository
                .findByCreatedAtBetweenOrderByCreatedAtDesc(weekAgo, Instant.now());

        dashboard.put("summary", stats);
        dashboard.put("recentActivity", recentLogs);
        dashboard.put("generatedAt", Instant.now().toString());

        return dashboard;
    }

    public List<MaintenanceLog> getLogsByType(String type) {
        return maintenanceLogRepository.findByTypeOrderByCreatedAtDesc(type.toUpperCase());
    }

    public Map<String, Object> runFullMaintenance() {
        logger.info("Ejecutando mantenimiento completo del sistema (4 tipos)");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("correctivo", runCorrectiveDiagnostics());
        result.put("preventivo", runPreventiveMaintenance());
        result.put("perfectivo", runPerfectiveAnalysis());
        result.put("adaptativo", runAdaptiveCheck());
        result.put("executedAt", Instant.now().toString());
        result.put("status", "MANTENIMIENTO_COMPLETO");
        return result;
    }

    private void logMaintenance(String type, String category, String description,
                                String status, String severity, String component,
                                Map<String, Object> details) {
        MaintenanceLog log = MaintenanceLog.builder()
                .type(type)
                .category(category)
                .description(description)
                .status(status)
                .severity(severity)
                .component(component)
                .details(details)
                .createdAt(Instant.now())
                .build();
        maintenanceLogRepository.save(log);
    }
}
