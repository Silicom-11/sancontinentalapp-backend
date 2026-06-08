package com.iepca.app.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JavaResourcesStatusService {

    private static final Logger logger = LoggerFactory.getLogger(JavaResourcesStatusService.class);
    private static final Logger securityAudit = LoggerFactory.getLogger("SECURITY_AUDIT");

    private final SystemHealthService systemHealthService;
    private final StudentExcelReportService studentExcelReportService;

    public JavaResourcesStatusService(SystemHealthService systemHealthService,
                                      StudentExcelReportService studentExcelReportService) {
        this.systemHealthService = systemHealthService;
        this.studentExcelReportService = studentExcelReportService;
    }

    public Map<String, Object> buildStatusSnapshot(String requestedBy) {
        Map<String, Object> firstHealthSnapshot = systemHealthService.getSnapshot();
        Map<String, Object> secondHealthSnapshot = systemHealthService.getSnapshot();
        byte[] excelReport = studentExcelReportService.generateActiveStudentsReport();

        String unsafeName = "../justificaciones//certificado medico FINAL!!.PDF";
        String safeFileName = FilenameUtils.getName(unsafeName);
        String extension = FilenameUtils.getExtension(safeFileName).toLowerCase();
        String normalizedText = StringUtils.normalizeSpace("  IEP   Continental      Americano  ");

        Map<String, Object> guava = new LinkedHashMap<>();
        guava.put("library", "Google Guava");
        guava.put("feature", "CacheBuilder");
        guava.put("status", "Cache operativo");
        guava.put("sameCachedObject", firstHealthSnapshot == secondHealthSnapshot);
        guava.put("checkedAt", firstHealthSnapshot.get("checkedAt"));
        guava.put("usedMemoryMb", firstHealthSnapshot.get("usedMemoryMb"));

        Map<String, Object> poi = new LinkedHashMap<>();
        poi.put("library", "Apache POI");
        poi.put("feature", "XSSFWorkbook");
        poi.put("status", "Generacion disponible");
        poi.put("fileName", "estudiantes_iepca.xlsx");
        poi.put("bytes", excelReport.length);
        poi.put("endpoint", "/api/reports/students/excel");

        Map<String, Object> commons = new LinkedHashMap<>();
        commons.put("library", "Apache Commons");
        commons.put("feature", "StringUtils + FilenameUtils");
        commons.put("status", "Reglas activas");
        commons.put("originalFile", unsafeName);
        commons.put("safeFileName", safeFileName);
        commons.put("extension", extension);
        commons.put("normalizedText", normalizedText);

        Map<String, Object> logback = new LinkedHashMap<>();
        logback.put("library", "Logback/SLF4J");
        logback.put("feature", "Rolling logs + SECURITY_AUDIT");
        logback.put("status", "Registro operativo");
        logback.put("logFile", "logs/iepca-security-audit.log");
        logback.put("eventAt", Instant.now().toString());

        securityAudit.info("SYSTEM_RESOURCES_STATUS requestedBy={} poiBytes={} guavaCached={}",
                sanitize(requestedBy), excelReport.length, firstHealthSnapshot == secondHealthSnapshot);
        logger.info("Estado de recursos del sistema verificado para {}", sanitize(requestedBy));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("title", "Estado operativo - IEP Continental Americano");
        response.put("guava", guava);
        response.put("apachePoi", poi);
        response.put("apacheCommons", commons);
        response.put("logback", logback);
        return response;
    }

    private String sanitize(String value) {
        return StringUtils.normalizeSpace(StringUtils.defaultString(value, "-"))
                .replace('\r', '_')
                .replace('\n', '_')
                .replace('\t', '_');
    }
}
