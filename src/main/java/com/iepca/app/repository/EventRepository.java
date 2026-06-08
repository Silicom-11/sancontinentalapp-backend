package com.iepca.app.repository;

import com.iepca.app.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findByIsActiveTrue();

    List<Event> findByType(String type);

    List<Event> findByTypeAndIsActiveTrue(String type);

    List<Event> findByDateBetween(String startDate, String endDate);

    List<Event> findByDateBetweenAndIsActiveTrue(String startDate, String endDate);

    List<Event> findByCreatedBy(String userId);

    List<Event> findAllByOrderByDateAsc();
}

