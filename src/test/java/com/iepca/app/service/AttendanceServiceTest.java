package com.iepca.app.service;

import com.iepca.app.dto.request.AttendanceRequest;
import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Attendance;
import com.iepca.app.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AttendanceServiceTest {

    private AttendanceRepository attendanceRepository;
    private AttendanceService attendanceService;

    @BeforeEach
    void setUp() {
        attendanceRepository = mock(AttendanceRepository.class);
        attendanceService = new AttendanceService(attendanceRepository);
    }

    @Test
    void findByIdShouldReturnAttendance() {
        Attendance attendance = Attendance.builder()
                .id("att-1").student("student-1").status("present").build();
        when(attendanceRepository.findById("att-1")).thenReturn(Optional.of(attendance));

        Attendance result = attendanceService.findById("att-1");

        assertEquals("att-1", result.getId());
        assertEquals("present", result.getStatus());
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        when(attendanceRepository.findById("not-found")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> attendanceService.findById("not-found"));
    }

    @Test
    void markAttendanceRecordsShouldSaveAllEntries() {
        AttendanceRequest request = new AttendanceRequest();
        request.setCourseId("course-math");
        request.setDate("2026-07-01");

        AttendanceRequest.AttendanceEntry entry1 = new AttendanceRequest.AttendanceEntry();
        entry1.setStudentId("student-1");
        entry1.setStatus("present");

        AttendanceRequest.AttendanceEntry entry2 = new AttendanceRequest.AttendanceEntry();
        entry2.setStudentId("student-2");
        entry2.setStatus("absent");

        request.setRecords(List.of(entry1, entry2));

        when(attendanceRepository.findByStudentAndCourseAndDate(anyString(), anyString(), any()))
                .thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        List<Attendance> results = attendanceService.markAttendanceRecords(request, "teacher-1");

        assertEquals(2, results.size());
        assertEquals("present", results.get(0).getStatus());
        assertEquals("absent", results.get(1).getStatus());
        verify(attendanceRepository, times(2)).save(any(Attendance.class));
    }

    @Test
    void markAttendanceShouldUpdateExistingRecord() {
        Attendance existing = Attendance.builder()
                .id("att-existing").student("student-1").course("course-1")
                .status("absent").build();

        AttendanceRequest request = new AttendanceRequest();
        request.setCourseId("course-1");
        request.setDate("2026-07-01");

        AttendanceRequest.AttendanceEntry entry = new AttendanceRequest.AttendanceEntry();
        entry.setStudentId("student-1");
        entry.setStatus("justified");
        entry.setObservations("Certificado medico presentado");
        request.setRecords(List.of(entry));

        when(attendanceRepository.findByStudentAndCourseAndDate(eq("student-1"), eq("course-1"), any()))
                .thenReturn(Optional.of(existing));
        when(attendanceRepository.save(any(Attendance.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        List<Attendance> results = attendanceService.markAttendanceRecords(request, "teacher-1");

        assertEquals("justified", results.get(0).getStatus());
        assertEquals("Certificado medico presentado", results.get(0).getObservations());
    }

    @Test
    void getStudentStatsShouldCalculateCorrectly() {
        List<Attendance> records = List.of(
                Attendance.builder().status("present").build(),
                Attendance.builder().status("present").build(),
                Attendance.builder().status("present").build(),
                Attendance.builder().status("late").build(),
                Attendance.builder().status("absent").build(),
                Attendance.builder().status("justified").build()
        );
        when(attendanceRepository.findByStudent("student-1")).thenReturn(records);

        Map<String, Object> stats = attendanceService.getStudentStats("student-1");

        assertEquals(6, stats.get("total"));
        assertEquals(3L, stats.get("present"));
        assertEquals(1L, stats.get("absent"));
        assertEquals(1L, stats.get("late"));
        assertEquals(1L, stats.get("justified"));
    }

    @Test
    void getStudentHistoryShouldReturnAllRecords() {
        List<Attendance> records = List.of(
                Attendance.builder().id("a1").student("s1").status("present").build(),
                Attendance.builder().id("a2").student("s1").status("late").build()
        );
        when(attendanceRepository.findByStudent("s1")).thenReturn(records);

        List<Attendance> result = attendanceService.getStudentHistory("s1");

        assertEquals(2, result.size());
    }

    @Test
    void markAttendanceShouldReturnSummaryMap() {
        AttendanceRequest request = new AttendanceRequest();
        request.setCourseId("course-1");
        request.setDate("2026-07-01");

        AttendanceRequest.AttendanceEntry entry = new AttendanceRequest.AttendanceEntry();
        entry.setStudentId("student-1");
        entry.setStatus("present");
        request.setRecords(List.of(entry));

        when(attendanceRepository.findByStudentAndCourseAndDate(anyString(), anyString(), any()))
                .thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = attendanceService.markAttendance(request, "teacher-1");

        assertEquals(1, result.get("saved"));
        assertNotNull(result.get("records"));
    }
}
