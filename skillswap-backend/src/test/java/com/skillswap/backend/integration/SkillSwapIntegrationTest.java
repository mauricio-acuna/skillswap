package com.skillswap.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillswap.backend.dto.SendMessageRequest;

import org.junit.jupiter.api.BeforeEach;
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
 * Integration tests for SkillSwap APIs
 * Tests complete API workflows and security integration
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("SkillSwap API Integration Tests")
class SkillSwapIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String studentToken;
    private String instructorToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create test users and get tokens
        studentToken = createUserAndGetToken("student@test.com", "STUDENT");
        instructorToken = createUserAndGetToken("instructor@test.com", "INSTRUCTOR");
    }

    @Test
    @DisplayName("Complete User Registration and Authentication Flow")
    void completeUserRegistrationAndAuthenticationFlow() throws Exception {
        // Step 1: Register new user
        RegisterRequest registerRequest = RegisterRequest.builder()
            .email("newuser@test.com")
            .password("SecurePassword123!")
            .firstName("New")
            .lastName("User")
            .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        // Step 2: Login with new user
        LoginRequest loginRequest = LoginRequest.builder()
            .email("newuser@test.com")
            .password("SecurePassword123!")
            .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("newuser@test.com"));

        // Step 3: Access protected endpoint
        mockMvc.perform(get("/api/auth/profile")
                .header("Authorization", "Bearer " + extractTokenFromResponse(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@test.com"));
    }

    @Test
    @DisplayName("Video Session Complete Workflow")
    void videoSessionCompleteWorkflow() throws Exception {
        // Step 1: Create video session
        CreateVideoSessionRequest createRequest = CreateVideoSessionRequest.builder()
            .participantId(2L) // Instructor ID
            .scheduledTime(LocalDateTime.now().plusHours(1))
            .topic("React Native Basics")
            .build();

        String response = mockMvc.perform(post("/api/video-sessions")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.topic").value("React Native Basics"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        Long sessionId = extractSessionIdFromResponse(response);

        // Step 2: Accept invitation (as instructor)
        mockMvc.perform(put("/api/video-sessions/" + sessionId + "/accept")
                .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        // Step 3: Start session
        mockMvc.perform(put("/api/video-sessions/" + sessionId + "/start")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // Step 4: End session
        mockMvc.perform(put("/api/video-sessions/" + sessionId + "/end")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // Step 5: Rate session
        mockMvc.perform(put("/api/video-sessions/" + sessionId + "/rate")
                .header("Authorization", "Bearer " + studentToken)
                .param("rating", "5")
                .param("feedback", "Excellent session!"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Credit Transfer and Balance Management")
    void creditTransferAndBalanceManagement() throws Exception {
        // Step 1: Check initial balance
        mockMvc.perform(get("/api/credits/balance")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").exists());

        // Step 2: Transfer credits
        TransferCreditsRequest transferRequest = TransferCreditsRequest.builder()
            .recipientId(2L)
            .amount(new BigDecimal("10.00"))
            .description("Payment for React Native lesson")
            .build();

        mockMvc.perform(post("/api/credits/transfer")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Step 3: Verify transaction history
        mockMvc.perform(get("/api/credits/transactions")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].amount").value(10.00))
                .andExpect(jsonPath("$.content[0].type").value("SPENT_LEARNING"));

        // Step 4: Check updated balance
        mockMvc.perform(get("/api/credits/balance")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Security Headers Validation")
    void securityHeadersValidation() throws Exception {
        mockMvc.perform(get("/api/auth/profile")
                .header("Authorization", "Bearer " + studentToken))
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
            mockMvc.perform(get("/api/credits/balance")
                    .header("Authorization", "Bearer " + studentToken));
        }

        // This request should be rate limited
        mockMvc.perform(get("/api/credits/balance")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("SQL Injection Protection")
    void sqlInjectionProtection() throws Exception {
        // Attempt SQL injection in query parameter
        mockMvc.perform(get("/api/video-sessions")
                .header("Authorization", "Bearer " + studentToken)
                .param("search", "'; DROP TABLE users; --"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("XSS Protection")
    void xssProtection() throws Exception {
        // Attempt XSS in request body
        CreateVideoSessionRequest xssRequest = CreateVideoSessionRequest.builder()
            .participantId(2L)
            .scheduledTime(LocalDateTime.now().plusHours(1))
            .topic("<script>alert('xss')</script>")
            .build();

        mockMvc.perform(post("/api/video-sessions")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Unauthorized Access Protection")
    void unauthorizedAccessProtection() throws Exception {
        // Attempt to access protected endpoint without token
        mockMvc.perform(get("/api/credits/balance"))
                .andExpect(status().isUnauthorized());

        // Attempt to access with invalid token
        mockMvc.perform(get("/api/credits/balance")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Role-Based Access Control")
    void roleBasedAccessControl() throws Exception {
        // Student trying to access admin endpoint (if any)
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Message Exchange Workflow")
    void messageExchangeWorkflow() throws Exception {
        // Send message
        mockMvc.perform(post("/api/messages")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"recipientId\": 2, \"content\": \"Hello instructor!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello instructor!"));

        // Get messages
        mockMvc.perform(get("/api/messages")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        // Get conversation
        mockMvc.perform(get("/api/messages/conversation/2")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Input Validation")
    void inputValidation() throws Exception {
        // Test with invalid email format
        RegisterRequest invalidRegister = RegisterRequest.builder()
            .email("invalid-email")
            .password("short")
            .firstName("")
            .lastName("")
            .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("CORS Headers")
    void corsHeaders() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"));
    }

    // Helper methods

    private String createUserAndGetToken(String email, String role) throws Exception {
        // Register user
        RegisterRequest registerRequest = RegisterRequest.builder()
            .email(email)
            .password("SecurePassword123!")
            .firstName("Test")
            .lastName("User")
            .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Login and get token
        LoginRequest loginRequest = LoginRequest.builder()
            .email(email)
            .password("SecurePassword123!")
            .build();

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return extractTokenFromResponse(response);
    }

    private String extractTokenFromResponse(String response) throws Exception {
        return objectMapper.readTree(response).get("token").asText();
    }

    private String extractTokenFromResponse(LoginRequest loginRequest) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        return extractTokenFromResponse(response);
    }

    private Long extractSessionIdFromResponse(String response) throws Exception {
        return objectMapper.readTree(response).get("id").asLong();
    }
}
