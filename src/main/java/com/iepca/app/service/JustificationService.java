package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Justification;
import com.iepca.app.repository.JustificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class JustificationService {

    private static final Logger logger = LoggerFactory.getLogger(JustificationService.class);

    private final JustificationRepository justificationRepository;

    public JustificationService(JustificationRepository justificationRepository) {
        this.justificationRepository = justificationRepository;
    }

    public List<Justification> findAll() {
        return justificationRepository.findAllByOrderByCreatedAtDesc();
    }

    public Justification findById(String id) {
        return justificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JustificaciÃ³n", "id", id));
    }

    public List<Justification> findByStudent(String studentId) {
        return justificationRepository.findByStudent(studentId);
    }

    public List<Justification> findByParent(String parentId) {
        return justificationRepository.findByParent(parentId);
    }

    public List<Justification> findByStatus(String status) {
        return justificationRepository.findByStatus(status);
    }

    public Justification submit(Justification justification) {
        justification.setStatus("pending");
        Justification saved = justificationRepository.save(justification);
        logger.info("JustificaciÃ³n enviada: estudiante={}, razÃ³n={}", saved.getStudent(), saved.getReason());
        return saved;
    }

    public Justification approve(String id, String reviewerId, String reviewNote) {
        Justification justification = findById(id);
        justification.setStatus("approved");
        justification.setReviewedBy(reviewerId);
        justification.setReviewedAt(Instant.now());
        justification.setReviewNote(reviewNote);
        logger.info("JustificaciÃ³n aprobada: {}", id);
        return justificationRepository.save(justification);
    }

    public Justification reject(String id, String reviewerId, String reviewNote) {
        Justification justification = findById(id);
        justification.setStatus("rejected");
        justification.setReviewedBy(reviewerId);
        justification.setReviewedAt(Instant.now());
        justification.setReviewNote(reviewNote);
        logger.info("JustificaciÃ³n rechazada: {}", id);
        return justificationRepository.save(justification);
    }

    public long countPending() {
        return justificationRepository.countByStatus("pending");
    }

    public Justification approve(String id, String reviewerId) {
        return approve(id, reviewerId, null);
    }

    public void delete(String id) {
        Justification justification = findById(id);
        justificationRepository.delete(justification);
        logger.info("JustificaciÃ³n eliminada: {}", id);
    }

    public Map<String, Object> getStats() {
        long total = justificationRepository.count();
        long pending = justificationRepository.countByStatus("pending");
        long approved = justificationRepository.countByStatus("approved");
        long rejected = justificationRepository.countByStatus("rejected");
        return Map.of(
                "total", total,
                "pending", pending,
                "approved", approved,
                "rejected", rejected
        );
    }
}

