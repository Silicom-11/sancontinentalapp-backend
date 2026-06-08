package com.iepca.app.service;

import com.iepca.app.exception.BadRequestException;
import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Enrollment;
import com.iepca.app.model.embedded.StatusHistoryEntry;
import com.iepca.app.repository.EnrollmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    public Enrollment findById(String id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MatrÃ­cula", "id", id));
    }

    public Enrollment create(Enrollment enrollment) {
        if (enrollmentRepository.existsByStudentAndAcademicYear(
                enrollment.getStudent(), enrollment.getAcademicYear())) {
            throw new BadRequestException("El estudiante ya tiene una matrÃ­cula para este aÃ±o acadÃ©mico");
        }

        String enrollmentNumber = generateEnrollmentNumber();
        enrollment.setEnrollmentNumber(enrollmentNumber);

        if (enrollment.getStatus() == null) {
            enrollment.setStatus("activa");
        }

        StatusHistoryEntry entry = new StatusHistoryEntry();
        entry.setStatus(enrollment.getStatus());
        entry.setDate(Instant.now());
        entry.setReason("MatrÃ­cula creada");
        enrollment.setStatusHistory(new ArrayList<>(List.of(entry)));

        Enrollment saved = enrollmentRepository.save(enrollment);
        logger.info("MatrÃ­cula creada: {} para estudiante {}", enrollmentNumber, enrollment.getStudent());
        return saved;
    }

    public Enrollment updateStatus(String id, String newStatus, String reason, String changedBy) {
        Enrollment enrollment = findById(id);
        enrollment.setStatus(newStatus);

        StatusHistoryEntry entry = new StatusHistoryEntry();
        entry.setStatus(newStatus);
        entry.setDate(Instant.now());
        entry.setReason(reason);
        entry.setChangedBy(changedBy);

        if (enrollment.getStatusHistory() == null) {
            enrollment.setStatusHistory(new ArrayList<>());
        }
        enrollment.getStatusHistory().add(entry);

        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> findByAcademicYear(String academicYearId) {
        return enrollmentRepository.findByAcademicYear(academicYearId);
    }

    public List<Enrollment> findByStudent(String studentId) {
        return enrollmentRepository.findByStudent(studentId);
    }

    public List<Enrollment> findByStatus(String status) {
        return enrollmentRepository.findByStatus(status);
    }

    public long countByAcademicYearAndStatus(String academicYearId, String status) {
        return enrollmentRepository.countByAcademicYearAndStatus(academicYearId, status);
    }

    private String generateEnrollmentNumber() {
        int year = Year.now().getValue();
        long count = enrollmentRepository.count() + 1;
        return String.format("MAT-%d-%04d", year, count);
    }
}

