package com.skillswap.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Security interceptor to detect and prevent common web attacks
 * Implements OWASP security checks and threat detection
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);
    
    @Autowired
    private SecurityAuditService auditService;
    
    @Autowired
    private RateLimitingService rateLimitingService;
    
    // SQL Injection patterns
    private static final List<Pattern> SQL_INJECTION_PATTERNS = Arrays.asList(
        Pattern.compile("(?i).*('|(\\-\\-)|(;)|(\\|)|(\\*)|(%))", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*(union|select|insert|delete|update|drop|create|alter|exec|execute)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*(script|javascript|vbscript|onload|onerror|onclick)", Pattern.CASE_INSENSITIVE)
    );
    
    // XSS patterns
    private static final List<Pattern> XSS_PATTERNS = Arrays.asList(
        Pattern.compile("(?i).*<script.*?>.*?</script>.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*javascript:.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*on(load|error|click|mouse|focus|blur|change|submit)\\s*=.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*<iframe.*?>.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*<object.*?>.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*<embed.*?>.*", Pattern.CASE_INSENSITIVE)
    );
    
    // Path traversal patterns
    private static final List<Pattern> PATH_TRAVERSAL_PATTERNS = Arrays.asList(
        Pattern.compile(".*\\.\\.[\\/\\\\].*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*[\\/\\\\]\\.\\..*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*%2e%2e.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*%252e%252e.*", Pattern.CASE_INSENSITIVE)
    );
    
    // Command injection patterns
    private static final List<Pattern> COMMAND_INJECTION_PATTERNS = Arrays.asList(
        Pattern.compile(".*[;&|`$].*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\$\\(.*\\).*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*`.*`.*", Pattern.CASE_INSENSITIVE)
    );
    
    // Suspicious headers that might indicate bot/scanner activity
    private static final List<String> SUSPICIOUS_USER_AGENTS = Arrays.asList(
        "sqlmap", "nikto", "w3af", "acunetix", "netsparker", "burp", "owasp zap",
        "nmap", "masscan", "dirb", "dirbuster", "gobuster", "curl", "wget"
    );
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ipAddress = auditService.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        
        try {
            // Check rate limiting first
            if (!rateLimitingService.isRequestAllowed(requestUri, ipAddress)) {
                auditService.logRateLimitExceeded(requestUri, ipAddress, 
                    5, // Default attempt count
                    15); // Default window minutes
                response.setStatus(429); // Too Many Requests
                response.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
                return false;
            }
            
            // Check for suspicious user agent
            if (isSuspiciousUserAgent(userAgent)) {
                auditService.logSuspiciousActivity("SUSPICIOUS_USER_AGENT", 
                    "Potential bot/scanner detected", ipAddress, 
                    java.util.Map.of("userAgent", userAgent, "uri", requestUri));
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\":\"Access denied\"}");
                return false;
            }
            
            // Check for path traversal in URI
            if (containsPathTraversal(requestUri)) {
                auditService.logSecurityViolation("PATH_TRAVERSAL", 
                    "Path traversal attempt in URI: " + requestUri, request);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid request\"}");
                return false;
            }
            
            // Check query parameters for injection attempts
            if (containsSqlInjection(request) || containsXss(request) || containsCommandInjection(request)) {
                auditService.logSecurityViolation("INJECTION_ATTEMPT", 
                    "Potential injection attack detected", request);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid input detected\"}");
                return false;
            }
            
            // Check for suspicious headers
            if (hasSuspiciousHeaders(request)) {
                auditService.logSuspiciousActivity("SUSPICIOUS_HEADERS", 
                    "Suspicious request headers detected", ipAddress,
                    java.util.Map.of("uri", requestUri, "method", method));
            }
            
            // Log successful security check for sensitive endpoints
            if (isSensitiveEndpoint(requestUri)) {
                auditService.logSensitiveDataAccess(extractDataType(requestUri), method, 
                    extractResourceId(requestUri), ipAddress);
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error in security interceptor for request {} from {}: {}", 
                        requestUri, ipAddress, e.getMessage());
            // Allow request to proceed in case of interceptor error
            return true;
        }
    }
    
    /**
     * Check if user agent is suspicious
     */
    private boolean isSuspiciousUserAgent(String userAgent) {
        if (userAgent == null || userAgent.trim().isEmpty()) {
            return true; // Empty user agent is suspicious
        }
        
        String lowerUserAgent = userAgent.toLowerCase();
        return SUSPICIOUS_USER_AGENTS.stream().anyMatch(lowerUserAgent::contains);
    }
    
    /**
     * Check for path traversal patterns
     */
    private boolean containsPathTraversal(String input) {
        if (input == null) return false;
        return PATH_TRAVERSAL_PATTERNS.stream().anyMatch(pattern -> pattern.matcher(input).matches());
    }
    
    /**
     * Check for SQL injection patterns in request parameters
     */
    private boolean containsSqlInjection(HttpServletRequest request) {
        return checkParametersForPatterns(request, SQL_INJECTION_PATTERNS);
    }
    
    /**
     * Check for XSS patterns in request parameters
     */
    private boolean containsXss(HttpServletRequest request) {
        return checkParametersForPatterns(request, XSS_PATTERNS);
    }
    
    /**
     * Check for command injection patterns in request parameters
     */
    private boolean containsCommandInjection(HttpServletRequest request) {
        return checkParametersForPatterns(request, COMMAND_INJECTION_PATTERNS);
    }
    
    /**
     * Check request parameters against patterns
     */
    private boolean checkParametersForPatterns(HttpServletRequest request, List<Pattern> patterns) {
        // Check query parameters
        if (request.getQueryString() != null) {
            for (Pattern pattern : patterns) {
                if (pattern.matcher(request.getQueryString()).matches()) {
                    return true;
                }
            }
        }
        
        // Check form parameters
        java.util.Map<String, String[]> params = request.getParameterMap();
        for (String[] values : params.values()) {
            for (String value : values) {
                if (value != null) {
                    for (Pattern pattern : patterns) {
                        if (pattern.matcher(value).matches()) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Check for suspicious headers
     */
    private boolean hasSuspiciousHeaders(HttpServletRequest request) {
        // Check for common attack headers
        String[] suspiciousHeaders = {
            "X-Forwarded-Host", "X-Forwarded-Server", "X-Forwarded-Proto"
        };
        
        for (String header : suspiciousHeaders) {
            String value = request.getHeader(header);
            if (value != null && (value.contains("<script") || value.contains("javascript:"))) {
                return true;
            }
        }
        
        // Check for unusually long headers (potential buffer overflow)
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (headerValue != null && headerValue.length() > 8192) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if endpoint handles sensitive data
     */
    private boolean isSensitiveEndpoint(String uri) {
        return uri.contains("/api/users/") || 
               uri.contains("/api/messages/") ||
               uri.contains("/api/video-sessions/") ||
               uri.contains("/api/credits/") ||
               uri.contains("/api/profile/");
    }
    
    /**
     * Extract data type from URI
     */
    private String extractDataType(String uri) {
        if (uri.contains("/users/")) return "USER_DATA";
        if (uri.contains("/messages/")) return "MESSAGE_DATA";
        if (uri.contains("/video-sessions/")) return "VIDEO_SESSION_DATA";
        if (uri.contains("/credits/")) return "CREDIT_DATA";
        if (uri.contains("/profile/")) return "PROFILE_DATA";
        return "UNKNOWN_DATA";
    }
    
    /**
     * Extract resource ID from URI
     */
    private Long extractResourceId(String uri) {
        try {
            // Look for numeric ID at the end of the path
            String[] parts = uri.split("/");
            for (int i = parts.length - 1; i >= 0; i--) {
                try {
                    return Long.parseLong(parts[i]);
                } catch (NumberFormatException e) {
                    // Continue looking
                }
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return null;
    }
}
