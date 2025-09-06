package com.skillswap.backend.repository;

import com.skillswap.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email verification token
     */
    Optional<User> findByEmailVerificationToken(String token);

    /**
     * Find user by password reset token
     */
    Optional<User> findByPasswordResetToken(String token);

    /**
     * Find user by phone verification token
     */
    Optional<User> findByPhoneVerificationToken(String token);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone number exists
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Find users by account status
     */
    List<User> findByAccountStatus(User.AccountStatus accountStatus);

    /**
     * Find users by user type
     */
    List<User> findByUserType(User.UserType userType);

    /**
     * Find users by country
     */
    List<User> findByCountry(String country);

    /**
     * Find active users (account status = ACTIVE)
     */
    @Query("SELECT u FROM User u WHERE u.accountStatus = 'ACTIVE'")
    List<User> findActiveUsers();

    /**
     * Find verified users (both email and phone verified)
     */
    @Query("SELECT u FROM User u WHERE u.isEmailVerified = true AND u.isPhoneVerified = true")
    List<User> findVerifiedUsers();

    /**
     * Find users who can teach (user type TEACHER or BOTH)
     */
    @Query("SELECT u FROM User u WHERE u.userType IN ('TEACHER', 'BOTH') AND u.accountStatus = 'ACTIVE'")
    List<User> findTeachers();

    /**
     * Find users who can learn (user type LEARNER or BOTH)
     */
    @Query("SELECT u FROM User u WHERE u.userType IN ('LEARNER', 'BOTH') AND u.accountStatus = 'ACTIVE'")
    List<User> findLearners();

    /**
     * Find premium users
     */
    List<User> findByIsPremiumTrue();

    /**
     * Find users with incomplete profiles
     */
    List<User> findByIsProfileCompleteFalse();

    /**
     * Find users by first name and last name (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) " +
           "AND LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<User> findByFirstNameAndLastNameContainingIgnoreCase(@Param("firstName") String firstName, 
                                                              @Param("lastName") String lastName);

    /**
     * Find users by display name (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.displayName) LIKE LOWER(CONCAT('%', :displayName, '%'))")
    List<User> findByDisplayNameContainingIgnoreCase(@Param("displayName") String displayName);

    /**
     * Find recently active users (last active within specified time)
     */
    @Query("SELECT u FROM User u WHERE u.lastActiveAt > :since")
    List<User> findRecentlyActiveUsers(@Param("since") LocalDateTime since);

    /**
     * Find users who haven't been active since specified time
     */
    @Query("SELECT u FROM User u WHERE u.lastActiveAt < :since OR u.lastActiveAt IS NULL")
    List<User> findInactiveUsersSince(@Param("since") LocalDateTime since);

    /**
     * Find users with expired password reset tokens
     */
    @Query("SELECT u FROM User u WHERE u.passwordResetToken IS NOT NULL AND u.passwordResetExpiresAt < :now")
    List<User> findUsersWithExpiredPasswordResetTokens(@Param("now") LocalDateTime now);

    /**
     * Find users by timezone
     */
    List<User> findByTimezone(String timezone);

    /**
     * Find users by language preference
     */
    List<User> findByLanguage(String language);

    /**
     * Search users by text in name or bio
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.displayName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.bio) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<User> searchUsers(@Param("searchText") String searchText);

    /**
     * Count users by account status
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.accountStatus = :status")
    Long countByAccountStatus(@Param("status") User.AccountStatus status);

    /**
     * Count users by user type
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType")
    Long countByUserType(@Param("userType") User.UserType userType);

    /**
     * Count verified users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isEmailVerified = true AND u.isPhoneVerified = true")
    Long countVerifiedUsers();

    /**
     * Find users for GDPR data retention cleanup
     */
    @Query("SELECT u FROM User u WHERE u.dataRetentionDate IS NOT NULL AND u.dataRetentionDate < :now")
    List<User> findUsersForDataRetentionCleanup(@Param("now") LocalDateTime now);

    /**
     * Update last active timestamp for user
     */
    @Query("UPDATE User u SET u.lastActiveAt = :timestamp WHERE u.id = :userId")
    void updateLastActiveAt(@Param("userId") Long userId, @Param("timestamp") LocalDateTime timestamp);

    /**
     * Clear password reset token for user
     */
    @Query("UPDATE User u SET u.passwordResetToken = NULL, u.passwordResetExpiresAt = NULL WHERE u.id = :userId")
    void clearPasswordResetToken(@Param("userId") Long userId);

    /**
     * Clear email verification token for user
     */
    @Query("UPDATE User u SET u.emailVerificationToken = NULL WHERE u.id = :userId")
    void clearEmailVerificationToken(@Param("userId") Long userId);

    /**
     * Clear phone verification token for user
     */
    @Query("UPDATE User u SET u.phoneVerificationToken = NULL WHERE u.id = :userId")
    void clearPhoneVerificationToken(@Param("userId") Long userId);
}
