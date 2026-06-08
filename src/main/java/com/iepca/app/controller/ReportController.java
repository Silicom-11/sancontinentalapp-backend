package com.iepca.app.controller;

import com.iepca.app.service.StudentExcelReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final StudentExcelReportService studentExcelReportService;

    public ReportController(StudentExcelReportService studentExcelReportService) {
        this.studentExcelReportService = studentExcelReportService;
    }

    @GetMapping("/students/excel")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<byte[]> exportStudentsExcel() {
        byte[] report = studentExcelReportService.generateActiveStudentsReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=estudiantes_iepca.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(report.length)
                .body(report);
    }
}
