package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Subject;
import com.iepca.app.repository.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private static final Logger logger = LoggerFactory.getLogger(SubjectService.class);

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> findAll() {
        return subjectRepository.findByIsActiveTrue();
    }

    public Subject findById(String id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia", "id", id));
    }

    public Subject create(Subject subject) {
        Subject saved = subjectRepository.save(subject);
        logger.info("Materia creada: {} ({})", saved.getName(), saved.getCode());
        return saved;
    }

    public Subject update(String id, Subject updated) {
        Subject existing = findById(id);
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getCode() != null) existing.setCode(updated.getCode());
        if (updated.getArea() != null) existing.setArea(updated.getArea());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getHoursPerWeek() != null) existing.setHoursPerWeek(updated.getHoursPerWeek());
        if (updated.getDefaultWeights() != null) existing.setDefaultWeights(updated.getDefaultWeights());
        if (updated.getCompetencies() != null) existing.setCompetencies(updated.getCompetencies());
        if (updated.getColor() != null) existing.setColor(updated.getColor());
        if (updated.getIcon() != null) existing.setIcon(updated.getIcon());
        return subjectRepository.save(existing);
    }

    public void delete(String id) {
        Subject subject = findById(id);
        subject.setIsActive(false);
        subjectRepository.save(subject);
        logger.info("Materia desactivada: {}", subject.getName());
    }

    public List<Subject> findByInstitution(String institutionId) {
        return subjectRepository.findByInstitutionAndIsActiveTrue(institutionId);
    }

    public List<Subject> findByArea(String area) {
        return subjectRepository.findByAreaAndIsActiveTrue(area);
    }
}

