package com.skillswap.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Skill exchange matches between teachers and learners
 */
@Entity
@Table(name = "skill_matches")
@EntityListeners(AuditingEntityListener.class)
public class SkillMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_user_id", nullable = false)
    private User teacherUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_user_id", nullable = false)
    private User learnerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_skill_id", nullable = false)
    private UserSkill teacherSkill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_skill_id", nullable = false)
    private UserSkill learnerSkill;

    @Column(name = "match_score", precision = 5, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Match score cannot be negative")
    @DecimalMax(value = "100.0", message = "Match score cannot exceed 100")
    private BigDecimal matchScore = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MatchStatus status = MatchStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false, length = 20)
    private MatchType matchType = MatchType.BILATERAL;

    // Teacher preferences
    @Column(name = "teacher_hourly_rate", precision = 10, scale = 2)
    private BigDecimal teacherHourlyRate;

    @Column(name = "teacher_currency", length = 3)
    private String teacherCurrency = "USD";

    @Column(name = "teacher_preferred_duration")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 480, message = "Duration cannot exceed 480 minutes")
    private Integer teacherPreferredDuration;

    @Column(name = "teacher_preferred_schedule", columnDefinition = "TEXT")
    private String teacherPreferredSchedule;

    // Learner preferences
    @Column(name = "learner_max_budget", precision = 10, scale = 2)
    private BigDecimal learnerMaxBudget;

    @Column(name = "learner_currency", length = 3)
    private String learnerCurrency = "USD";

    @Column(name = "learner_preferred_duration")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 480, message = "Duration cannot exceed 480 minutes")
    private Integer learnerPreferredDuration;

    @Column(name = "learner_preferred_schedule", columnDefinition = "TEXT")
    private String learnerPreferredSchedule;

    // Match metadata
    @Column(name = "matched_at", nullable = false)
    private LocalDateTime matchedAt = LocalDateTime.now();

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    // Rejection/completion details
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejection_by_user_id")
    private User rejectionByUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status", length = 20)
    private CompletionStatus completionStatus;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Rating and feedback
    @Column(name = "teacher_rating")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer teacherRating;

    @Column(name = "learner_rating")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer learnerRating;

    @Column(name = "teacher_feedback", columnDefinition = "TEXT")
    private String teacherFeedback;

    @Column(name = "learner_feedback", columnDefinition = "TEXT")
    private String learnerFeedback;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public SkillMatch() {}

    public SkillMatch(User teacherUser, User learnerUser, UserSkill teacherSkill, UserSkill learnerSkill) {
        this.teacherUser = teacherUser;
        this.learnerUser = learnerUser;
        this.teacherSkill = teacherSkill;
        this.learnerSkill = learnerSkill;
    }

    // Enums
    public enum MatchStatus {
        PENDING, ACCEPTED, REJECTED, EXPIRED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    public enum MatchType {
        BILATERAL, UNILATERAL, GROUP, MENTORSHIP
    }

    public enum CompletionStatus {
        SUCCESS, PARTIAL, FAILED, CANCELLED
    }

    // Helper methods
    public boolean isPending() {
        return MatchStatus.PENDING.equals(status);
    }

    public boolean isActive() {
        return MatchStatus.ACCEPTED.equals(status) || MatchStatus.IN_PROGRESS.equals(status);
    }

    public boolean isCompleted() {
        return MatchStatus.COMPLETED.equals(status);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTeacherUser() {
        return teacherUser;
    }

    public void setTeacherUser(User teacherUser) {
        this.teacherUser = teacherUser;
    }

    public User getLearnerUser() {
        return learnerUser;
    }

    public void setLearnerUser(User learnerUser) {
        this.learnerUser = learnerUser;
    }

    public UserSkill getTeacherSkill() {
        return teacherSkill;
    }

    public void setTeacherSkill(UserSkill teacherSkill) {
        this.teacherSkill = teacherSkill;
    }

    public UserSkill getLearnerSkill() {
        return learnerSkill;
    }

    public void setLearnerSkill(UserSkill learnerSkill) {
        this.learnerSkill = learnerSkill;
    }

    public BigDecimal getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(BigDecimal matchScore) {
        this.matchScore = matchScore;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public BigDecimal getTeacherHourlyRate() {
        return teacherHourlyRate;
    }

    public void setTeacherHourlyRate(BigDecimal teacherHourlyRate) {
        this.teacherHourlyRate = teacherHourlyRate;
    }

    public String getTeacherCurrency() {
        return teacherCurrency;
    }

    public void setTeacherCurrency(String teacherCurrency) {
        this.teacherCurrency = teacherCurrency;
    }

    public Integer getTeacherPreferredDuration() {
        return teacherPreferredDuration;
    }

    public void setTeacherPreferredDuration(Integer teacherPreferredDuration) {
        this.teacherPreferredDuration = teacherPreferredDuration;
    }

    public String getTeacherPreferredSchedule() {
        return teacherPreferredSchedule;
    }

    public void setTeacherPreferredSchedule(String teacherPreferredSchedule) {
        this.teacherPreferredSchedule = teacherPreferredSchedule;
    }

    public BigDecimal getLearnerMaxBudget() {
        return learnerMaxBudget;
    }

    public void setLearnerMaxBudget(BigDecimal learnerMaxBudget) {
        this.learnerMaxBudget = learnerMaxBudget;
    }

    public String getLearnerCurrency() {
        return learnerCurrency;
    }

    public void setLearnerCurrency(String learnerCurrency) {
        this.learnerCurrency = learnerCurrency;
    }

    public Integer getLearnerPreferredDuration() {
        return learnerPreferredDuration;
    }

    public void setLearnerPreferredDuration(Integer learnerPreferredDuration) {
        this.learnerPreferredDuration = learnerPreferredDuration;
    }

    public String getLearnerPreferredSchedule() {
        return learnerPreferredSchedule;
    }

    public void setLearnerPreferredSchedule(String learnerPreferredSchedule) {
        this.learnerPreferredSchedule = learnerPreferredSchedule;
    }

    public LocalDateTime getMatchedAt() {
        return matchedAt;
    }

    public void setMatchedAt(LocalDateTime matchedAt) {
        this.matchedAt = matchedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public User getRejectionByUser() {
        return rejectionByUser;
    }

    public void setRejectionByUser(User rejectionByUser) {
        this.rejectionByUser = rejectionByUser;
    }

    public CompletionStatus getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(CompletionStatus completionStatus) {
        this.completionStatus = completionStatus;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getTeacherRating() {
        return teacherRating;
    }

    public void setTeacherRating(Integer teacherRating) {
        this.teacherRating = teacherRating;
    }

    public Integer getLearnerRating() {
        return learnerRating;
    }

    public void setLearnerRating(Integer learnerRating) {
        this.learnerRating = learnerRating;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public String getLearnerFeedback() {
        return learnerFeedback;
    }

    public void setLearnerFeedback(String learnerFeedback) {
        this.learnerFeedback = learnerFeedback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkillMatch that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SkillMatch{" +
                "id=" + id +
                ", status=" + status +
                ", matchType=" + matchType +
                ", matchScore=" + matchScore +
                '}';
    }
}
