package com.iepca.app.repository;

import com.iepca.app.model.Classroom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends MongoRepository<Classroom, String> {

    List<Classroom> findByAcademicYear(String academicYearId);

    List<Classroom> findByAcademicYearAndIsActiveTrue(String academicYearId);

    List<Classroom> findByGradeLevel(String gradeLevelId);

    List<Classroom> findByGradeLevelAndAcademicYear(String gradeLevelId, String academicYearId);

    Optional<Classroom> findByGradeLevelAndAcademicYearAndSectionAndShift(
            String gradeLevelId, String academicYearId, String section, String shift);

    List<Classroom> findByTutor(String tutorId);

    long countByAcademicYearAndIsActiveTrue(String academicYearId);

    long countByIsActiveTrue();

    List<Classroom> findByIsActiveTrue();

    List<Classroom> findByGradeLevelAndIsActiveTrue(String gradeLevelId);
}

