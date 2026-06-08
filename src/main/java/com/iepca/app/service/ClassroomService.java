package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Classroom;
import com.iepca.app.model.embedded.ClassroomStats;
import com.iepca.app.repository.ClassroomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomService {

    private static final Logger logger = LoggerFactory.getLogger(ClassroomService.class);

    private final ClassroomRepository classroomRepository;

    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    public List<Classroom> findAll() {
        return classroomRepository.findByIsActiveTrue();
    }

    public Classroom findById(String id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula", "id", id));
    }

    public Classroom create(Classroom classroom) {
        if (classroom.getStats() == null) {
            ClassroomStats stats = new ClassroomStats();
            stats.setEnrolledStudents(0);
            classroom.setStats(stats);
        }
        Classroom saved = classroomRepository.save(classroom);
        logger.info("Aula creada: {} {} - secciÃ³n {}", saved.getGradeLevel(), saved.getAcademicYear(), saved.getSection());
        return saved;
    }

    public Classroom update(String id, Classroom updated) {
        Classroom existing = findById(id);
        if (updated.getGradeLevel() != null) existing.setGradeLevel(updated.getGradeLevel());
        if (updated.getAcademicYear() != null) existing.setAcademicYear(updated.getAcademicYear());
        if (updated.getSection() != null) existing.setSection(updated.getSection());
        if (updated.getShift() != null) existing.setShift(updated.getShift());
        if (updated.getTutor() != null) existing.setTutor(updated.getTutor());
        if (updated.getCapacity() != null) existing.setCapacity(updated.getCapacity());
        if (updated.getLocation() != null) existing.setLocation(updated.getLocation());
        return classroomRepository.save(existing);
    }

    public void delete(String id) {
        Classroom classroom = findById(id);
        classroom.setIsActive(false);
        classroomRepository.save(classroom);
        logger.info("Aula desactivada: {}", id);
    }

    public List<Classroom> findByAcademicYear(String academicYearId) {
        return classroomRepository.findByAcademicYearAndIsActiveTrue(academicYearId);
    }

    public List<Classroom> findByGradeLevel(String gradeLevelId) {
        return classroomRepository.findByGradeLevelAndIsActiveTrue(gradeLevelId);
    }

    public Classroom updateStats(String id, int totalStudents) {
        Classroom classroom = findById(id);
        ClassroomStats stats = classroom.getStats();
        if (stats == null) {
            stats = new ClassroomStats();
        }
        stats.setEnrolledStudents(totalStudents);
        classroom.setStats(stats);
        return classroomRepository.save(classroom);
    }
}

