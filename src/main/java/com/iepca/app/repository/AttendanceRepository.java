package com.iepca.app.repository;

import com.iepca.app.model.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends MongoRepository<Attendance, String> {

    Optional<Attendance> findByStudentAndCourseAndDate(String studentId, String courseId, Instant date);

    List<Attendance> findByStudent(String studentId);

    List<Attendance> findByStudentAndDateBetween(String studentId, Instant startDate, Instant endDate);

    List<Attendance> findByCourseAndDate(String courseId, Instant date);

    List<Attendance> findByCourseAndDateBetween(String courseId, Instant startDate, Instant endDate);

    List<Attendance> findByStudentAndCourse(String studentId, String courseId);

    long countByStudentAndStatus(String studentId, String status);

    long countByStudentAndDateBetween(String studentId, Instant startDate, Instant endDate);

    long countByStudentAndStatusAndDateBetween(String studentId, String status, Instant startDate, Instant endDate);

    List<Attendance> findByTeacher(String teacherId);
}

