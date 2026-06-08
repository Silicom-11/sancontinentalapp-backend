package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Event;
import com.iepca.app.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Event>>> list(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type) {
        List<Event> events;
        if (startDate != null && endDate != null) {
            events = eventService.findByDateRange(startDate, endDate);
        } else {
            events = eventService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(events));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<Event>>> getUpcoming(
            @RequestParam(defaultValue = "10") int limit) {
        List<Event> events = eventService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> getById(@PathVariable String id) {
        Event event = eventService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(event));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Event>> create(@RequestBody Event event) {
        Event created = eventService.create(event);
        return ResponseEntity.ok(ApiResponse.ok("Evento creado exitosamente", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Event>> update(
            @PathVariable String id,
            @RequestBody Event event) {
        Event updated = eventService.update(id, event);
        return ResponseEntity.ok(ApiResponse.ok("Evento actualizado", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        eventService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Evento eliminado"));
    }
}

