package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Event;
import com.iepca.app.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findByIsActiveTrue();
    }

    public Event findById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", "id", id));
    }

    public Event create(Event event) {
        Event saved = eventRepository.save(event);
        logger.info("Evento creado: {}", saved.getTitle());
        return saved;
    }

    public Event update(String id, Event updated) {
        Event existing = findById(id);
        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getDate() != null) existing.setDate(updated.getDate());
        if (updated.getTime() != null) existing.setTime(updated.getTime());
        if (updated.getType() != null) existing.setType(updated.getType());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getLocation() != null) existing.setLocation(updated.getLocation());
        if (updated.getParticipants() != null) existing.setParticipants(updated.getParticipants());
        if (updated.getNotifyStudents() != null) existing.setNotifyStudents(updated.getNotifyStudents());
        if (updated.getNotifyParents() != null) existing.setNotifyParents(updated.getNotifyParents());
        if (updated.getNotifyTeachers() != null) existing.setNotifyTeachers(updated.getNotifyTeachers());
        return eventRepository.save(existing);
    }

    public void delete(String id) {
        Event event = findById(id);
        event.setIsActive(false);
        eventRepository.save(event);
        logger.info("Evento desactivado: {}", event.getTitle());
    }

    public List<Event> findByDateRange(String startDate, String endDate) {
        return eventRepository.findByDateBetweenAndIsActiveTrue(startDate, endDate);
    }
}

