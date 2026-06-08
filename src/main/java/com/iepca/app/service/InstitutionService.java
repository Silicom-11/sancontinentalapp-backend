package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Institution;
import com.iepca.app.repository.InstitutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstitutionService {

    private static final Logger logger = LoggerFactory.getLogger(InstitutionService.class);

    private final InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public List<Institution> findAll() {
        return institutionRepository.findByIsActiveTrue();
    }

    public Institution findById(String id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InstituciÃ³n", "id", id));
    }

    public Institution findByCode(String code) {
        return institutionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("InstituciÃ³n", "cÃ³digo", code));
    }

    public Institution create(Institution institution) {
        if (institution.getIsActive() == null) {
            institution.setIsActive(true);
        }
        Institution saved = institutionRepository.save(institution);
        logger.info("InstituciÃ³n creada: {} ({})", saved.getName(), saved.getCode());
        return saved;
    }

    public Institution update(String id, Institution updated) {
        Institution existing = findById(id);
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getCode() != null) existing.setCode(updated.getCode());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getDirector() != null) existing.setDirector(updated.getDirector());
        if (updated.getLogo() != null) existing.setLogo(updated.getLogo());
        if (updated.getShifts() != null) existing.setShifts(updated.getShifts());
        if (updated.getShiftSchedules() != null) existing.setShiftSchedules(updated.getShiftSchedules());
        if (updated.getAcademicLevels() != null) existing.setAcademicLevels(updated.getAcademicLevels());
        if (updated.getEvaluationSystem() != null) existing.setEvaluationSystem(updated.getEvaluationSystem());
        if (updated.getGradeScale() != null) existing.setGradeScale(updated.getGradeScale());
        if (updated.getEvaluationTypes() != null) existing.setEvaluationTypes(updated.getEvaluationTypes());
        return institutionRepository.save(existing);
    }

    public void delete(String id) {
        Institution institution = findById(id);
        institution.setIsActive(false);
        institutionRepository.save(institution);
        logger.info("InstituciÃ³n desactivada: {}", institution.getName());
    }
}

