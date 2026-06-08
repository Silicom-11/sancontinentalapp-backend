package com.iepca.app.repository;

import com.iepca.app.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    Optional<Course> findByCode(String code);

    List<Course> findByIsActiveTrue();

    List<Course> findByTeacher(String teacherId);

    List<Course> findByTeacherAndIsActiveTrue(String teacherId);

    List<Course> findByGradeLevelAndSection(String gradeLevel, String section);

    List<Course> findByGradeLevelAndSectionAndIsActiveTrue(String gradeLevel, String section);

    List<Course> findByStudentsContaining(String studentId);

    List<Course> findByAcademicYear(Integer year);

    long countByIsActiveTrue();

    boolean existsByCode(String code);
}

