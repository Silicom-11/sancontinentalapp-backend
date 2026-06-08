package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.CourseSection;
import com.iepca.app.model.embedded.CourseSectionStats;
import com.iepca.app.repository.CourseSectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseSectionService {

    private static final Logger logger = LoggerFactory.getLogger(CourseSectionService.class);

    private final CourseSectionRepository courseSectionRepository;

    public CourseSectionService(CourseSectionRepository courseSectionRepository) {
        this.courseSectionRepository = courseSectionRepository;
    }

    public List<CourseSection> findAll() {
        return courseSectionRepository.findByIsActiveTrue();
    }

    public CourseSection findById(String id) {
        return courseSectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SecciÃ³n de curso", "id", id));
    }

    public CourseSection create(CourseSection section) {
        if (section.getStats() == null) {
            CourseSectionStats stats = new CourseSectionStats();
            stats.setTotalStudents(0);
            stats.setAverageGrade(0.0);
            stats.setAttendanceRate(0.0);
            section.setStats(stats);
        }
        if (section.getIsActive() == null) {
            section.setIsActive(true);
        }
        CourseSection saved = courseSectionRepository.save(section);
        logger.info("SecciÃ³n de curso creada: {}", saved.getId());
        return saved;
    }

    public CourseSection update(String id, CourseSection updated) {
        CourseSection existing = findById(id);
        if (updated.getSubject() != null) existing.setSubject(updated.getSubject());
        if (updated.getClassroom() != null) existing.setClassroom(updated.getClassroom());
        if (updated.getTeacher() != null) existing.setTeacher(updated.getTeacher());
        if (updated.getSchedule() != null) existing.setSchedule(updated.getSchedule());
        if (updated.getResources() != null) existing.setResources(updated.getResources());
        if (updated.getPeriodEvaluations() != null) existing.setPeriodEvaluations(updated.getPeriodEvaluations());
        return courseSectionRepository.save(existing);
    }

    public void delete(String id) {
        CourseSection section = findById(id);
        section.setIsActive(false);
        courseSectionRepository.save(section);
        logger.info("SecciÃ³n de curso desactivada: {}", id);
    }

    public CourseSection addStudent(String id, String studentId) {
        CourseSection section = findById(id);
        if (section.getStudents() == null) {
            section.setStudents(new ArrayList<>());
        }
        if (!section.getStudents().contains(studentId)) {
            section.getStudents().add(studentId);
            if (section.getStats() != null) {
                section.getStats().setTotalStudents(section.getStudents().size());
            }
        }
        return courseSectionRepository.save(section);
    }

    public CourseSection removeStudent(String id, String studentId) {
        CourseSection section = findById(id);
        if (section.getStudents() != null) {
            section.getStudents().remove(studentId);
            if (section.getStats() != null) {
                section.getStats().setTotalStudents(section.getStudents().size());
            }
        }
        return courseSectionRepository.save(section);
    }

    public List<CourseSection> findByTeacher(String teacherId) {
        return courseSectionRepository.findByTeacherAndIsActiveTrue(teacherId);
    }

    public List<CourseSection> findByClassroom(String classroomId) {
        return courseSectionRepository.findByClassroomAndIsActiveTrue(classroomId);
    }

    public List<CourseSection> findByAcademicYear(String academicYearId) {
        return courseSectionRepository.findByAcademicYearAndIsActiveTrue(academicYearId);
    }

    public List<CourseSection> findByStudentId(String studentId) {
        return courseSectionRepository.findByStudentsContainingAndIsActiveTrue(studentId);
    }
}

