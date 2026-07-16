package com.iepca.app.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SecurityAuditServiceTest {

    private final SecurityAuditService auditService = new SecurityAuditService();

    @Test
    void loginSuccessShouldLogWithoutException() {
        assertDoesNotThrow(() ->
                auditService.loginSuccess("user-1", "admin@iepca.edu.pe", "administrativo"));
    }

    @Test
    void loginFailureShouldLogWithoutException() {
        assertDoesNotThrow(() ->
                auditService.loginFailure("admin@iepca.edu.pe", "BAD_PASSWORD"));
    }

    @Test
    void accountLockedShouldLogWithoutException() {
        assertDoesNotThrow(() ->
                auditService.accountLocked("user-1", "admin@iepca.edu.pe"));
    }

    @Test
    void passwordResetRequestedShouldLogWithoutException() {
        assertDoesNotThrow(() ->
                auditService.passwordResetRequested("user-1", "admin@iepca.edu.pe"));
    }

    @Test
    void passwordChangedShouldLogWithoutException() {
        assertDoesNotThrow(() ->
                auditService.passwordChanged("user-1", "admin@iepca.edu.pe"));
    }

    @Test
    void shouldSanitizeNewlinesInLogInjectionAttempt() {
        assertDoesNotThrow(() ->
                auditService.loginFailure("fake@mail.com\nINFO LOGIN_SUCCESS userId=admin", "INJECTED"));
    }

    @Test
    void shouldSanitizeCarriageReturnInLogInjectionAttempt() {
        assertDoesNotThrow(() ->
                auditService.loginFailure("fake@mail.com\rINFO LOGIN_SUCCESS userId=admin", "INJECTED"));
    }

    @Test
    void shouldSanitizeTabInLogInjectionAttempt() {
        assertDoesNotThrow(() ->
                auditService.loginFailure("fake@mail.com\tINFO LOGIN_SUCCESS", "INJECTED"));
    }

    @Test
    void shouldHandleNullValues() {
        assertDoesNotThrow(() ->
                auditService.loginSuccess(null, null, null));
        assertDoesNotThrow(() ->
                auditService.loginFailure(null, null));
    }

    @Test
    void shouldHandleEmptyStrings() {
        assertDoesNotThrow(() ->
                auditService.loginSuccess("", "", ""));
    }
}
