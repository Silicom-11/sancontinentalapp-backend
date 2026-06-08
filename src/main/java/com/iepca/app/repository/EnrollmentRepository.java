package com.iepca.app.repository;

import com.iepca.app.model.Enrollment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {

    Optional<Enrollment> findByStudentAndAcademicYear(String studentId, String academicYearId);

    Optional<Enrollment> findByEnrollmentNumber(String enrollmentNumber);

    List<Enrollment> findByStudent(String studentId);

    List<Enrollment> findByClassroom(String classroomId);

    List<Enrollment> findByClassroomAndStatus(String classroomId, String status);

    List<Enrollment> findByAcademicYear(String academicYearId);

    List<Enrollment> findByAcademicYearAndStatus(String academicYearId, String status);

    long countByClassroomAndStatus(String classroomId, String status);

    long countByAcademicYearAndStatus(String academicYearId, String status);

    boolean existsByStudentAndAcademicYear(String studentId, String academicYearId);

    List<Enrollment> findByStatus(String status);
}

