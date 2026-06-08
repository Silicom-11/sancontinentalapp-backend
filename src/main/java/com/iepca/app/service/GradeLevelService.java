package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.GradeLevel;
import com.iepca.app.repository.GradeLevelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeLevelService {

    private static final Logger logger = LoggerFactory.getLogger(GradeLevelService.class);

    private final GradeLevelRepository gradeLevelRepository;

    public GradeLevelService(GradeLevelRepository gradeLevelRepository) {
        this.gradeLevelRepository = gradeLevelRepository;
    }

    public List<GradeLevel> findAll() {
        return gradeLevelRepository.findByIsActiveTrue();
    }

    public GradeLevel findById(String id) {
        return gradeLevelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nivel de grado", "id", id));
    }

    public GradeLevel create(GradeLevel gradeLevel) {
        GradeLevel saved = gradeLevelRepository.save(gradeLevel);
        logger.info("Nivel de grado creado: {} - nivel {}", saved.getType(), saved.getLevel());
        return saved;
    }

    public GradeLevel update(String id, GradeLevel updated) {
        GradeLevel existing = findById(id);
        if (updated.getInstitution() != null) existing.setInstitution(updated.getInstitution());
        if (updated.getType() != null) existing.setType(updated.getType());
        if (updated.getLevel() != null) existing.setLevel(updated.getLevel());
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        return gradeLevelRepository.save(existing);
    }

    public void delete(String id) {
        GradeLevel gradeLevel = findById(id);
        gradeLevel.setIsActive(false);
        gradeLevelRepository.save(gradeLevel);
        logger.info("Nivel de grado desactivado: {}", id);
    }

    public List<GradeLevel> findByInstitution(String institutionId) {
        return gradeLevelRepository.findByInstitutionAndIsActiveTrue(institutionId);
    }

    public List<GradeLevel> findByType(String type) {
        return gradeLevelRepository.findByTypeAndIsActiveTrue(type);
    }
}

