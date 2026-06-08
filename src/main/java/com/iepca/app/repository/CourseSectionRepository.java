package com.iepca.app.repository;

import com.iepca.app.model.CourseSection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends MongoRepository<CourseSection, String> {

    List<CourseSection> findByTeacher(String teacherId);

    List<CourseSection> findByTeacherAndIsActiveTrue(String teacherId);

    List<CourseSection> findByClassroom(String classroomId);

    List<CourseSection> findBySubject(String subjectId);

    List<CourseSection> findByAcademicYear(String academicYearId);

    List<CourseSection> findByAcademicYearAndIsActiveTrue(String academicYearId);

    List<CourseSection> findByClassroomAndAcademicYear(String classroomId, String academicYearId);

    long countByAcademicYearAndIsActiveTrue(String academicYearId);

    List<CourseSection> findByIsActiveTrue();

    List<CourseSection> findByClassroomAndIsActiveTrue(String classroomId);

    List<CourseSection> findByStudentsContainingAndIsActiveTrue(String studentId);
}

