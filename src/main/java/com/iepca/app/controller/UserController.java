package com.iepca.app.controller;

import com.iepca.app.dto.response.ApiResponse;
import com.iepca.app.dto.response.UserResponse;
import com.iepca.app.model.User;
import com.iepca.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<List<User>>> list(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        List<User> users;
        if (role != null) {
            users = userService.findByRole(role);
        } else {
            users = userService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        long teachers = userService.countByRole("docente");
        long parents = userService.countByRole("padre");
        long students = userService.countByRole("estudiante");
        long admins = userService.countByRole("administrativo");

        Map<String, Object> stats = Map.of(
                "teachers", teachers,
                "parents", parents,
                "students", students,
                "admins", admins,
                "total", teachers + parents + students + admins
        );
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        String userId = authentication.getName();
        User user = userService.findById(userId);
        UserResponse response = userService.toResponse(user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Authentication authentication,
            @RequestBody User updates) {
        String userId = authentication.getName();
        User updated = userService.update(userId, updates);
        return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado", userService.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<User>> getById(@PathVariable String id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<User>> create(@RequestBody User user) {
        User created = userService.create(user);
        return ResponseEntity.ok(ApiResponse.ok("Usuario creado", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<User>> update(
            @PathVariable String id,
            @RequestBody User user) {
        User updated = userService.update(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrativo', 'director')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("Usuario desactivado"));
    }
}

