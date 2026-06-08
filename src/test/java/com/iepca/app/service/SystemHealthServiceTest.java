package com.iepca.app.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemHealthServiceTest {

    @Test
    void shouldReturnCachedHealthSnapshot() {
        SystemHealthService service = new SystemHealthService();

        Map<String, Object> firstSnapshot = service.getSnapshot();
        Map<String, Object> secondSnapshot = service.getSnapshot();

        assertEquals("UP", firstSnapshot.get("status"));
        assertEquals("IEP Continental Americano", firstSnapshot.get("application"));
        assertTrue(firstSnapshot.containsKey("usedMemoryMb"));
        assertSame(firstSnapshot, secondSnapshot);
    }
}
