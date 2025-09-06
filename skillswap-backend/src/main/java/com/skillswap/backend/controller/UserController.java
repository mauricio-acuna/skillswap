package com.skillswap.backend.controller;

import com.skillswap.backend.model.User;
import com.skillswap.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for User management operations
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Operations for managing users in the SkillSwap platform")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Create a new user
     */
    @PostMapping
    @Operation(summary = "Create a new user", description = "Register a new user in the platform")
    public ResponseEntity<RestApiResponse<User>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        logger.info("Creating new user with email: {}", request.getEmail());
        
        try {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPasswordHash(request.getPassword()); // Will be encoded in service
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setUserType(request.getUserType());
            user.setCountry(request.getCountry());
            user.setTimezone(request.getTimezone());
            user.setLanguage(request.getLanguage());
            user.setGdprConsentGiven(request.getGdprConsent());
            user.setMarketingConsent(request.getMarketingConsent());
            
            User createdUser = userService.createUser(user);
            
            // Remove sensitive data from response
            createdUser.setPasswordHash(null);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RestApiResponse<>(true, "User created successfully", createdUser));
                    
        } catch (RuntimeException e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RestApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    public ResponseEntity<RestApiResponse<User>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        logger.info("Fetching user with ID: {}", id);
        
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPasswordHash(null); // Remove sensitive data
            return ResponseEntity.ok(new RestApiResponse<>(true, "User found", user));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestApiResponse<>(false, "User not found", null));
        }
    }

    /**
     * Get user by email
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve a specific user by their email address")
    public ResponseEntity<RestApiResponse<User>> getUserByEmail(
            @Parameter(description = "User email") @PathVariable String email) {
        
        logger.info("Fetching user with email: {}", email);
        
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPasswordHash(null); // Remove sensitive data
            return ResponseEntity.ok(new RestApiResponse<>(true, "User found", user));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestApiResponse<>(false, "User not found", null));
        }
    }

    /**
     * Get all users with pagination
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination")
    public ResponseEntity<RestApiResponse<Page<User>>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.info("Fetching all users - page: {}, size: {}", page, size);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<User> users = userService.getAllUsers(pageable);
        
        // Remove sensitive data
        users.getContent().forEach(user -> user.setPasswordHash(null));
        
        return ResponseEntity.ok(new RestApiResponse<>(true, "Users retrieved successfully", users));
    }

    /**
     * Search users
     */
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name or bio content")
    public ResponseEntity<RestApiResponse<List<User>>> searchUsers(
            @Parameter(description = "Search text") @RequestParam String q) {
        
        logger.info("Searching users with query: {}", q);
        
        List<User> users = userService.searchUsers(q);
        
        // Remove sensitive data
        users.forEach(user -> user.setPasswordHash(null));
        
        return ResponseEntity.ok(new RestApiResponse<>(true, "Search completed", users));
    }

    /**
     * Get teachers
     */
    @GetMapping("/teachers")
    @Operation(summary = "Get all teachers", description = "Retrieve users who can teach")
    public ResponseEntity<RestApiResponse<List<User>>> getTeachers() {
        
        logger.info("Fetching all teachers");
        
        List<User> teachers = userService.getTeachers();
        
        // Remove sensitive data
        teachers.forEach(user -> user.setPasswordHash(null));
        
        return ResponseEntity.ok(new RestApiResponse<>(true, "Teachers retrieved successfully", teachers));
    }

    /**
     * Get learners
     */
    @GetMapping("/learners")
    @Operation(summary = "Get all learners", description = "Retrieve users who want to learn")
    public ResponseEntity<RestApiResponse<List<User>>> getLearners() {
        
        logger.info("Fetching all learners");
        
        List<User> learners = userService.getLearners();
        
        // Remove sensitive data
        learners.forEach(user -> user.setPasswordHash(null));
        
        return ResponseEntity.ok(new RestApiResponse<>(true, "Learners retrieved successfully", learners));
    }

    /**
     * Update user profile
     */
    @PutMapping("/{id}/profile")
    @Operation(summary = "Update user profile", description = "Update user profile information")
    public ResponseEntity<RestApiResponse<User>> updateProfile(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        
        logger.info("Updating profile for user ID: {}", id);
        
        try {
            User profileData = new User();
            profileData.setFirstName(request.getFirstName());
            profileData.setLastName(request.getLastName());
            profileData.setDisplayName(request.getDisplayName());
            profileData.setBio(request.getBio());
            profileData.setCountry(request.getCountry());
            profileData.setTimezone(request.getTimezone());
            profileData.setLanguage(request.getLanguage());
            profileData.setProfilePictureUrl(request.getProfilePictureUrl());
            
            User updatedUser = userService.updateProfile(id, profileData);
            updatedUser.setPasswordHash(null); // Remove sensitive data
            
            return ResponseEntity.ok(new RestApiResponse<>(true, "Profile updated successfully", updatedUser));
            
        } catch (RuntimeException e) {
            logger.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Change user password
     */
    @PutMapping("/{id}/password")
    @Operation(summary = "Change user password", description = "Change user password with current password verification")
    public ResponseEntity<RestApiResponse<String>> changePassword(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        
        logger.info("Changing password for user ID: {}", id);
        
        try {
            userService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(new RestApiResponse<>(true, "Password changed successfully", null));
            
        } catch (RuntimeException e) {
            logger.error("Error changing password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RestApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Verify email
     */
    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify user email with verification token")
    public ResponseEntity<RestApiResponse<String>> verifyEmail(
            @RequestBody Map<String, String> request) {
        
        String token = request.get("token");
        logger.info("Verifying email with token: {}", token);
        
        try {
            userService.verifyEmail(token);
            return ResponseEntity.ok(new RestApiResponse<>(true, "Email verified successfully", null));
            
        } catch (RuntimeException e) {
            logger.error("Error verifying email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RestApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete user account (soft delete)")
    public ResponseEntity<RestApiResponse<String>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        logger.info("Deleting user with ID: {}", id);
        
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new RestApiResponse<>(true, "User deleted successfully", null));
            
        } catch (RuntimeException e) {
            logger.error("Error deleting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestApiResponse<>(false, e.getMessage(), null));
        }
    }

    // DTO Classes for request/response
    
    /**
     * Generic API Response wrapper
     */
    public static class RestApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public RestApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
    
    public static class CreateUserRequest {
        @jakarta.validation.constraints.Email
        @jakarta.validation.constraints.NotBlank
        private String email;
        
        @jakarta.validation.constraints.NotBlank
        @jakarta.validation.constraints.Size(min = 8)
        private String password;
        
        @jakarta.validation.constraints.NotBlank
        private String firstName;
        
        @jakarta.validation.constraints.NotBlank
        private String lastName;
        
        private User.UserType userType = User.UserType.LEARNER;
        private String country;
        private String timezone;
        private String language;
        private Boolean gdprConsent = false;
        private Boolean marketingConsent = false;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public User.UserType getUserType() { return userType; }
        public void setUserType(User.UserType userType) { this.userType = userType; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public Boolean getGdprConsent() { return gdprConsent; }
        public void setGdprConsent(Boolean gdprConsent) { this.gdprConsent = gdprConsent; }
        public Boolean getMarketingConsent() { return marketingConsent; }
        public void setMarketingConsent(Boolean marketingConsent) { this.marketingConsent = marketingConsent; }
    }

    public static class UpdateProfileRequest {
        private String firstName;
        private String lastName;
        private String displayName;
        private String bio;
        private String country;
        private String timezone;
        private String language;
        private String profilePictureUrl;

        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getProfilePictureUrl() { return profilePictureUrl; }
        public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    }

    public static class ChangePasswordRequest {
        @jakarta.validation.constraints.NotBlank
        private String currentPassword;
        
        @jakarta.validation.constraints.NotBlank
        @jakarta.validation.constraints.Size(min = 8)
        private String newPassword;

        // Getters and setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
