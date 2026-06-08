package com.iepca.app.repository;

import com.iepca.app.model.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {

    Optional<Subject> findByInstitutionAndCode(String institutionId, String code);

    List<Subject> findByInstitution(String institutionId);

    List<Subject> findByInstitutionAndIsActiveTrue(String institutionId);

    List<Subject> findByInstitutionAndArea(String institutionId, String area);

    List<Subject> findByGradeLevelsContaining(String gradeLevelId);

    List<Subject> findByInstitutionOrderByOrderAsc(String institutionId);

    List<Subject> findByIsActiveTrue();

    List<Subject> findByAreaAndIsActiveTrue(String area);
}

