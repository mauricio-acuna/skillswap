package com.skillswap.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for logging security-related events and audit trails
 * Provides comprehensive logging for authentication, authorization, and security events
 */
@Service
public class SecurityAuditService {
    
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY_AUDIT");
    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditService.class);
    
    /**
     * Log successful authentication events
     */
    public void logSuccessfulAuthentication(String username, String ipAddress, String userAgent) {
        Map<String, Object> auditData = createBaseAuditData("AUTHENTICATION_SUCCESS", username, ipAddress);
        auditData.put("userAgent", userAgent);
        auditData.put("timestamp", LocalDateTime.now());
        
        logSecurityEvent(auditData);
        logger.info("Successful authentication for user: {} from IP: {}", username, ipAddress);
    }
    
    /**
     * Log failed authentication attempts
     */
    public void logFailedAuthentication(String username, String ipAddress, String reason, String userAgent) {
        Map<String, Object> auditData = createBaseAuditData("AUTHENTICATION_FAILED", username, ipAddress);
        auditData.put("reason", reason);
        auditData.put("userAgent", userAgent);
        auditData.put("timestamp", LocalDateTime.now());
        
        logSecurityEvent(auditData);
        logger.warn("Failed authentication attempt for user: {} from IP: {} - Reason: {}", username, ipAddress, reason);
    }
    
    /**
     * Log access denied events
     */
    public void logAccessDenied(String resource, String action, String ipAddress) {
        String username = getCurrentUsername();
        Map<String, Object> auditData = createBaseAuditData("ACCESS_DENIED", username, ipAddress);
        auditData.put("resource", resource);
        auditData.put("action", action);
        auditData.put("timestamp", LocalDateTime.now());
        
        logSecurityEvent(auditData);
        logger.warn("Access denied for user: {} to resource: {} action: {} from IP: {}", 
                   username, resource, action, ipAddress);
    }
    
    /**
     * Log suspicious activity
     */
    public void logSuspiciousActivity(String activityType, String description, String ipAddress, 
                                    Map<String, Object> additionalData) {
        String username = getCurrentUsername();
        Map<String, Object> auditData = createBaseAuditData("SUSPICIOUS_ACTIVITY", username, ipAddress);
        auditData.put("activityType", activityType);
        auditData.put("description", description);
        auditData.put("timestamp", LocalDateTime.now());
        
        if (additionalData != null) {
            auditData.putAll(additionalData);
        }
        
        logSecurityEvent(auditData);
        logger.warn("Suspicious activity detected - Type: {} User: {} IP: {} Description: {}", 
                   activityType, username, ipAddress, description);
    }
    
    /**
     * Log rate limiting events
     */
    public void logRateLimitExceeded(String endpoint, String ipAddress, int attemptCount, long windowMinutes) {
        String username = getCurrentUsername();
        Map<String, Object> auditData = createBaseAuditData("RATE_LIMIT_EXCEEDED", username, ipAddress);
        auditData.put("endpoint", endpoint);
        auditData.put("attemptCount", attemptCount);
        auditData.put("windowMinutes", windowMinutes);
        auditData.put("timestamp", LocalDateTime.now());
        
        logSecurityEvent(auditData);
        logger.warn("Rate limit exceeded for endpoint: {} User: {} IP: {} Attempts: {} in {} minutes", 
                   endpoint, username, ipAddress, attemptCount, windowMinutes);
    }
    
    /**
     * Log security configuration changes
     */
    public void logSecurityConfigChange(String configType, String oldValue, String newValue, String changeReason) {
        String username = getCurrentUsername();
        Map<String, Object> auditData = createBaseAuditData("SECURITY_CONFIG_CHANGE", username, "SYSTEM");
        auditData.put("configType", configType);
        auditData.put("oldValue", maskSensitiveData(oldValue));
        auditData.put("newValue", maskSensitiveData(newValue));
        auditData.put("changeReason", changeReason);
        auditData.put("timestamp", LocalDateTime.now());
        
        logSecurityEvent(auditData);
        logger.info("Security configuration changed - Type: {} by User: {} Reason: {}", 
                   configType, username, changeReason);
    }
    
    /**
     * Log privilege escalation attempts
     */
    public void logPrivilegeEscalation(String attemptedAction, String requiredRole, String currentRole, String ipAddress) {
        String username = getCurrentUsername();
        Map<String, Object> auditData = createBaseAuditData("PRIVILEGE_ESCALATION", username, ipAddress);
        auditData.put("attemptedAction", attemptedAction);
        auditData.put("requiredRole", requiredRole);
        auditData.put("currentRole", currentRole);
        auditData.put("timestamp", LocalDateTime.now());
        
        logSecurityEvent(auditData);
        logger.warn("Privilege escalation attempt - User: {} attempted: {} with role: {} required: {} from IP: {}", 
                   username, attemptedAction, currentRole, requiredRole, ipAddress);
    }
    
    /**
     * Log data access events for sensitive operations
     */
    public void logSensitiveDataAccess(String dataType, String operation, Long recordId, String ipAddress) {
        String username = getCurrentUsername();
        Map<String, Object> auditData = createBaseAuditData("SENSITIVE_DATA_ACCESS", username, ipAddress);
        auditData.put("dataType", dataType);
        auditData.put("operation", operation);
        auditData.put("recordId", recordId);
        auditData.put("timestamp", LocalDateTime.now());
        
        logSecurityEvent(auditData);
        logger.info("Sensitive data access - User: {} operation: {} on {} ID: {} from IP: {}", 
                   username, operation, dataType, recordId, ipAddress);
    }
    
    /**
     * Log security violation attempts
     */
    public void logSecurityViolation(String violationType, String details, HttpServletRequest request) {
        String username = getCurrentUsername();
        String ipAddress = getClientIpAddress(request);
        
        Map<String, Object> auditData = createBaseAuditData("SECURITY_VIOLATION", username, ipAddress);
        auditData.put("violationType", violationType);
        auditData.put("details", details);
        auditData.put("requestUri", request.getRequestURI());
        auditData.put("method", request.getMethod());
        auditData.put("userAgent", request.getHeader("User-Agent"));
        auditData.put("timestamp", LocalDateTime.now());
        
        logSecurityEvent(auditData);
        logger.error("Security violation detected - Type: {} User: {} IP: {} URI: {} Details: {}", 
                    violationType, username, ipAddress, request.getRequestURI(), details);
    }
    
    /**
     * Create base audit data structure
     */
    private Map<String, Object> createBaseAuditData(String eventType, String username, String ipAddress) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("eventType", eventType);
        auditData.put("username", username != null ? username : "ANONYMOUS");
        auditData.put("ipAddress", ipAddress);
        auditData.put("sessionId", MDC.get("sessionId"));
        return auditData;
    }
    
    /**
     * Log security event to dedicated security logger
     */
    private void logSecurityEvent(Map<String, Object> auditData) {
        try {
            // Set MDC context for structured logging
            MDC.put("eventType", (String) auditData.get("eventType"));
            MDC.put("username", (String) auditData.get("username"));
            MDC.put("ipAddress", (String) auditData.get("ipAddress"));
            
            // Log as structured JSON-like format
            securityLogger.info("SECURITY_EVENT: {}", auditData);
        } finally {
            // Clean up MDC
            MDC.remove("eventType");
            MDC.remove("username");
            MDC.remove("ipAddress");
        }
    }
    
    /**
     * Get current authenticated username
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            logger.debug("Could not get current username: {}", e.getMessage());
        }
        return "ANONYMOUS";
    }
    
    /**
     * Extract client IP address from request
     */
    public String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Mask sensitive data for logging
     */
    private String maskSensitiveData(String data) {
        if (data == null || data.length() <= 4) {
            return "****";
        }
        return data.substring(0, 2) + "****" + data.substring(data.length() - 2);
    }
}
