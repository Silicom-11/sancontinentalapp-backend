package com.iepca.app.service;

import com.iepca.app.model.Student;
import com.iepca.app.model.Attendance;
import com.iepca.app.model.Grade;
import com.iepca.app.repository.AttendanceRepository;
import com.iepca.app.repository.GradeRepository;
import com.iepca.app.repository.StudentRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class StudentExcelReportService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;

    public StudentExcelReportService(StudentRepository studentRepository,
                                     GradeRepository gradeRepository,
                                     AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public byte[] generateActiveStudentsReport() {
        List<Student> students = studentRepository.findByIsActiveTrue();
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Estudiantes");
            writeHeader(workbook, sheet);
            writeRows(sheet, students);
            for (int column = 0; column < 12; column++) {
                sheet.autoSizeColumn(column);
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo generar el reporte Excel de estudiantes", exception);
        }
    }

    private void writeHeader(Workbook workbook, Sheet sheet) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row row = sheet.createRow(0);
        String[] headers = {
                "Codigo", "DNI", "Nombres", "Apellidos", "Correo",
                "Grado", "Seccion", "Estado", "Promedio", "% Asistencia",
                "Riesgo Academico", "Observacion"
        };
        for (int index = 0; index < headers.length; index++) {
            row.createCell(index).setCellValue(headers[index]);
            row.getCell(index).setCellStyle(headerStyle);
        }
    }

    private void writeRows(Sheet sheet, List<Student> students) {
        int rowIndex = 1;
        for (Student student : students) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(StringUtils.defaultIfBlank(student.getStudentCode(), "-"));
            row.createCell(1).setCellValue(StringUtils.defaultIfBlank(student.getDni(), "-"));
            row.createCell(2).setCellValue(StringUtils.defaultIfBlank(student.getFirstName(), "-"));
            row.createCell(3).setCellValue(StringUtils.defaultIfBlank(student.getLastName(), "-"));
            row.createCell(4).setCellValue(StringUtils.defaultIfBlank(student.getEmail(), "-"));
            row.createCell(5).setCellValue(StringUtils.defaultIfBlank(student.getGradeLevel(), "-"));
            row.createCell(6).setCellValue(StringUtils.defaultIfBlank(student.getSection(), "-"));
            row.createCell(7).setCellValue(StringUtils.defaultIfBlank(student.getStatus(), "-"));
            double average = calculateAcademicAverage(student.getId());
            double attendanceRate = calculateAttendanceRate(student.getId());
            row.createCell(8).setCellValue(average);
            row.createCell(9).setCellValue(attendanceRate);
            row.createCell(10).setCellValue(resolveRiskLevel(average, attendanceRate));
            row.createCell(11).setCellValue(buildObservation(average, attendanceRate));
        }
    }

    private double calculateAcademicAverage(String studentId) {
        List<Grade> grades = gradeRepository.findByStudent(studentId);
        return grades.stream()
                .filter(grade -> grade.getAverage() != null)
                .mapToDouble(Grade::getAverage)
                .average()
                .orElse(0.0);
    }

    private double calculateAttendanceRate(String studentId) {
        List<Attendance> attendances = attendanceRepository.findByStudent(studentId);
        if (attendances.isEmpty()) {
            return 0.0;
        }
        long attended = attendances.stream()
                .filter(attendance -> {
                    String status = StringUtils.defaultString(attendance.getStatus()).toLowerCase();
                    return status.equals("present") || status.equals("late") || status.equals("justified");
                })
                .count();
        return Math.round((attended * 10000.0) / attendances.size()) / 100.0;
    }

    private String resolveRiskLevel(double average, double attendanceRate) {
        if (average < 11.0 || attendanceRate < 70.0) {
            return "ALTO";
        }
        if (average < 14.0 || attendanceRate < 85.0) {
            return "MEDIO";
        }
        return "BAJO";
    }

    private String buildObservation(double average, double attendanceRate) {
        if (average < 11.0 && attendanceRate < 70.0) {
            return "Requiere intervencion academica y seguimiento de asistencia.";
        }
        if (average < 11.0) {
            return "Requiere refuerzo academico.";
        }
        if (attendanceRate < 70.0) {
            return "Requiere seguimiento por inasistencias.";
        }
        return "Sin alertas criticas.";
    }
}
