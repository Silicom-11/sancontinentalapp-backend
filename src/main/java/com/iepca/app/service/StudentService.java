package com.iepca.app.service;

import com.iepca.app.exception.BadRequestException;
import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Parent;
import com.iepca.app.model.Student;
import com.iepca.app.repository.ParentRepository;
import com.iepca.app.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;

    public StudentService(StudentRepository studentRepository, ParentRepository parentRepository) {
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findByIsActiveTrue();
    }

    public Student findById(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", id));
    }

    public Student findByUserId(String userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "userId", userId));
    }

    public Student create(Student student) {
        if (student.getDni() != null && studentRepository.existsByDni(student.getDni())) {
            throw new BadRequestException("El DNI ya estÃ¡ registrado");
        }
        // Auto-generate student code
        if (student.getStudentCode() == null) {
            long count = studentRepository.count() + 1;
            student.setStudentCode(String.format("EST-%d-%04d",
                    java.time.Year.now().getValue(), count));
        }
        Student saved = studentRepository.save(student);
        logger.info("Estudiante creado: {} ({})", saved.getFullName(), saved.getStudentCode());
        return saved;
    }

    public Student update(String id, Student updated) {
        Student existing = findById(id);
        if (updated.getFirstName() != null) existing.setFirstName(updated.getFirstName());
        if (updated.getLastName() != null) existing.setLastName(updated.getLastName());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getPhoto() != null) existing.setPhoto(updated.getPhoto());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getGradeLevel() != null) existing.setGradeLevel(updated.getGradeLevel());
        if (updated.getSection() != null) existing.setSection(updated.getSection());
        if (updated.getMedicalInfo() != null) existing.setMedicalInfo(updated.getMedicalInfo());
        if (updated.getGuardians() != null) existing.setGuardians(updated.getGuardians());
        if (updated.getDocuments() != null) existing.setDocuments(updated.getDocuments());
        return studentRepository.save(existing);
    }

    public void delete(String id) {
        Student student = findById(id);
        student.setIsActive(false);
        student.setStatus("inactivo");
        studentRepository.save(student);
        logger.info("Estudiante desactivado: {}", student.getFullName());
    }

    public List<Student> findByGradeAndSection(String gradeLevel, String section) {
        return studentRepository.findByGradeLevelAndSectionAndIsActiveTrue(gradeLevel, section);
    }

    public long countActive() {
        return studentRepository.countByIsActiveTrue();
    }

    public List<Student> findByIds(List<String> ids) {
        return studentRepository.findByIdIn(ids);
    }

    public List<Student> findByParentUserId(String parentUserId) {
        Parent parent = parentRepository.findByUserId(parentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Padre no encontrado para userId: " + parentUserId));
        if (parent.getChildren() == null || parent.getChildren().isEmpty()) {
            return List.of();
        }
        List<String> studentIds = parent.getChildren().stream()
                .map(com.iepca.app.model.embedded.Guardian::getStudent)
                .filter(java.util.Objects::nonNull)
                .toList();
        return studentRepository.findByIdIn(studentIds);
    }
}

