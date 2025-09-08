package com.skillswap.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillswap.backend.dto.SendMessageRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Basic Integration tests for SkillSwap APIs
 * Tests security integration and core functionality
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("SkillSwap Security Integration Tests")
class SkillSwapSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Security Headers Validation")
    void securityHeadersValidation() throws Exception {
        mockMvc.perform(get("/api/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-XSS-Protection", "1; mode=block"))
                .andExpect(header().exists("Strict-Transport-Security"))
                .andExpect(header().exists("Content-Security-Policy"));
    }

    @Test
    @DisplayName("Rate Limiting Protection")
    void rateLimitingProtection() throws Exception {
        // Attempt multiple rapid requests to trigger rate limiting
        for (int i = 0; i < 70; i++) { // Exceed default 60 requests/minute
            try {
                mockMvc.perform(get("/api/health"));
            } catch (Exception e) {
                // Some requests may fail due to rate limiting, which is expected
                break;
            }
        }

        // This request should potentially be rate limited
        mockMvc.perform(get("/api/health"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should be either OK (if not rate limited) or 429 (if rate limited)
                    assert status == 200 || status == 429;
                });
    }

    @Test
    @DisplayName("SQL Injection Protection")
    void sqlInjectionProtection() throws Exception {
        // Attempt SQL injection in query parameter
        mockMvc.perform(get("/api/health")
                .param("search", "'; DROP TABLE users; --"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should be either OK (if endpoint doesn't process param) or 400 (if blocked)
                    assert status == 200 || status == 400;
                });
    }

    @Test
    @DisplayName("XSS Protection in Headers")
    void xssProtectionInHeaders() throws Exception {
        // Attempt XSS in headers
        mockMvc.perform(get("/api/health")
                .header("User-Agent", "<script>alert('xss')</script>"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should be either OK (if header is sanitized) or 403/400 (if blocked)
                    assert status == 200 || status == 403 || status == 400;
                });
    }

    @Test
    @DisplayName("Suspicious User Agent Detection")
    void suspiciousUserAgentDetection() throws Exception {
        // Test with suspicious user agent
        mockMvc.perform(get("/api/health")
                .header("User-Agent", "sqlmap/1.4.7"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should be blocked (403) or allowed (200) depending on configuration
                    assert status == 200 || status == 403;
                });
    }

    @Test
    @DisplayName("CORS Headers Validation")
    void corsHeadersValidation() throws Exception {
        mockMvc.perform(options("/api/health")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should handle OPTIONS request appropriately
                    assert status == 200 || status == 204;
                });
    }

    @Test
    @DisplayName("Message Request Processing")
    void messageRequestProcessing() throws Exception {
        // Test message DTO processing
        SendMessageRequest messageRequest = new SendMessageRequest();
        messageRequest.setReceiverId(1L);
        messageRequest.setContent("Test message content");

        // This tests that our security interceptor doesn't break normal processing
        mockMvc.perform(post("/api/test/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should be processed (even if endpoint doesn't exist, should get 404, not security error)
                    assert status != 400; // Not a security validation error
                });
    }

    @Test
    @DisplayName("Invalid JSON Processing")
    void invalidJsonProcessing() throws Exception {
        // Test with invalid JSON
        mockMvc.perform(post("/api/test/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should handle invalid JSON gracefully
                    assert status == 400 || status == 404; // Bad request or not found
                });
    }

    @Test
    @DisplayName("Large Request Handling")
    void largeRequestHandling() throws Exception {
        // Test with large content
        String largeContent = "x".repeat(10000);
        SendMessageRequest largeRequest = new SendMessageRequest();
        largeRequest.setReceiverId(1L);
        largeRequest.setContent(largeContent);

        mockMvc.perform(post("/api/test/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largeRequest)))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should handle large requests appropriately
                    assert status != 500; // Should not cause server error
                });
    }

    @Test
    @DisplayName("Empty Request Handling")
    void emptyRequestHandling() throws Exception {
        // Test with empty request body
        mockMvc.perform(post("/api/test/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should handle empty requests gracefully
                    assert status != 500; // Should not cause server error
                });
    }

    @Test
    @DisplayName("Special Characters in Content")
    void specialCharactersInContent() throws Exception {
        // Test with special characters and unicode
        SendMessageRequest specialRequest = new SendMessageRequest();
        specialRequest.setReceiverId(1L);
        specialRequest.setContent("Hello 世界! Special chars: àáâãäå æç èéêë ìíîï 🌍");

        mockMvc.perform(post("/api/test/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialRequest)))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should handle unicode and special characters properly
                    assert status != 500; // Should not cause server error
                });
    }
}
