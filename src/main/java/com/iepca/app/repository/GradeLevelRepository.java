package com.iepca.app.repository;

import com.iepca.app.model.GradeLevel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeLevelRepository extends MongoRepository<GradeLevel, String> {

    List<GradeLevel> findByInstitution(String institutionId);

    List<GradeLevel> findByInstitutionAndIsActiveTrue(String institutionId);

    List<GradeLevel> findByInstitutionAndType(String institutionId, String type);

    List<GradeLevel> findByInstitutionOrderByOrderAsc(String institutionId);

    List<GradeLevel> findByIsActiveTrue();

    List<GradeLevel> findByTypeAndIsActiveTrue(String type);
}

