package com.iepca.app.repository;

import com.iepca.app.model.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends MongoRepository<Grade, String> {

    Optional<Grade> findByStudentAndCourseAndBimesterAndAcademicYear(
            String student, String course, Integer bimester, Integer academicYear);

    List<Grade> findByStudent(String studentId);

    List<Grade> findByStudentAndAcademicYear(String studentId, Integer academicYear);

    List<Grade> findByCourseAndBimesterAndAcademicYear(
            String courseId, Integer bimester, Integer academicYear);

    List<Grade> findByCourse(String courseId);

    List<Grade> findByTeacher(String teacherId);

    List<Grade> findByStudentAndCourse(String studentId, String courseId);

    long countByCourseAndBimesterAndAcademicYear(String courseId, Integer bimester, Integer academicYear);
}

