package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Parent;
import com.iepca.app.model.embedded.Guardian;
import com.iepca.app.repository.ParentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParentService {

    private static final Logger logger = LoggerFactory.getLogger(ParentService.class);

    private final ParentRepository parentRepository;

    public ParentService(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
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
        logger.info("Padre/Apoderado creado: {} {}", saved.getFirstName(), saved.getLastName());
        return saved;
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
        logger.info("Hijo agregado al padre {}: estudiante {}", parentId, child.getStudent());
        return parentRepository.save(parent);
    }

    public Parent removeChild(String parentId, String studentId) {
        Parent parent = findById(parentId);
        if (parent.getChildren() != null) {
            parent.getChildren().removeIf(g -> studentId.equals(g.getStudent()));
        }
        logger.info("Hijo removido del padre {}: estudiante {}", parentId, studentId);
        return parentRepository.save(parent);
    }

    public List<Parent> findByChildStudent(String studentId) {
        return parentRepository.findByChildStudent(studentId);
    }

    public long countActive() {
        return parentRepository.countByIsActiveTrue();
    }
}

