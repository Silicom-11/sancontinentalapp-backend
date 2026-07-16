package com.iepca.app.service;

import com.iepca.app.dto.request.LoginRequest;
import com.iepca.app.dto.request.RegisterRequest;
import com.iepca.app.dto.response.AuthResponse;
import com.iepca.app.exception.BadRequestException;
import com.iepca.app.exception.UnauthorizedException;
import com.iepca.app.model.User;
import com.iepca.app.repository.UserRepository;
import com.iepca.app.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private EmailService emailService;
    private SecurityAuditService securityAuditService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder(4);
        jwtTokenProvider = new JwtTokenProvider(
                "continental_test_secret_key_2026_more_than_32_chars", 60_000, 120_000);
        emailService = mock(EmailService.class);
        securityAuditService = mock(SecurityAuditService.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenProvider,
                emailService, securityAuditService);
    }

    @Test
    void registerShouldCreateUserAndReturnTokens() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByDni(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("new-user-id");
            return u;
        });

        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Sofia");
        request.setLastName("Ramirez");
        request.setEmail("sofia@iepca.edu.pe");
        request.setPassword("SecurePass123");
        request.setDni("70000001");
        request.setRole("estudiante");

        AuthResponse response = authService.register(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("sofia@iepca.edu.pe", response.getUser().getEmail());
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        when(userRepository.existsByEmail("admin@iepca.edu.pe")).thenReturn(true);

        RegisterRequest request = new RegisterRequest();
        request.setEmail("admin@iepca.edu.pe");
        request.setDni("70000002");

        assertThrows(BadRequestException.class, () -> authService.register(request));
    }

    @Test
    void loginShouldReturnTokensForValidCredentials() {
        String encoded = passwordEncoder.encode("password123");
        User user = User.builder()
                .id("user-1").email("docente@iepca.edu.pe").password(encoded)
                .role("docente").isActive(true).loginAttempts(0).build();
        when(userRepository.findByEmail("docente@iepca.edu.pe")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        LoginRequest request = new LoginRequest();
        request.setEmail("docente@iepca.edu.pe");
        request.setPassword("password123");

        AuthResponse response = authService.login(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());
        verify(securityAuditService).loginSuccess("user-1", "docente@iepca.edu.pe", "docente");
    }

    @Test
    void loginShouldRejectInvalidPassword() {
        String encoded = passwordEncoder.encode("correctPassword");
        User user = User.builder()
                .id("user-2").email("padre@iepca.edu.pe").password(encoded)
                .role("padre").isActive(true).loginAttempts(0).build();
        when(userRepository.findByEmail("padre@iepca.edu.pe")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        LoginRequest request = new LoginRequest();
        request.setEmail("padre@iepca.edu.pe");
        request.setPassword("wrongPassword");

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
        verify(securityAuditService).loginFailure("padre@iepca.edu.pe", "BAD_PASSWORD_ATTEMPT_1");
    }

    @Test
    void loginShouldLockAccountAfterFiveFailedAttempts() {
        String encoded = passwordEncoder.encode("correctPassword");
        User user = User.builder()
                .id("user-3").email("locked@iepca.edu.pe").password(encoded)
                .role("estudiante").isActive(true).loginAttempts(4).build();
        when(userRepository.findByEmail("locked@iepca.edu.pe")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        LoginRequest request = new LoginRequest();
        request.setEmail("locked@iepca.edu.pe");
        request.setPassword("wrongPassword");

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
        verify(securityAuditService).accountLocked("user-3", "locked@iepca.edu.pe");
        assertNotNull(user.getLockUntil());
    }

    @Test
    void loginShouldRejectDisabledAccount() {
        User user = User.builder()
                .id("user-4").email("disabled@iepca.edu.pe").password("x")
                .role("docente").isActive(false).loginAttempts(0).build();
        when(userRepository.findByEmail("disabled@iepca.edu.pe")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setEmail("disabled@iepca.edu.pe");
        request.setPassword("any");

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> authService.login(request));
        assertTrue(ex.getMessage().contains("desactivada"));
    }

    @Test
    void loginShouldRejectLockedAccount() {
        User user = User.builder()
                .id("user-5").email("temp@iepca.edu.pe").password("x")
                .role("padre").isActive(true).loginAttempts(5)
                .lockUntil(Instant.now().plusSeconds(1800)).build();
        when(userRepository.findByEmail("temp@iepca.edu.pe")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setEmail("temp@iepca.edu.pe");
        request.setPassword("any");

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void loginShouldResetAttemptsOnSuccess() {
        String encoded = passwordEncoder.encode("pass");
        User user = User.builder()
                .id("user-6").email("reset@iepca.edu.pe").password(encoded)
                .role("administrativo").isActive(true).loginAttempts(3).build();
        when(userRepository.findByEmail("reset@iepca.edu.pe")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        LoginRequest request = new LoginRequest();
        request.setEmail("reset@iepca.edu.pe");
        request.setPassword("pass");

        authService.login(request);

        assertEquals(0, user.getLoginAttempts());
        assertNull(user.getLockUntil());
    }
}
