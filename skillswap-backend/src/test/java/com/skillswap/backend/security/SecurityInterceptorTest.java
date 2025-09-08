package com.skillswap.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SecurityInterceptor
 * Tests security violation detection and prevention
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Security Interceptor Tests")
class SecurityInterceptorTest {

    @Mock
    private SecurityAuditService auditService;

    @Mock
    private RateLimitingService rateLimitingService;

    @InjectMocks
    private SecurityInterceptor securityInterceptor;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        
        // Default setup for successful requests
        when(rateLimitingService.isRequestAllowed(anyString(), anyString())).thenReturn(true);
        when(auditService.getClientIpAddress(any())).thenReturn("127.0.0.1");
    }

    @Test
    @DisplayName("Should allow valid requests")
    void shouldAllowValidRequests() throws Exception {
        // Given
        request.setRequestURI("/api/skills");
        request.setMethod("GET");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should block requests exceeding rate limit")
    void shouldBlockRequestsExceedingRateLimit() throws Exception {
        // Given
        when(rateLimitingService.isRequestAllowed(anyString(), anyString())).thenReturn(false);
        request.setRequestURI("/api/auth/login");
        request.setMethod("POST");

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(429);
        verify(auditService).logRateLimitExceeded(eq("/api/auth/login"), eq("127.0.0.1"), eq(5), eq(15));
    }

    @Test
    @DisplayName("Should block SQL injection attempts")
    void shouldBlockSqlInjectionAttempts() throws Exception {
        // Given
        request.setRequestURI("/api/skills");
        request.setMethod("GET");
        request.setParameter("search", "'; DROP TABLE users; --");
        request.addHeader("User-Agent", "Mozilla/5.0");

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(400);
        verify(auditService).logSecurityViolation(eq("INJECTION_ATTEMPT"), 
            eq("Potential injection attack detected"), eq(request));
    }

    @Test
    @DisplayName("Should block XSS attempts")
    void shouldBlockXssAttempts() throws Exception {
        // Given
        request.setRequestURI("/api/skills");
        request.setMethod("GET");
        request.setParameter("description", "<script>alert('xss')</script>");
        request.addHeader("User-Agent", "Mozilla/5.0");

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(400);
        verify(auditService).logSecurityViolation(eq("INJECTION_ATTEMPT"), 
            eq("Potential injection attack detected"), eq(request));
    }

    @Test
    @DisplayName("Should block path traversal attempts")
    void shouldBlockPathTraversalAttempts() throws Exception {
        // Given
        request.setRequestURI("/api/files/../../../etc/passwd");
        request.setMethod("GET");
        request.addHeader("User-Agent", "Mozilla/5.0");

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(400);
        verify(auditService).logSecurityViolation(eq("PATH_TRAVERSAL"), 
            contains("Path traversal attempt"), eq(request));
    }

    @Test
    @DisplayName("Should block suspicious user agents")
    void shouldBlockSuspiciousUserAgents() throws Exception {
        // Given
        request.setRequestURI("/api/skills");
        request.setMethod("GET");
        request.addHeader("User-Agent", "sqlmap/1.4.7");

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
        verify(auditService).logSuspiciousActivity(eq("SUSPICIOUS_USER_AGENT"), 
            eq("Potential bot/scanner detected"), eq("127.0.0.1"), any());
    }

    @Test
    @DisplayName("Should handle missing user agent")
    void shouldHandleMissingUserAgent() throws Exception {
        // Given
        request.setRequestURI("/api/skills");
        request.setMethod("GET");
        // No User-Agent header

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should log sensitive data access")
    void shouldLogSensitiveDataAccess() throws Exception {
        // Given
        request.setRequestURI("/api/users/123");
        request.setMethod("GET");
        request.addHeader("User-Agent", "Mozilla/5.0");

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isTrue();
        verify(auditService).logSensitiveDataAccess(eq("USER_DATA"), eq("GET"), eq(123L), eq("127.0.0.1"));
    }

    @Test
    @DisplayName("Should detect command injection attempts")
    void shouldDetectCommandInjectionAttempts() throws Exception {
        // Given
        request.setRequestURI("/api/skills");
        request.setMethod("POST");
        request.setParameter("command", "ls -la; cat /etc/passwd");
        request.addHeader("User-Agent", "Mozilla/5.0");

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(400);
        verify(auditService).logSecurityViolation(eq("INJECTION_ATTEMPT"), 
            eq("Potential injection attack detected"), eq(request));
    }

    @Test
    @DisplayName("Should handle interceptor errors gracefully")
    void shouldHandleInterceptorErrorsGracefully() throws Exception {
        // Given
        when(auditService.getClientIpAddress(any())).thenThrow(new RuntimeException("Test error"));
        request.setRequestURI("/api/skills");
        request.setMethod("GET");
        request.addHeader("User-Agent", "Mozilla/5.0");

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isTrue(); // Should allow request to proceed on error
    }

    @Test
    @DisplayName("Should extract resource ID correctly")
    void shouldExtractResourceIdCorrectly() throws Exception {
        // Given
        request.setRequestURI("/api/video-sessions/456/start");
        request.setMethod("POST");
        request.addHeader("User-Agent", "Mozilla/5.0");

        // When
        boolean result = securityInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isTrue();
        verify(auditService).logSensitiveDataAccess(eq("VIDEO_SESSION_DATA"), eq("POST"), eq(456L), eq("127.0.0.1"));
    }
}
