package com.iepca.app.repository;

import com.iepca.app.model.AcademicYear;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicYearRepository extends MongoRepository<AcademicYear, String> {

    Optional<AcademicYear> findByInstitutionAndYear(String institutionId, Integer year);

    Optional<AcademicYear> findByIsCurrentTrue();

    List<AcademicYear> findByInstitution(String institutionId);

    List<AcademicYear> findByInstitutionAndIsActiveTrue(String institutionId);

    List<AcademicYear> findByInstitutionOrderByYearDesc(String institutionId);

    Optional<AcademicYear> findByInstitutionAndStatus(String institutionId, String status);
}

