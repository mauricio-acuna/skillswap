package com.skillswap.backend.service;

import com.skillswap.backend.model.User;
import com.skillswap.backend.model.UserCredits;
import com.skillswap.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for User entity operations
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new user
     */
    public User createUser(User user) {
        logger.info("Creating new user with email: {}", user.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        // Encode password
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        
        // Generate email verification token
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        
        // Set default values
        user.setAccountStatus(User.AccountStatus.PENDING_VERIFICATION);
        user.setIsEmailVerified(false);
        user.setIsPhoneVerified(false);
        user.setIsProfileComplete(false);
        user.setIsPremium(false);
        user.setGdprConsentGiven(true);
        user.setGdprConsentDate(LocalDateTime.now());
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Create user credits account
        UserCredits userCredits = new UserCredits(savedUser);
        savedUser.setUserCredits(userCredits);
        
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Update an existing user
     */
    public User updateUser(User user) {
        logger.info("Updating user with ID: {}", user.getId());
        
        // Check if user exists
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("User not found with ID: " + user.getId());
        }
        
        return userRepository.save(user);
    }

    /**
     * Find user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Get active users
     */
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findActiveUsers();
    }

    /**
     * Get teachers
     */
    @Transactional(readOnly = true)
    public List<User> getTeachers() {
        return userRepository.findTeachers();
    }

    /**
     * Get learners
     */
    @Transactional(readOnly = true)
    public List<User> getLearners() {
        return userRepository.findLearners();
    }

    /**
     * Search users by text
     */
    @Transactional(readOnly = true)
    public List<User> searchUsers(String searchText) {
        return userRepository.searchUsers(searchText);
    }

    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Set account status to DELETED instead of hard delete for GDPR compliance
            user.setAccountStatus(User.AccountStatus.DELETED);
            user.setDataRetentionDate(LocalDateTime.now().plusDays(30)); // 30 days retention
            userRepository.save(user);
            logger.info("User marked as deleted with ID: {}", id);
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }

    /**
     * Verify user email
     */
    public User verifyEmail(String verificationToken) {
        logger.info("Verifying email with token: {}", verificationToken);
        
        Optional<User> userOpt = userRepository.findByEmailVerificationToken(verificationToken);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsEmailVerified(true);
            user.setEmailVerificationToken(null);
            
            // Update account status if both email and phone are verified
            if (user.getIsPhoneVerified()) {
                user.setAccountStatus(User.AccountStatus.ACTIVE);
            }
            
            User savedUser = userRepository.save(user);
            logger.info("Email verified for user ID: {}", savedUser.getId());
            return savedUser;
        } else {
            throw new RuntimeException("Invalid email verification token: " + verificationToken);
        }
    }

    /**
     * Verify user phone
     */
    public User verifyPhone(String phoneToken) {
        logger.info("Verifying phone with token: {}", phoneToken);
        
        Optional<User> userOpt = userRepository.findByPhoneVerificationToken(phoneToken);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsPhoneVerified(true);
            user.setPhoneVerificationToken(null);
            
            // Update account status if both email and phone are verified
            if (user.getIsEmailVerified()) {
                user.setAccountStatus(User.AccountStatus.ACTIVE);
            }
            
            User savedUser = userRepository.save(user);
            logger.info("Phone verified for user ID: {}", savedUser.getId());
            return savedUser;
        } else {
            throw new RuntimeException("Invalid phone verification token: " + phoneToken);
        }
    }

    /**
     * Generate password reset token
     */
    public String generatePasswordResetToken(String email) {
        logger.info("Generating password reset token for email: {}", email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String resetToken = UUID.randomUUID().toString();
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetExpiresAt(LocalDateTime.now().plusHours(24)); // 24 hours expiry
            
            userRepository.save(user);
            logger.info("Password reset token generated for user ID: {}", user.getId());
            return resetToken;
        } else {
            throw new RuntimeException("User not found with email: " + email);
        }
    }

    /**
     * Reset password using token
     */
    public User resetPassword(String resetToken, String newPassword) {
        logger.info("Resetting password with token: {}", resetToken);
        
        Optional<User> userOpt = userRepository.findByPasswordResetToken(resetToken);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check if token is expired
            if (user.getPasswordResetExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Password reset token has expired");
            }
            
            // Update password and clear reset token
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setPasswordResetToken(null);
            user.setPasswordResetExpiresAt(null);
            
            User savedUser = userRepository.save(user);
            logger.info("Password reset successfully for user ID: {}", savedUser.getId());
            return savedUser;
        } else {
            throw new RuntimeException("Invalid password reset token: " + resetToken);
        }
    }

    /**
     * Update user last active timestamp
     */
    public void updateLastActive(Long userId) {
        userRepository.updateLastActiveAt(userId, LocalDateTime.now());
    }

    /**
     * Change user password
     */
    public User changePassword(Long userId, String currentPassword, String newPassword) {
        logger.info("Changing password for user ID: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                throw new RuntimeException("Current password is incorrect");
            }
            
            // Update password
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            User savedUser = userRepository.save(user);
            
            logger.info("Password changed successfully for user ID: {}", userId);
            return savedUser;
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }

    /**
     * Update user profile
     */
    public User updateProfile(Long userId, User profileData) {
        logger.info("Updating profile for user ID: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Update allowed profile fields
            if (profileData.getFirstName() != null) {
                user.setFirstName(profileData.getFirstName());
            }
            if (profileData.getLastName() != null) {
                user.setLastName(profileData.getLastName());
            }
            if (profileData.getDisplayName() != null) {
                user.setDisplayName(profileData.getDisplayName());
            }
            if (profileData.getBio() != null) {
                user.setBio(profileData.getBio());
            }
            if (profileData.getCountry() != null) {
                user.setCountry(profileData.getCountry());
            }
            if (profileData.getTimezone() != null) {
                user.setTimezone(profileData.getTimezone());
            }
            if (profileData.getLanguage() != null) {
                user.setLanguage(profileData.getLanguage());
            }
            if (profileData.getProfilePictureUrl() != null) {
                user.setProfilePictureUrl(profileData.getProfilePictureUrl());
            }
            
            // Check if profile is now complete
            boolean isComplete = user.getFirstName() != null && 
                               user.getLastName() != null && 
                               user.getBio() != null && 
                               user.getCountry() != null;
            user.setIsProfileComplete(isComplete);
            
            User savedUser = userRepository.save(user);
            logger.info("Profile updated successfully for user ID: {}", userId);
            return savedUser;
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }

    /**
     * Suspend user account
     */
    public User suspendUser(Long userId, String reason) {
        logger.info("Suspending user ID: {} for reason: {}", userId, reason);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setAccountStatus(User.AccountStatus.SUSPENDED);
            
            User savedUser = userRepository.save(user);
            logger.info("User suspended successfully with ID: {}", userId);
            return savedUser;
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }

    /**
     * Reactivate user account
     */
    public User reactivateUser(Long userId) {
        logger.info("Reactivating user ID: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setAccountStatus(User.AccountStatus.ACTIVE);
            
            User savedUser = userRepository.save(user);
            logger.info("User reactivated successfully with ID: {}", userId);
            return savedUser;
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countByAccountStatus(User.AccountStatus.ACTIVE);
        Long verifiedUsers = userRepository.countVerifiedUsers();
        Long teachers = userRepository.countByUserType(User.UserType.TEACHER);
        Long learners = userRepository.countByUserType(User.UserType.LEARNER);
        Long bothType = userRepository.countByUserType(User.UserType.BOTH);
        
        return new UserStatistics(totalUsers, activeUsers, verifiedUsers, teachers, learners, bothType);
    }

    /**
     * User statistics data class
     */
    public static class UserStatistics {
        private final Long totalUsers;
        private final Long activeUsers;
        private final Long verifiedUsers;
        private final Long teachers;
        private final Long learners;
        private final Long bothType;

        public UserStatistics(Long totalUsers, Long activeUsers, Long verifiedUsers, 
                            Long teachers, Long learners, Long bothType) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.verifiedUsers = verifiedUsers;
            this.teachers = teachers;
            this.learners = learners;
            this.bothType = bothType;
        }

        // Getters
        public Long getTotalUsers() { return totalUsers; }
        public Long getActiveUsers() { return activeUsers; }
        public Long getVerifiedUsers() { return verifiedUsers; }
        public Long getTeachers() { return teachers; }
        public Long getLearners() { return learners; }
        public Long getBothType() { return bothType; }
    }
}
