package com.iepca.app.controller;

import com.iepca.app.dto.request.ForgotPasswordRequest;
import com.iepca.app.dto.request.LoginRequest;
import com.iepca.app.dto.request.RegisterRequest;
import com.iepca.app.dto.request.ResetPasswordRequest;
import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.dto.response.AuthResponse;
import com.iepca.app.model.User;
import com.iepca.app.service.AuthService;
import com.iepca.app.service.UserService;
import com.iepca.app.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Usuario registrado exitosamente", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Inicio de sesiÃ³n exitoso", response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.<Void>ok(
                "Si el correo existe, se enviarÃ¡ un enlace de recuperaciÃ³n"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.<Void>ok("ContraseÃ±a restablecida exitosamente"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Token de refresco invÃ¡lido"));
        }
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String role = jwtTokenProvider.getRoleFromToken(refreshToken);
        String newToken = jwtTokenProvider.generateToken(userId, role);
        return ResponseEntity.ok(ApiResponse.ok(
                "Token refrescado exitosamente", Map.of("token", newToken)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(Authentication authentication) {
        String userId = authentication.getName();
        User user = authService.getCurrentUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            Authentication authentication,
            @RequestBody Map<String, Object> updates) {
        String userId = authentication.getName();
        User user = userService.findById(userId);
        if (updates.containsKey("firstName")) user.setFirstName((String) updates.get("firstName"));
        if (updates.containsKey("lastName")) user.setLastName((String) updates.get("lastName"));
        if (updates.containsKey("phone")) user.setPhone((String) updates.get("phone"));
        if (updates.containsKey("avatar")) user.setPhoto((String) updates.get("avatar"));
        User updated = userService.update(userId, user);
        return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado", updated));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @RequestBody Map<String, String> body) {
        String userId = authentication.getName();
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        authService.changePassword(userId, currentPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.<Void>ok("ContraseÃ±a cambiada exitosamente"));
    }

    @PostMapping("/push-token")
    public ResponseEntity<ApiResponse<Void>> savePushToken(
            Authentication authentication,
            @RequestBody Map<String, String> body) {
        String userId = authentication.getName();
        String token = body.get("token");
        String platform = body.get("platform");
        String device = body.get("device");
        authService.savePushToken(userId, token, platform, device);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Token de push registrado"));
    }

    @DeleteMapping("/push-token")
    public ResponseEntity<ApiResponse<Void>> removePushToken(
            Authentication authentication,
            @RequestBody Map<String, String> body) {
        String userId = authentication.getName();
        String token = body.get("token");
        authService.removePushToken(userId, token);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Token de push eliminado"));
    }
}

