package com.iepca.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtSecurityTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(
                "continental_test_secret_key_2026_more_than_32_chars", 60_000, 120_000);
    }

    @Test
    void shouldRejectExpiredToken() {
        JwtTokenProvider shortLived = new JwtTokenProvider(
                "continental_test_secret_key_2026_more_than_32_chars", 1, 1);
        String token = shortLived.generateToken("user-1", "docente");

        try { Thread.sleep(50); } catch (InterruptedException ignored) {}

        assertFalse(shortLived.validateToken(token));
    }

    @Test
    void shouldRejectTokenSignedWithDifferentKey() {
        JwtTokenProvider other = new JwtTokenProvider(
                "another_secret_key_that_is_longer_than_32_chars!", 60_000, 120_000);
        String token = other.generateToken("user-1", "administrativo");

        assertFalse(provider.validateToken(token));
    }

    @Test
    void shouldRejectEmptyToken() {
        assertFalse(provider.validateToken(""));
    }

    @Test
    void shouldRejectNullToken() {
        assertFalse(provider.validateToken(null));
    }

    @Test
    void shouldRejectTokenWithTamperedPayload() {
        String token = provider.generateToken("user-1", "docente");
        String[] parts = token.split("\\.");
        String tampered = parts[0] + ".dGFtcGVyZWQ." + parts[2];

        assertFalse(provider.validateToken(tampered));
    }

    @Test
    void shouldPreserveRoleClaimAcrossTokenLifecycle() {
        String token = provider.generateToken("user-1", "administrativo");
        String role = provider.getRoleFromToken(token);
        assertEquals("administrativo", role);
    }

    @Test
    void refreshTokenShouldNotContainRole() {
        String refreshToken = provider.generateRefreshToken("user-1");
        String role = provider.getRoleFromToken(refreshToken);
        assertNull(role);
    }

    @Test
    void differentUsersShouldGetDifferentTokens() {
        String token1 = provider.generateToken("user-1", "docente");
        String token2 = provider.generateToken("user-2", "docente");
        assertNotEquals(token1, token2);
    }

    @Test
    void sameUserDifferentRolesShouldGetDifferentTokens() {
        String token1 = provider.generateToken("user-1", "docente");
        String token2 = provider.generateToken("user-1", "administrativo");
        assertNotEquals(token1, token2);
    }

    @Test
    void shouldRejectRandomBase64String() {
        assertFalse(provider.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0In0.fake"));
    }
}
