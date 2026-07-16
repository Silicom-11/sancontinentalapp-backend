package com.iepca.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Endpoint público de salud usado por Render y monitoreo externo.
 * Devuelve 200 sin requerir autenticación.
 */
@RestController
public class HealthController {

    @GetMapping({"/api/health", "/"})
    public Map<String, Object> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "UP");
        body.put("service", "IEP Continental Americano - Backend");
        body.put("time", Instant.now().toString());
        return body;
    }
}
