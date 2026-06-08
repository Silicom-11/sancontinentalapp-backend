package com.iepca.app.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SecurityAuditService {

    private static final Logger auditLogger = LoggerFactory.getLogger("SECURITY_AUDIT");

    public void loginSuccess(String userId, String email, String role) {
        auditLogger.info("LOGIN_SUCCESS userId={} email={} role={}",
                sanitize(userId), sanitize(email), sanitize(role));
    }

    public void loginFailure(String email, String reason) {
        auditLogger.warn("LOGIN_FAILURE email={} reason={}",
                sanitize(email), sanitize(reason));
    }

    public void accountLocked(String userId, String email) {
        auditLogger.warn("ACCOUNT_LOCKED userId={} email={} durationMinutes=30",
                sanitize(userId), sanitize(email));
    }

    public void passwordResetRequested(String userId, String email) {
        auditLogger.info("PASSWORD_RESET_REQUESTED userId={} email={}",
                sanitize(userId), sanitize(email));
    }

    public void passwordChanged(String userId, String email) {
        auditLogger.info("PASSWORD_CHANGED userId={} email={}",
                sanitize(userId), sanitize(email));
    }

    private String sanitize(String value) {
        return StringUtils.normalizeSpace(StringUtils.defaultString(value, "-"))
                .replace('\r', '_')
                .replace('\n', '_')
                .replace('\t', '_');
    }
}
