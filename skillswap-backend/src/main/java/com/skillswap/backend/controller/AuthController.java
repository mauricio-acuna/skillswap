package com.skillswap.backend.controller;

import com.skillswap.backend.model.User;
import com.skillswap.backend.security.JwtTokenProvider;
import com.skillswap.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Authentication Controller
 * Handles user authentication, registration, and token management
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for email: {}", loginRequest.getEmail());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            // Generate tokens
            JwtTokenProvider.TokenResponse tokenResponse = tokenProvider.generateTokenResponse(authentication);

            logger.info("User logged in successfully: {}", loginRequest.getEmail());
            return ResponseEntity.ok(new LoginResponse(true, "Login successful", tokenResponse));

        } catch (AuthenticationException ex) {
            logger.error("Authentication failed for email: {}", loginRequest.getEmail(), ex);
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Invalid email or password"));
        } catch (Exception ex) {
            logger.error("Login error for email: {}", loginRequest.getEmail(), ex);
            return ResponseEntity.internalServerError()
                .body(new ApiResponse(false, "An error occurred during login"));
        }
    }

    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Registration attempt for email: {}", registerRequest.getEmail());

            // Check if email already exists
            if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email address already in use"));
            }

            // Create new user
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPasswordHash(registerRequest.getPassword()); // Will be encoded in service
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setUserType(User.UserType.valueOf(registerRequest.getUserType().toUpperCase()));
            user.setGdprConsentGiven(registerRequest.getGdprConsent());

            // Save user
            User savedUser = userService.createUser(user);

            logger.info("User registered successfully: {}", savedUser.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, 
                "User registered successfully. Please check your email for verification."));

        } catch (Exception ex) {
            logger.error("Registration error for email: {}", registerRequest.getEmail(), ex);
            return ResponseEntity.internalServerError()
                .body(new ApiResponse(false, "An error occurred during registration"));
        }
    }

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        try {
            String refreshToken = refreshRequest.getRefreshToken();

            // Validate refresh token
            if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid refresh token"));
            }

            // Get user from token
            Long userId = tokenProvider.getUserIdFromToken(refreshToken);
            Optional<User> userOpt = userService.findById(userId);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "User not found"));
            }

            User user = userOpt.get();

            // Generate new tokens
            String newAccessToken = tokenProvider.generateTokenByUserId(
                user.getId(), user.getEmail(), user.getUserType().toString());
            String newRefreshToken = tokenProvider.generateRefreshToken(user.getId());

            JwtTokenProvider.TokenResponse tokenResponse = new JwtTokenProvider.TokenResponse(
                newAccessToken, newRefreshToken, "Bearer", 
                86400L, // 24 hours
                user.getId(), user.getEmail(), user.getUserType().toString()
            );

            logger.info("Token refreshed for user: {}", user.getEmail());
            return ResponseEntity.ok(new LoginResponse(true, "Token refreshed successfully", tokenResponse));

        } catch (Exception ex) {
            logger.error("Token refresh error", ex);
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Failed to refresh token"));
        }
    }

    /**
     * Email verification endpoint
     */
    @GetMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify user email address using token")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            User user = userService.verifyEmail(token);
            logger.info("Email verified for user: {}", user.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Email verified successfully"));
        } catch (Exception ex) {
            logger.error("Email verification error", ex);
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Invalid or expired verification token"));
        }
    }

    /**
     * Password reset request endpoint
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Request password reset token")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotRequest) {
        try {
            userService.generatePasswordResetToken(forgotRequest.getEmail());
            logger.info("Password reset requested for email: {}", forgotRequest.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, 
                "Password reset link sent to your email"));
        } catch (Exception ex) {
            // Don't reveal if email exists or not for security
            return ResponseEntity.ok(new ApiResponse(true, 
                "If the email exists, a password reset link has been sent"));
        }
    }

    /**
     * Password reset endpoint
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using token")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetRequest) {
        try {
            User user = userService.resetPassword(resetRequest.getToken(), resetRequest.getNewPassword());
            logger.info("Password reset for user: {}", user.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Password reset successfully"));
        } catch (Exception ex) {
            logger.error("Password reset error", ex);
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Invalid or expired reset token"));
        }
    }

    // Request/Response DTOs
    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String userType = "LEARNER";
        private Boolean gdprConsent = false;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public Boolean getGdprConsent() { return gdprConsent; }
        public void setGdprConsent(Boolean gdprConsent) { this.gdprConsent = gdprConsent; }
    }

    public static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class ForgotPasswordRequest {
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class ApiResponse {
        private Boolean success;
        private String message;

        public ApiResponse(Boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public Boolean getSuccess() { return success; }
        public void setSuccess(Boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class LoginResponse extends ApiResponse {
        private JwtTokenProvider.TokenResponse tokenResponse;

        public LoginResponse(Boolean success, String message, JwtTokenProvider.TokenResponse tokenResponse) {
            super(success, message);
            this.tokenResponse = tokenResponse;
        }

        public JwtTokenProvider.TokenResponse getTokenResponse() { return tokenResponse; }
        public void setTokenResponse(JwtTokenProvider.TokenResponse tokenResponse) { this.tokenResponse = tokenResponse; }
    }
}
