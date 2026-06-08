package com.iepca.app.repository;

import com.iepca.app.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    Optional<Student> findByDni(String dni);

    Optional<Student> findByEmail(String email);

    Optional<Student> findByStudentCode(String studentCode);

    Optional<Student> findByEnrollmentNumber(String enrollmentNumber);

    List<Student> findByIsActiveTrue();

    List<Student> findByGradeLevelAndSection(String gradeLevel, String section);

    List<Student> findByGradeLevelAndSectionAndIsActiveTrue(String gradeLevel, String section);

    @Query("{'guardians.student': ?0}")
    List<Student> findByGuardianStudentId(String studentId);

    long countByIsActiveTrue();

    long countByGradeLevel(String gradeLevel);

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);

    boolean existsByStudentCode(String studentCode);

    List<Student> findByIdIn(List<String> ids);

    Optional<Student> findByUserId(String userId);
}

