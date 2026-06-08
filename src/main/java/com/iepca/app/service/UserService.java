package com.iepca.app.service;

import com.iepca.app.dto.response.UserResponse;
import com.iepca.app.exception.BadRequestException;
import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.User;
import com.iepca.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRoleAndIsActiveTrue(role);
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("El email ya estÃ¡ registrado");
        }
        if (user.getDni() != null && userRepository.existsByDni(user.getDni())) {
            throw new BadRequestException("El DNI ya estÃ¡ registrado");
        }
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User update(String id, User updated) {
        User existing = findById(id);
        if (updated.getFirstName() != null) existing.setFirstName(updated.getFirstName());
        if (updated.getLastName() != null) existing.setLastName(updated.getLastName());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getPhoto() != null) existing.setPhoto(updated.getPhoto());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getPermissions() != null) existing.setPermissions(updated.getPermissions());
        if (updated.getSettings() != null) existing.setSettings(updated.getSettings());
        return userRepository.save(existing);
    }

    public void delete(String id) {
        User user = findById(id);
        user.setIsActive(false);
        userRepository.save(user);
        logger.info("Usuario desactivado: {}", user.getEmail());
    }

    public long countByRole(String role) {
        return userRepository.countByRoleAndIsActiveTrue(role);
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .dni(user.getDni())
                .phone(user.getPhone())
                .role(user.getRole())
                .photo(user.getPhoto())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .permissions(user.getPermissions())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

