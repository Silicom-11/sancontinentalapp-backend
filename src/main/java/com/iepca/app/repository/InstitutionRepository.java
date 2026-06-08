package com.iepca.app.repository;

import com.iepca.app.model.Institution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionRepository extends MongoRepository<Institution, String> {

    Optional<Institution> findByCode(String code);

    Optional<Institution> findFirstByIsActiveTrue();

    boolean existsByCode(String code);

    List<Institution> findByIsActiveTrue();
}

