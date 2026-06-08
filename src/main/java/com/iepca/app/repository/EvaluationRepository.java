package com.iepca.app.repository;

import com.iepca.app.model.Evaluation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends MongoRepository<Evaluation, String> {

    List<Evaluation> findByCourse(String courseId);

    List<Evaluation> findByCourseAndBimester(String courseId, Integer bimester);

    List<Evaluation> findByCourseAndBimesterAndAcademicYear(String courseId, Integer bimester, Integer academicYear);

    List<Evaluation> findByTeacher(String teacherId);

    List<Evaluation> findByCourseAndIsActiveTrue(String courseId);

    List<Evaluation> findByCourseOrderByOrderAsc(String courseId);

    long countByCourseAndBimesterAndAcademicYear(String courseId, Integer bimester, Integer academicYear);

    List<Evaluation> findByIsActiveTrue();

    List<Evaluation> findByTeacherAndIsActiveTrue(String teacherId);

    List<Evaluation> findByCourseAndBimesterAndIsActiveTrue(String courseId, Integer bimester);
}

