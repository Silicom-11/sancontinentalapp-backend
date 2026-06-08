package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Teacher;
import com.iepca.app.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
public class TeacherService {

    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> findAll() {
        return teacherRepository.findByIsActiveTrue();
    }

    public Teacher findById(String id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente", "id", id));
    }

    public Teacher findByUserId(String userId) {
        return teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Docente", "userId", userId));
    }

    public Teacher create(Teacher teacher) {
        if (teacher.getEmployeeCode() == null || teacher.getEmployeeCode().isEmpty()) {
            teacher.setEmployeeCode(generateEmployeeCode());
        }
        if (teacher.getIsActive() == null) {
            teacher.setIsActive(true);
        }
        Teacher saved = teacherRepository.save(teacher);
        logger.info("Docente creado: {} - {}", saved.getEmployeeCode(), saved.getFirstName());
        return saved;
    }

    public Teacher update(String id, Teacher updated) {
        Teacher existing = findById(id);
        if (updated.getFirstName() != null) existing.setFirstName(updated.getFirstName());
        if (updated.getLastName() != null) existing.setLastName(updated.getLastName());
        if (updated.getDni() != null) existing.setDni(updated.getDni());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getSpecialty() != null) existing.setSpecialty(updated.getSpecialty());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getCertifications() != null) existing.setCertifications(updated.getCertifications());
        if (updated.getContractType() != null) existing.setContractType(updated.getContractType());
        if (updated.getSalary() != null) existing.setSalary(updated.getSalary());
        if (updated.getBankAccount() != null) existing.setBankAccount(updated.getBankAccount());
        if (updated.getIsActive() != null) existing.setIsActive(updated.getIsActive());
        return teacherRepository.save(existing);
    }

    public void delete(String id) {
        Teacher teacher = findById(id);
        teacher.setIsActive(false);
        teacherRepository.save(teacher);
        logger.info("Docente desactivado: {}", teacher.getEmployeeCode());
    }

    public List<Teacher> findBySpecialty(String specialty) {
        return teacherRepository.findBySpecialtyAndIsActiveTrue(specialty);
    }

    public List<Teacher> findByInstitution(String institutionId) {
        return teacherRepository.findByInstitutionAndIsActiveTrue(institutionId);
    }

    public long countActive() {
        return teacherRepository.countByIsActiveTrue();
    }

    private String generateEmployeeCode() {
        int year = Year.now().getValue();
        long count = teacherRepository.count() + 1;
        return String.format("DOC-%d-%04d", year, count);
    }
}

