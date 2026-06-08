package com.iepca.app.repository;

import com.iepca.app.model.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends MongoRepository<Teacher, String> {

    Optional<Teacher> findByDni(String dni);

    Optional<Teacher> findByEmail(String email);

    Optional<Teacher> findByEmployeeCode(String employeeCode);

    List<Teacher> findByIsActiveTrue();

    List<Teacher> findBySpecialty(String specialty);

    Optional<Teacher> findByUserId(String userId);

    long countByIsActiveTrue();

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);

    List<Teacher> findBySpecialtyAndIsActiveTrue(String specialty);

    List<Teacher> findByInstitutionAndIsActiveTrue(String institutionId);
}

