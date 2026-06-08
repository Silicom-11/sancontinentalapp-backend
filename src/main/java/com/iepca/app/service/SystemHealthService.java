package com.iepca.app.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class SystemHealthService {

    private final Cache<String, Map<String, Object>> snapshotCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .maximumSize(1)
            .build();

    public Map<String, Object> getSnapshot() {
        try {
            return snapshotCache.get("system-health", this::buildSnapshot);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("No se pudo calcular el estado del sistema", exception);
        }
    }

    private Map<String, Object> buildSnapshot() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("status", "UP");
        snapshot.put("application", "IEP Continental Americano");
        snapshot.put("checkedAt", Instant.now().toString());
        snapshot.put("javaVersion", System.getProperty("java.version"));
        snapshot.put("availableProcessors", runtime.availableProcessors());
        snapshot.put("usedMemoryMb", usedMemory / (1024 * 1024));
        snapshot.put("totalMemoryMb", totalMemory / (1024 * 1024));
        snapshot.put("uptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());
        return snapshot;
    }
}
