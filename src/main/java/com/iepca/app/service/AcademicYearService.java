package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.AcademicYear;
import com.iepca.app.model.embedded.AcademicPeriod;
import com.iepca.app.repository.AcademicYearRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcademicYearService {

    private static final Logger logger = LoggerFactory.getLogger(AcademicYearService.class);

    private final AcademicYearRepository academicYearRepository;

    public AcademicYearService(AcademicYearRepository academicYearRepository) {
        this.academicYearRepository = academicYearRepository;
    }

    public List<AcademicYear> findAll() {
        return academicYearRepository.findAll();
    }

    public AcademicYear findById(String id) {
        return academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AÃ±o acadÃ©mico", "id", id));
    }

    public AcademicYear findByInstitutionAndYear(String institutionId, Integer year) {
        return academicYearRepository.findByInstitutionAndYear(institutionId, year)
                .orElseThrow(() -> new ResourceNotFoundException("AÃ±o acadÃ©mico", "institution/year",
                        institutionId + "/" + year));
    }

    public AcademicYear findCurrentByInstitution(String institutionId) {
        return academicYearRepository.findByInstitutionAndStatus(institutionId, "en_curso")
                .orElseThrow(() -> new ResourceNotFoundException("AÃ±o acadÃ©mico activo", "institution", institutionId));
    }

    public AcademicYear create(AcademicYear academicYear) {
        if (academicYear.getStatus() == null) {
            academicYear.setStatus("planificado");
        }
        AcademicYear saved = academicYearRepository.save(academicYear);
        logger.info("AÃ±o acadÃ©mico creado: {} para instituciÃ³n {}", saved.getYear(), saved.getInstitution());
        return saved;
    }

    public AcademicYear update(String id, AcademicYear updated) {
        AcademicYear existing = findById(id);
        if (updated.getYear() != null) existing.setYear(updated.getYear());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        if (updated.getStartDate() != null) existing.setStartDate(updated.getStartDate());
        if (updated.getEndDate() != null) existing.setEndDate(updated.getEndDate());
        if (updated.getPeriods() != null) existing.setPeriods(updated.getPeriods());
        if (updated.getImportantDates() != null) existing.setImportantDates(updated.getImportantDates());
        if (updated.getStats() != null) existing.setStats(updated.getStats());
        return academicYearRepository.save(existing);
    }

    public AcademicYear activatePeriod(String id, int periodNumber) {
        AcademicYear academicYear = findById(id);
        if (academicYear.getPeriods() != null) {
            for (AcademicPeriod period : academicYear.getPeriods()) {
                period.setIsActive(period.getNumber() == periodNumber);
            }
        }
        if ("planificado".equals(academicYear.getStatus())) {
            academicYear.setStatus("en_curso");
        }
        logger.info("Periodo {} activado para aÃ±o acadÃ©mico {}", periodNumber, academicYear.getYear());
        return academicYearRepository.save(academicYear);
    }

    public AcademicYear finalize(String id) {
        AcademicYear academicYear = findById(id);
        academicYear.setStatus("finalizado");
        if (academicYear.getPeriods() != null) {
            for (AcademicPeriod period : academicYear.getPeriods()) {
                period.setIsActive(false);
            }
        }
        logger.info("AÃ±o acadÃ©mico {} finalizado", academicYear.getYear());
        return academicYearRepository.save(academicYear);
    }
}

