package com.iepca.app.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    @Test
    void shouldGenerateAndValidateRoleAwareToken() {
        JwtTokenProvider provider = new JwtTokenProvider(
                "continental_test_secret_key_2026_more_than_32_chars",
                60_000,
                120_000
        );

        String token = provider.generateToken("user-123", "administrativo");

        assertTrue(provider.validateToken(token));
        assertEquals("user-123", provider.getUserIdFromToken(token));
        assertEquals("administrativo", provider.getRoleFromToken(token));
    }

    @Test
    void shouldRejectMalformedToken() {
        JwtTokenProvider provider = new JwtTokenProvider(
                "continental_test_secret_key_2026_more_than_32_chars",
                60_000,
                120_000
        );

        assertFalse(provider.validateToken("not-a-valid-jwt"));
    }
}
