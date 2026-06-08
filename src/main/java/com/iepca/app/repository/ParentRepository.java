package com.iepca.app.repository;

import com.iepca.app.model.Parent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentRepository extends MongoRepository<Parent, String> {

    Optional<Parent> findByDni(String dni);

    Optional<Parent> findByEmail(String email);

    List<Parent> findByIsActiveTrue();

    @Query("{'children.student': ?0}")
    List<Parent> findByChildStudent(String studentId);

    Optional<Parent> findByUserId(String userId);

    long countByIsActiveTrue();

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);
}

