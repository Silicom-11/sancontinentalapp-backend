package com.iepca.app.repository;

import com.iepca.app.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByDni(String dni);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    List<User> findByRole(String role);

    List<User> findByRoleAndIsActiveTrue(String role);

    List<User> findByIsActiveTrue();

    List<User> findByChildren_Student(String studentId);

    Optional<User> findByPasswordResetToken(String token);

    long countByRole(String role);

    long countByRoleAndIsActiveTrue(String role);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);
}

