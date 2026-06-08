package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Evaluation;
import com.iepca.app.repository.EvaluationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(EvaluationService.class);

    private final EvaluationRepository evaluationRepository;

    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    public List<Evaluation> findAll() {
        return evaluationRepository.findByIsActiveTrue();
    }

    public Evaluation findById(String id) {
        return evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluaciÃ³n", "id", id));
    }

    public Evaluation create(Evaluation evaluation) {
        if (evaluation.getMaxGrade() == null) {
            evaluation.setMaxGrade(20.0);
        }
        if (evaluation.getIsActive() == null) {
            evaluation.setIsActive(true);
        }
        Evaluation saved = evaluationRepository.save(evaluation);
        logger.info("EvaluaciÃ³n creada: {} - {}", saved.getName(), saved.getType());
        return saved;
    }

    public Evaluation update(String id, Evaluation updated) {
        Evaluation existing = findById(id);
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getType() != null) existing.setType(updated.getType());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getCourse() != null) existing.setCourse(updated.getCourse());
        if (updated.getTeacher() != null) existing.setTeacher(updated.getTeacher());
        if (updated.getDate() != null) existing.setDate(updated.getDate());
        if (updated.getMaxGrade() != null) existing.setMaxGrade(updated.getMaxGrade());
        if (updated.getWeight() != null) existing.setWeight(updated.getWeight());
        if (updated.getBimester() != null) existing.setBimester(updated.getBimester());
        return evaluationRepository.save(existing);
    }

    public void delete(String id) {
        Evaluation evaluation = findById(id);
        evaluation.setIsActive(false);
        evaluationRepository.save(evaluation);
        logger.info("EvaluaciÃ³n desactivada: {}", evaluation.getName());
    }

    public List<Evaluation> findByCourse(String courseId) {
        return evaluationRepository.findByCourseAndIsActiveTrue(courseId);
    }

    public List<Evaluation> findByTeacher(String teacherId) {
        return evaluationRepository.findByTeacherAndIsActiveTrue(teacherId);
    }

    public List<Evaluation> findByCourseAndBimester(String courseId, Integer bimester) {
        return evaluationRepository.findByCourseAndBimesterAndIsActiveTrue(courseId, bimester);
    }
}

