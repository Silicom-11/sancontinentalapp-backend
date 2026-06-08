package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.model.Location;
import com.iepca.app.model.embedded.Coordinates;
import com.iepca.app.model.embedded.DeviceInfo;
import com.iepca.app.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Location>> recordLocation(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        Location location = buildLocationFromPayload(payload);
        location.setUser(authentication.getName());
        Location saved = locationService.recordLocation(location);
        return ResponseEntity.ok(ApiResponse.ok("Ubicacion registrada", saved));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        locationService.updateSessionStatus(authentication.getName(), "offline");
        return ResponseEntity.ok(ApiResponse.<Void>ok("Sesion GPS cerrada"));
    }

    @PostMapping("/disconnect")
    public ResponseEntity<ApiResponse<Void>> disconnect(Authentication authentication) {
        locationService.updateSessionStatus(authentication.getName(), "offline");
        return ResponseEntity.ok(ApiResponse.<Void>ok("Estudiante desconectado del GPS"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Location>> getMyLocation(Authentication authentication) {
        Location location = locationService.getLastLocation(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok(location));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Location>>> getHistory(Authentication authentication) {
        List<Location> history = locationService.getUserHistory(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok(history));
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<List<Location>>> getStudentLocations() {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getLatestStudentLocations()));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLocationStats() {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getLocationStats()));
    }

    @GetMapping("/children")
    @PreAuthorize("hasRole('padre')")
    public ResponseEntity<ApiResponse<List<Location>>> getChildrenLocations(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getChildrenLocations(authentication.getName())));
    }

    @GetMapping("/child/{studentId}")
    @PreAuthorize("hasAnyRole('padre', 'administrativo', 'director')")
    public ResponseEntity<ApiResponse<Location>> getChildLocation(@PathVariable String studentId) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getStudentLocation(studentId)));
    }

    @GetMapping("/users/online")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<List<Location>>> getOnlineUsers() {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getOnlineUsers()));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Location>> getUserLocation(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getLastLocation(userId)));
    }

    @GetMapping("/user/{userId}/history")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<List<Location>>> getUserHistory(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getUserHistory(userId)));
    }

    @SuppressWarnings("unchecked")
    private Location buildLocationFromPayload(Map<String, Object> payload) {
        Coordinates coordinates = Coordinates.builder()
                .latitude(getDouble(payload, "latitude"))
                .longitude(getDouble(payload, "longitude"))
                .accuracy(getDouble(payload, "accuracy"))
                .altitude(getDouble(payload, "altitude"))
                .speed(getDouble(payload, "speed"))
                .heading(getDouble(payload, "heading"))
                .build();

        Object nested = payload.get("coordinates");
        if (nested instanceof Map<?, ?> nestedMap) {
            Map<String, Object> map = (Map<String, Object>) nestedMap;
            if (coordinates.getLatitude() == null) coordinates.setLatitude(getDouble(map, "latitude"));
            if (coordinates.getLongitude() == null) coordinates.setLongitude(getDouble(map, "longitude"));
            if (coordinates.getAccuracy() == null) coordinates.setAccuracy(getDouble(map, "accuracy"));
            if (coordinates.getAltitude() == null) coordinates.setAltitude(getDouble(map, "altitude"));
            if (coordinates.getSpeed() == null) coordinates.setSpeed(getDouble(map, "speed"));
            if (coordinates.getHeading() == null) coordinates.setHeading(getDouble(map, "heading"));
        }

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .platform(getString(payload, "platform", "android"))
                .deviceId(getString(payload, "deviceId", null))
                .appVersion(getString(payload, "appVersion", null))
                .build();

        return Location.builder()
                .coordinates(coordinates)
                .deviceInfo(deviceInfo)
                .batteryLevel(getDouble(payload, "battery"))
                .networkType(getString(payload, "networkType", "unknown"))
                .sessionStatus(getString(payload, "sessionStatus", "online"))
                .updateType(getString(payload, "updateType", "periodic"))
                .clientTimestamp(Instant.now())
                .build();
    }

    private Double getDouble(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value instanceof Number number) return number.doubleValue();
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String getString(Map<String, Object> payload, String key, String fallback) {
        Object value = payload.get(key);
        return value != null && !value.toString().isBlank() ? value.toString() : fallback;
    }
}
