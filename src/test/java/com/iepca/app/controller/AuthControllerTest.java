package com.iepca.app.controller;

import com.iepca.app.dto.request.LoginRequest;
import com.iepca.app.dto.request.RegisterRequest;
import com.iepca.app.dto.response.AuthResponse;
import com.iepca.app.dto.response.UserResponse;
import com.iepca.app.service.AuthService;
import com.iepca.app.service.UserService;
import com.iepca.app.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private UserService userService;
    private JwtTokenProvider jwtTokenProvider;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        userService = mock(UserService.class);
        jwtTokenProvider = new JwtTokenProvider(
                "continental_test_secret_key_2026_more_than_32_chars", 60_000, 120_000);
        authController = new AuthController(authService, userService, jwtTokenProvider);
    }

    @Test
    void loginShouldReturnOkWithTokens() {
        AuthResponse mockResponse = AuthResponse.builder()
                .success(true).message("Login exitoso")
                .token("jwt-token").refreshToken("refresh-token")
                .user(UserResponse.builder().id("u1").email("admin@iepca.edu.pe").build())
                .build();
        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@iepca.edu.pe");
        request.setPassword("password");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void registerShouldReturnOkOnSuccess() {
        AuthResponse mockResponse = AuthResponse.builder()
                .success(true).message("Registro exitoso")
                .token("jwt-token").refreshToken("refresh-token")
                .user(UserResponse.builder().id("u2").email("nuevo@iepca.edu.pe").build())
                .build();
        when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("nuevo@iepca.edu.pe");
        request.setPassword("pass123");
        request.setDni("70000099");
        request.setRole("estudiante");

        ResponseEntity<?> response = authController.register(request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void refreshTokenShouldReturnNewTokenForValidRefresh() {
        String validRefreshToken = jwtTokenProvider.generateRefreshToken("user-1");

        ResponseEntity<?> response = authController.refreshToken(
                Map.of("refreshToken", validRefreshToken));

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void refreshTokenShouldReturn401ForInvalidToken() {
        ResponseEntity<?> response = authController.refreshToken(
                Map.of("refreshToken", "invalid-token"));

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void refreshTokenShouldReturn401WhenTokenMissing() {
        ResponseEntity<?> response = authController.refreshToken(Map.of());

        assertEquals(401, response.getStatusCode().value());
    }
}
