package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Parent;
import com.iepca.app.model.Student;
import com.iepca.app.model.User;
import com.iepca.app.model.embedded.Guardian;
import com.iepca.app.repository.ParentRepository;
import com.iepca.app.repository.StudentRepository;
import com.iepca.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ParentService {

    private static final Logger logger = LoggerFactory.getLogger(ParentService.class);

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ParentService(ParentRepository parentRepository,
                         StudentRepository studentRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Parent> findAll() {
        return parentRepository.findByIsActiveTrue();
    }

    public Parent findById(String id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Padre/Apoderado", "id", id));
    }

    public Parent findByUserId(String userId) {
        return parentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Padre/Apoderado", "userId", userId));
    }

    public Parent create(Parent parent) {
        if (parent.getChildren() == null) {
            parent.setChildren(new ArrayList<>());
        }
        Parent saved = parentRepository.save(parent);

        String userId = createParentAccount(saved);
        if (userId != null) {
            saved.setUserId(userId);
            saved = parentRepository.save(saved);
        }

        logger.info("Padre/Apoderado creado: {} {}", saved.getFirstName(), saved.getLastName());
        return saved;
    }

    private String createParentAccount(Parent parent) {
        String email = parent.getEmail();
        if (email == null || email.isBlank()) {
            String base = (safe(parent.getFirstName()) + "." + safe(parent.getLastName()))
                    .replaceAll("[^a-z0-9.]", "");
            String suffix = parent.getDni() != null && parent.getDni().length() >= 4
                    ? parent.getDni().substring(parent.getDni().length() - 4)
                    : String.valueOf(System.currentTimeMillis() % 10000);
            email = base + suffix + "@iepca.edu.pe";
        }
        if (userRepository.existsByEmail(email)) {
            logger.warn("No se creo cuenta de padre: el email {} ya existe", email);
            return null;
        }

        String rawPassword = parent.getDni() != null && !parent.getDni().isBlank()
                ? parent.getDni() : "Continental2026";

        User user = User.builder()
                .firstName(parent.getFirstName())
                .lastName(parent.getLastName())
                .email(email)
                .dni(parent.getDni())
                .phone(parent.getPhone())
                .password(passwordEncoder.encode(rawPassword))
                .role("padre")
                .isActive(true)
                .isVerified(true)
                .children(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        User savedUser = userRepository.save(user);
        logger.info("Cuenta de padre creada: {} (contrasena inicial = DNI)", email);
        return savedUser.getId();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    public Parent update(String id, Parent updated) {
        Parent existing = findById(id);
        if (updated.getFirstName() != null) existing.setFirstName(updated.getFirstName());
        if (updated.getLastName() != null) existing.setLastName(updated.getLastName());
        if (updated.getDni() != null) existing.setDni(updated.getDni());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getOccupation() != null) existing.setOccupation(updated.getOccupation());
        if (updated.getWorkplace() != null) existing.setWorkplace(updated.getWorkplace());
        if (updated.getWorkPhone() != null) existing.setWorkPhone(updated.getWorkPhone());
        if (updated.getNotifications() != null) existing.setNotifications(updated.getNotifications());
        return parentRepository.save(existing);
    }

    public void delete(String id) {
        Parent parent = findById(id);
        parent.setIsActive(false);
        parentRepository.save(parent);
        logger.info("Padre/Apoderado desactivado: {} {}", parent.getFirstName(), parent.getLastName());
    }

    public Parent addChild(String parentId, Guardian child) {
        Parent parent = findById(parentId);
        if (parent.getChildren() == null) {
            parent.setChildren(new ArrayList<>());
        }
        parent.getChildren().add(child);
        Parent saved = parentRepository.save(parent);

        syncStudentGuardian(child.getStudent(), parent.getUserId(), child.getRelationship(), true);
        syncUserChildren(parent.getUserId(), child.getStudent(), child.getRelationship(), true);

        logger.info("Hijo agregado al padre {}: estudiante {}", parentId, child.getStudent());
        return saved;
    }

    public Parent removeChild(String parentId, String studentId) {
        Parent parent = findById(parentId);
        if (parent.getChildren() != null) {
            parent.getChildren().removeIf(g -> studentId.equals(g.getStudent()));
        }
        Parent saved = parentRepository.save(parent);

        syncStudentGuardian(studentId, parent.getUserId(), null, false);
        syncUserChildren(parent.getUserId(), studentId, null, false);

        logger.info("Hijo removido del padre {}: estudiante {}", parentId, studentId);
        return saved;
    }

    private void syncStudentGuardian(String studentId, String parentUserId, String relationship, boolean add) {
        if (studentId == null || parentUserId == null) return;
        try {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) return;
            if (student.getGuardians() == null) student.setGuardians(new ArrayList<>());

            if (add) {
                boolean exists = student.getGuardians().stream()
                        .anyMatch(g -> parentUserId.equals(g.getStudent()));
                if (!exists) {
                    student.getGuardians().add(Guardian.builder()
                            .student(parentUserId)
                            .relationship(relationship)
                            .isPrimaryContact(student.getGuardians().isEmpty())
                            .build());
                    studentRepository.save(student);
                }
            } else {
                student.getGuardians().removeIf(g -> parentUserId.equals(g.getStudent()));
                studentRepository.save(student);
            }
        } catch (Exception e) {
            logger.warn("Error sincronizando guardian del estudiante {}: {}", studentId, e.getMessage());
        }
    }

    private void syncUserChildren(String parentUserId, String studentId, String relationship, boolean add) {
        if (parentUserId == null || studentId == null) return;
        try {
            User user = userRepository.findById(parentUserId).orElse(null);
            if (user == null) return;
            if (user.getChildren() == null) user.setChildren(new ArrayList<>());

            if (add) {
                boolean exists = user.getChildren().stream()
                        .anyMatch(c -> studentId.equals(c.getStudent()));
                if (!exists) {
                    user.getChildren().add(User.ChildReference.builder()
                            .student(studentId)
                            .relationship(relationship)
                            .isPrimaryContact(user.getChildren().isEmpty())
                            .build());
                    userRepository.save(user);
                }
            } else {
                user.getChildren().removeIf(c -> studentId.equals(c.getStudent()));
                userRepository.save(user);
            }
        } catch (Exception e) {
            logger.warn("Error sincronizando children del usuario {}: {}", parentUserId, e.getMessage());
        }
    }

    public List<Parent> findByChildStudent(String studentId) {
        return parentRepository.findByChildStudent(studentId);
    }

    public long countActive() {
        return parentRepository.countByIsActiveTrue();
    }
}
