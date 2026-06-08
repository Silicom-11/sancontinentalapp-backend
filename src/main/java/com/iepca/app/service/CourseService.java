package com.iepca.app.service;

import com.iepca.app.exception.BadRequestException;
import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Course;
import com.iepca.app.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findByIsActiveTrue();
    }

    public Course findById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));
    }

    public Course create(Course course) {
        if (courseRepository.existsByCode(course.getCode())) {
            throw new BadRequestException("El cÃ³digo de curso ya existe: " + course.getCode());
        }
        Course saved = courseRepository.save(course);
        logger.info("Curso creado: {} ({})", saved.getName(), saved.getCode());
        return saved;
    }

    public Course update(String id, Course updated) {
        Course existing = findById(id);
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getTeacher() != null) existing.setTeacher(updated.getTeacher());
        if (updated.getSchedule() != null) existing.setSchedule(updated.getSchedule());
        if (updated.getEvaluationWeights() != null) existing.setEvaluationWeights(updated.getEvaluationWeights());
        return courseRepository.save(existing);
    }

    public void delete(String id) {
        Course course = findById(id);
        course.setIsActive(false);
        courseRepository.save(course);
        logger.info("Curso desactivado: {}", course.getName());
    }

    public List<Course> findByTeacher(String teacherId) {
        return courseRepository.findByTeacherAndIsActiveTrue(teacherId);
    }

    public List<Course> findByStudent(String studentId) {
        return courseRepository.findByStudentsContaining(studentId);
    }

    public Course addStudent(String courseId, String studentId) {
        Course course = findById(courseId);
        if (!course.getStudents().contains(studentId)) {
            course.getStudents().add(studentId);
            courseRepository.save(course);
            logger.info("Estudiante {} agregado al curso {}", studentId, course.getName());
        }
        return course;
    }

    public Course removeStudent(String courseId, String studentId) {
        Course course = findById(courseId);
        course.getStudents().remove(studentId);
        courseRepository.save(course);
        logger.info("Estudiante {} removido del curso {}", studentId, course.getName());
        return course;
    }
}

