package com.iepca.app.service;

import com.iepca.app.exception.BadRequestException;
import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Parent;
import com.iepca.app.model.Student;
import com.iepca.app.model.User;
import com.iepca.app.repository.ParentRepository;
import com.iepca.app.repository.StudentRepository;
import com.iepca.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          ParentRepository parentRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        // Crear automáticamente la cuenta de acceso del estudiante para que
        // pueda iniciar sesión desde su propio dispositivo y emitir su GPS real.
        String userId = createStudentAccount(saved);
        if (userId != null) {
            saved.setUserId(userId);
            saved = studentRepository.save(saved);
        }

        logger.info("Estudiante creado: {} ({})", saved.getFullName(), saved.getStudentCode());
        return saved;
    }

    /**
     * Crea una cuenta de usuario (rol estudiante) vinculada al estudiante.
     * Email: el del estudiante si viene, o generado desde nombre + DNI.
     * Contraseña inicial: su DNI (el estudiante puede cambiarla luego).
     * Devuelve el id del usuario creado, o null si no se pudo/ya existe.
     */
    private String createStudentAccount(Student student) {
        String email = student.getEmail();
        if (email == null || email.isBlank()) {
            String base = (safe(student.getFirstName()) + "." + safe(student.getLastName()))
                    .replaceAll("[^a-z0-9.]", "");
            String suffix = student.getDni() != null && student.getDni().length() >= 4
                    ? student.getDni().substring(student.getDni().length() - 4)
                    : String.valueOf(System.currentTimeMillis() % 10000);
            email = base + suffix + "@iepca.edu.pe";
        }
        if (userRepository.existsByEmail(email)) {
            logger.warn("No se creó cuenta: el email {} ya existe", email);
            return null;
        }

        String rawPassword = student.getDni() != null && !student.getDni().isBlank()
                ? student.getDni() : "Continental2026";

        User user = User.builder()
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(email)
                .dni(student.getDni())
                .phone(student.getPhone())
                .password(passwordEncoder.encode(rawPassword))
                .role("estudiante")
                .isActive(true)
                .isVerified(true)
                .studentProfile(student.getId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        User savedUser = userRepository.save(user);
        logger.info("Cuenta de estudiante creada: {} (contraseña inicial = DNI)", email);
        return savedUser.getId();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
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

