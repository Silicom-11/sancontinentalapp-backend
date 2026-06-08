package com.iepca.app.service;

import com.iepca.app.dto.request.LoginRequest;
import com.iepca.app.dto.request.RegisterRequest;
import com.iepca.app.dto.response.AuthResponse;
import com.iepca.app.dto.response.UserResponse;
import com.iepca.app.exception.BadRequestException;
import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.exception.UnauthorizedException;
import com.iepca.app.model.User;
import com.iepca.app.repository.UserRepository;
import com.iepca.app.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final SecurityAuditService securityAuditService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       EmailService emailService,
                       SecurityAuditService securityAuditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.securityAuditService = securityAuditService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya esta registrado");
        }
        if (userRepository.existsByDni(request.getDni())) {
            throw new BadRequestException("El DNI ya esta registrado");
        }

        User user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .dni(request.getDni().trim())
                .phone(request.getPhone())
                .role(request.getRole())
                .isActive(true)
                .isVerified(false)
                .build();

        user = userRepository.save(user);
        logger.info("Usuario registrado: {} ({})", user.getEmail(), user.getRole());

        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .success(true)
                .message("Registro exitoso")
                .token(token)
                .refreshToken(refreshToken)
                .user(toUserResponse(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        Optional<User> optionalUser = userRepository.findByEmail(normalizedEmail);
        if (optionalUser.isEmpty()) {
            securityAuditService.loginFailure(normalizedEmail, "USER_NOT_FOUND");
            throw new UnauthorizedException("Credenciales invalidas");
        }

        User user = optionalUser.get();
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            securityAuditService.loginFailure(normalizedEmail, "ACCOUNT_DISABLED");
            throw new UnauthorizedException("La cuenta esta desactivada");
        }

        if (user.isLocked()) {
            securityAuditService.loginFailure(normalizedEmail, "ACCOUNT_LOCKED");
            throw new UnauthorizedException("La cuenta esta bloqueada temporalmente. Intente mas tarde.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            if (user.getLoginAttempts() >= 5) {
                user.setLockUntil(Instant.now().plusSeconds(1800)); // 30 min lock
                securityAuditService.accountLocked(user.getId(), user.getEmail());
            }
            userRepository.save(user);
            securityAuditService.loginFailure(normalizedEmail, "BAD_PASSWORD_ATTEMPT_" + user.getLoginAttempts());
            throw new UnauthorizedException("Credenciales invalidas");
        }

        user.setLoginAttempts(0);
        user.setLockUntil(null);
        user.setLastLogin(Instant.now());
        user.setIsOnline(true);
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        logger.info("Login exitoso: {} ({})", user.getEmail(), user.getRole());
        securityAuditService.loginSuccess(user.getId(), user.getEmail(), user.getRole());

        return AuthResponse.builder()
                .success(true)
                .message("Login exitoso")
                .token(token)
                .refreshToken(refreshToken)
                .user(toUserResponse(user))
                .build();
    }

    public void forgotPassword(String email) {
        userRepository.findByEmail(email.toLowerCase().trim()).ifPresent(user -> {
            String resetToken = UUID.randomUUID().toString();
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetExpires(Instant.now().plusSeconds(3600)); // 1 hour
            userRepository.save(user);
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
            logger.info("Token de reset generado para: {}", user.getEmail());
            securityAuditService.passwordResetRequested(user.getId(), user.getEmail());
        });
        // Always return success to prevent email enumeration.
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new BadRequestException("Token invalido o expirado"));

        if (user.getPasswordResetExpires() == null || user.getPasswordResetExpires().isBefore(Instant.now())) {
            throw new BadRequestException("Token expirado");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        user.setLoginAttempts(0);
        user.setLockUntil(null);
        userRepository.save(user);

        logger.info("Contrasena restablecida para: {}", user.getEmail());
        securityAuditService.passwordChanged(user.getId(), user.getEmail());
    }

    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("La contrasena actual es incorrecta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Contrasena cambiada para: {}", user.getEmail());
        securityAuditService.passwordChanged(user.getId(), user.getEmail());
    }

    public void savePushToken(String userId, String token, String platform, String device) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));

        if (user.getPushTokens() == null) {
            user.setPushTokens(new java.util.ArrayList<>());
        }

        user.getPushTokens().removeIf(pt -> token.equals(pt.getToken()));

        while (user.getPushTokens().size() >= 5) {
            user.getPushTokens().remove(0);
        }

        com.iepca.app.model.embedded.PushToken pushToken = new com.iepca.app.model.embedded.PushToken();
        pushToken.setToken(token);
        pushToken.setPlatform(platform);
        pushToken.setDevice(device);
        user.getPushTokens().add(pushToken);
        userRepository.save(user);
    }

    public void removePushToken(String userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));

        if (user.getPushTokens() != null) {
            user.getPushTokens().removeIf(pt -> token.equals(pt.getToken()));
            userRepository.save(user);
        }
    }

    public User getCurrentUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .dni(user.getDni())
                .phone(user.getPhone())
                .role(user.getRole())
                .photo(user.getPhoto())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .permissions(user.getPermissions())
                .studentProfile(user.getStudentProfile())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
