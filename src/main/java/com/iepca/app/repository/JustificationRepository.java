package com.iepca.app.repository;

import com.iepca.app.model.Justification;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JustificationRepository extends MongoRepository<Justification, String> {

    List<Justification> findByStudent(String studentId);

    List<Justification> findByStudentAndStatus(String studentId, String status);

    List<Justification> findByParent(String parentId);

    List<Justification> findByStatus(String status);

    List<Justification> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);
}

