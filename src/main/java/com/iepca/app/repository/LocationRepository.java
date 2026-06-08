package com.iepca.app.repository;

import com.iepca.app.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {

    Optional<Location> findTopByUserOrderByCreatedAtDesc(String userId);

    List<Location> findByUser(String userId);

    List<Location> findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
            String userId, Instant startDate, Instant endDate);

    List<Location> findByUserAndSessionStatus(String userId, String sessionStatus);

    List<Location> findBySessionStatusAndCreatedAtAfter(String sessionStatus, Instant after);

    void deleteByCreatedAtBefore(Instant before);

    Optional<Location> findFirstByUserOrderByCreatedAtDesc(String userId);

    List<Location> findByUserOrderByCreatedAtDesc(String userId);

    List<Location> findBySessionStatus(String sessionStatus);
}

