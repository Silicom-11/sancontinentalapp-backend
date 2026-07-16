package com.iepca.app.security;

import com.iepca.app.dto.request.LoginRequest;
import com.iepca.app.dto.request.RegisterRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidationSecurityTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void loginRequestShouldRejectNullEmail() {
        LoginRequest request = new LoginRequest();
        request.setPassword("password");

        var violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void loginRequestShouldRejectNullPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@iepca.edu.pe");

        var violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void registerRequestShouldRejectMissingFields() {
        RegisterRequest request = new RegisterRequest();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void sqlInjectionInEmailShouldNotPassValidation() {
        LoginRequest request = new LoginRequest();
        request.setEmail("' OR 1=1 --");
        request.setPassword("password");

        var violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void xssInNameShouldBeAcceptedButSanitizedByService() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("<script>alert('xss')</script>");
        request.setLastName("Normal");
        request.setEmail("test@iepca.edu.pe");
        request.setPassword("ValidPass123");
        request.setDni("70000001");
        request.setRole("estudiante");

        var violations = validator.validate(request);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    void validLoginRequestShouldPassValidation() {
        LoginRequest request = new LoginRequest();
        request.setEmail("docente@iepca.edu.pe");
        request.setPassword("SecurePassword123");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validRegisterRequestShouldPassValidation() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Sofia");
        request.setLastName("Ramirez");
        request.setEmail("sofia@iepca.edu.pe");
        request.setPassword("SecurePassword123");
        request.setDni("70000001");
        request.setRole("estudiante");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
