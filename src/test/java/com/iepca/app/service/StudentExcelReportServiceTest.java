package com.iepca.app.service;

import com.iepca.app.model.Student;
import com.iepca.app.model.Attendance;
import com.iepca.app.model.Grade;
import com.iepca.app.repository.StudentRepository;
import com.iepca.app.repository.AttendanceRepository;
import com.iepca.app.repository.GradeRepository;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StudentExcelReportServiceTest {

    @Test
    void shouldGenerateExcelWithActiveStudents() throws Exception {
        StudentRepository studentRepository = mock(StudentRepository.class);
        GradeRepository gradeRepository = mock(GradeRepository.class);
        AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);

        Student student = Student.builder()
                .id("student-1")
                .studentCode("EST-2026-0001")
                .dni("70000004")
                .firstName("Sofia")
                .lastName("Ramirez")
                .email("alumno@iepca.edu.pe")
                .gradeLevel("3 Secundaria")
                .section("A")
                .status("activo")
                .build();

        when(studentRepository.findByIsActiveTrue()).thenReturn(List.of(student));
        when(gradeRepository.findByStudent("student-1")).thenReturn(List.of(
                Grade.builder().average(18.0).build(),
                Grade.builder().average(16.0).build()
        ));
        when(attendanceRepository.findByStudent("student-1")).thenReturn(List.of(
                Attendance.builder().status("present").build(),
                Attendance.builder().status("late").build(),
                Attendance.builder().status("absent").build(),
                Attendance.builder().status("justified").build()
        ));
        StudentExcelReportService service = new StudentExcelReportService(
                studentRepository, gradeRepository, attendanceRepository);

        byte[] report = service.generateActiveStudentsReport();

        assertTrue(report.length > 0);
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(report))) {
            assertEquals("Estudiantes", workbook.getSheetAt(0).getSheetName());
            assertEquals("Codigo", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
            assertEquals("EST-2026-0001", workbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
            assertEquals("Sofia", workbook.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());
            assertEquals(17.0, workbook.getSheetAt(0).getRow(1).getCell(8).getNumericCellValue());
            assertEquals(75.0, workbook.getSheetAt(0).getRow(1).getCell(9).getNumericCellValue());
            assertEquals("MEDIO", workbook.getSheetAt(0).getRow(1).getCell(10).getStringCellValue());
        }
    }
}
