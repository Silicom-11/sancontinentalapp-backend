package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Location;
import com.iepca.app.model.Parent;
import com.iepca.app.model.Student;
import com.iepca.app.model.embedded.Guardian;
import com.iepca.app.repository.LocationRepository;
import com.iepca.app.repository.ParentRepository;
import com.iepca.app.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private static final double EARTH_RADIUS_M = 6_371_000;
    private static final double SCHOOL_LAT = -10.9279;
    private static final double SCHOOL_LON = -74.8723;
    private static final double SCHOOL_RADIUS_M = 200;

    private final LocationRepository locationRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;

    public LocationService(LocationRepository locationRepository,
                           StudentRepository studentRepository,
                           ParentRepository parentRepository) {
        this.locationRepository = locationRepository;
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
    }

    public Location recordLocation(Location location) {
        if (location.getSessionStatus() == null || location.getSessionStatus().isBlank()) {
            location.setSessionStatus("online");
        }
        if (location.getUpdateType() == null || location.getUpdateType().isBlank()) {
            location.setUpdateType("periodic");
        }
        if (location.getClientTimestamp() == null) {
            location.setClientTimestamp(Instant.now());
        }
        applyGeofence(location);
        Location saved = locationRepository.save(location);
        logger.debug("Ubicacion registrada para usuario: {} | dentro del perimetro: {}",
                saved.getUser(), saved.getInsidePerimeter());
        return saved;
    }

    private void applyGeofence(Location location) {
        Double lat = location.getLatitude();
        Double lon = location.getLongitude();
        if (lat == null || lon == null) return;
        double distance = haversineDistance(lat, lon, SCHOOL_LAT, SCHOOL_LON);
        location.setDistanceToSchool(Math.round(distance * 10.0) / 10.0);
        location.setInsidePerimeter(distance <= SCHOOL_RADIUS_M);
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_M * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public void timeoutStaleLocations(int timeoutMinutes) {
        Instant cutoff = Instant.now().minus(timeoutMinutes, ChronoUnit.MINUTES);
        int count = 0;
        for (Student student : studentRepository.findByIsActiveTrue()) {
            Location last = findLastByStudent(student);
            if (last != null && last.isOnline()
                    && last.getCreatedAt() != null && last.getCreatedAt().isBefore(cutoff)) {
                last.setSessionStatus("offline");
                locationRepository.save(last);
                count++;
            }
        }
        if (count > 0) {
            logger.info("Auto-timeout: {} estudiantes marcados offline (sin actividad por {} min)",
                    count, timeoutMinutes);
        }
    }

    public Location findById(String id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicacion", "id", id));
    }

    public Location getLastLocation(String userId) {
        return locationRepository.findFirstByUserOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicacion", "user", userId));
    }

    public List<Location> getUserHistory(String userId) {
        return locationRepository.findByUserOrderByCreatedAtDesc(userId);
    }

    public List<Location> getOnlineUsers() {
        return locationRepository.findBySessionStatus("online");
    }

    public List<Location> getLatestStudentLocations() {
        List<Location> result = new ArrayList<>();
        for (Student student : studentRepository.findByIsActiveTrue()) {
            Location location = findLastByStudent(student);
            if (location != null) {
                location.setStudent(student);
                result.add(location);
            }
        }
        return result;
    }

    public List<Location> getChildrenLocations(String parentUserId) {
        Parent parent = parentRepository.findByUserId(parentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Padre", "userId", parentUserId));
        List<Location> result = new ArrayList<>();
        for (Guardian child : parent.getChildren()) {
            if (child == null || child.getStudent() == null) continue;
            Student student = studentRepository.findById(child.getStudent()).orElse(null);
            if (student == null) continue;
            Location location = findLastByStudent(student);
            if (location != null) {
                location.setStudent(student);
                result.add(location);
            }
        }
        return result;
    }

    public Location getStudentLocation(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", studentId));
        Location location = findLastByStudent(student);
        if (location == null) {
            throw new ResourceNotFoundException("Ubicacion", "student", studentId);
        }
        location.setStudent(student);
        return location;
    }

    public Map<String, Object> getLocationStats() {
        List<Location> studentLocations = getLatestStudentLocations();
        long online = studentLocations.stream().filter(Location::isOnline).count();
        long offline = studentLocations.size() - online;
        long lowBattery = studentLocations.stream()
                .map(Location::getBatteryLevel)
                .filter(Objects::nonNull)
                .filter(level -> level <= 20)
                .count();
        long insidePerimeter = studentLocations.stream()
                .filter(l -> Boolean.TRUE.equals(l.getInsidePerimeter()))
                .count();
        long outsidePerimeter = studentLocations.stream()
                .filter(l -> Boolean.FALSE.equals(l.getInsidePerimeter()))
                .count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("trackedStudents", studentLocations.size());
        stats.put("online", online);
        stats.put("offline", offline);
        stats.put("insidePerimeter", insidePerimeter);
        stats.put("outsidePerimeter", outsidePerimeter);
        stats.put("lowBattery", lowBattery);
        stats.put("schoolLat", SCHOOL_LAT);
        stats.put("schoolLon", SCHOOL_LON);
        stats.put("schoolRadiusM", SCHOOL_RADIUS_M);
        stats.put("updatedAt", Instant.now());
        return stats;
    }

    public void updateSessionStatus(String userId, String status) {
        Location lastLocation = locationRepository.findFirstByUserOrderByCreatedAtDesc(userId).orElse(null);
        if (lastLocation != null) {
            lastLocation.setSessionStatus(status);
            locationRepository.save(lastLocation);
            logger.info("Estado de sesion actualizado para usuario {}: {}", userId, status);
        }
    }

    public void cleanOldLocations(int daysToKeep) {
        Instant cutoff = Instant.now().minus(daysToKeep, ChronoUnit.DAYS);
        locationRepository.deleteByCreatedAtBefore(cutoff);
        logger.info("Ubicaciones anteriores a {} eliminadas", cutoff);
    }

    private Location findLastByStudent(Student student) {
        if (student == null || student.getUserId() == null || student.getUserId().isBlank()) {
            return null;
        }
        return locationRepository.findFirstByUserOrderByCreatedAtDesc(student.getUserId()).orElse(null);
    }
}
