package com.skillswap.backend.security;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Input Validation Service
 * Provides secure validation for user inputs to prevent injection attacks
 */
@Component
public class InputValidationService {

    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9]+$"
    );
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    // SQL Injection keywords to detect
    private static final String[] SQL_INJECTION_PATTERNS = {
        "union", "select", "insert", "update", "delete", "drop", "create", "alter",
        "exec", "execute", "script", "javascript:", "vbscript:", "onload", "onerror"
    };

    // XSS patterns to detect
    private static final String[] XSS_PATTERNS = {
        "<script", "</script>", "javascript:", "vbscript:", "onload=", "onerror=",
        "onclick=", "onmouseover=", "alert(", "confirm(", "prompt("
    };

    /**
     * Validate email format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        email = email.trim().toLowerCase();
        
        // Length validation
        if (email.length() > 254) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate password strength
     */
    public ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.invalid("Password is required");
        }
        
        if (password.length() < 8) {
            return ValidationResult.invalid("Password must be at least 8 characters long");
        }
        
        if (password.length() > 128) {
            return ValidationResult.invalid("Password too long");
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return ValidationResult.invalid(
                "Password must contain at least one lowercase letter, " +
                "one uppercase letter, one digit, and one special character"
            );
        }
        
        // Check for common passwords
        if (isCommonPassword(password)) {
            return ValidationResult.invalid("Password is too common");
        }
        
        return ValidationResult.valid();
    }

    /**
     * Sanitize string input to prevent XSS and injection attacks
     */
    public String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove potential XSS vectors
        String sanitized = input;
        for (String pattern : XSS_PATTERNS) {
            sanitized = sanitized.replaceAll("(?i)" + Pattern.quote(pattern), "");
        }
        
        // Remove potential SQL injection vectors
        for (String pattern : SQL_INJECTION_PATTERNS) {
            sanitized = sanitized.replaceAll("(?i)\\b" + pattern + "\\b", "");
        }
        
        // Remove null bytes and control characters
        sanitized = sanitized.replaceAll("\\x00", "").replaceAll("[\\x00-\\x1F\\x7F]", "");
        
        // Limit length
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 1000);
        }
        
        return sanitized.trim();
    }

    /**
     * Validate safe string (names, descriptions, etc.)
     */
    public ValidationResult validateSafeString(String input, int maxLength) {
        if (input == null || input.trim().isEmpty()) {
            return ValidationResult.invalid("Input is required");
        }
        
        String trimmed = input.trim();
        
        if (trimmed.length() > maxLength) {
            return ValidationResult.invalid("Input too long (max " + maxLength + " characters)");
        }
        
        // Check for potential injection attempts
        if (containsSuspiciousContent(trimmed)) {
            return ValidationResult.invalid("Input contains invalid characters");
        }
        
        return ValidationResult.valid();
    }

    /**
     * Validate credit amount
     */
    public ValidationResult validateCreditAmount(BigDecimal amount) {
        if (amount == null) {
            return ValidationResult.invalid("Amount is required");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.invalid("Amount must be positive");
        }
        
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            return ValidationResult.invalid("Amount too large (max 10,000 credits)");
        }
        
        // Check decimal places
        if (amount.scale() > 2) {
            return ValidationResult.invalid("Amount can have at most 2 decimal places");
        }
        
        return ValidationResult.valid();
    }

    /**
     * Validate user ID format
     */
    public ValidationResult validateUserId(Long userId) {
        if (userId == null) {
            return ValidationResult.invalid("User ID is required");
        }
        
        if (userId <= 0) {
            return ValidationResult.invalid("Invalid user ID");
        }
        
        // Reasonable upper bound for user IDs
        if (userId > 999999999L) {
            return ValidationResult.invalid("User ID out of range");
        }
        
        return ValidationResult.valid();
    }

    /**
     * Validate session ID format
     */
    public ValidationResult validateSessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return ValidationResult.invalid("Session ID is required");
        }
        
        String trimmed = sessionId.trim();
        
        // Session IDs should be alphanumeric and reasonable length
        if (trimmed.length() < 10 || trimmed.length() > 100) {
            return ValidationResult.invalid("Invalid session ID format");
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(trimmed.replace("-", "")).matches()) {
            return ValidationResult.invalid("Session ID contains invalid characters");
        }
        
        return ValidationResult.valid();
    }

    /**
     * Check if string contains suspicious content
     */
    private boolean containsSuspiciousContent(String input) {
        String lowerInput = input.toLowerCase();
        
        // Check for XSS patterns
        for (String pattern : XSS_PATTERNS) {
            if (lowerInput.contains(pattern.toLowerCase())) {
                return true;
            }
        }
        
        // Check for SQL injection patterns
        for (String pattern : SQL_INJECTION_PATTERNS) {
            if (lowerInput.contains(pattern.toLowerCase())) {
                return true;
            }
        }
        
        // Check for suspicious characters
        if (input.contains("../") || input.contains("..\\") || 
            input.contains("%2e%2e") || input.contains("0x")) {
            return true;
        }
        
        return false;
    }

    /**
     * Check if password is commonly used
     */
    private boolean isCommonPassword(String password) {
        String[] commonPasswords = {
            "password", "123456", "password123", "admin", "qwerty",
            "letmein", "welcome", "monkey", "dragon", "sunshine"
        };
        
        String lowerPassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowerPassword.contains(common)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
