package com.skillswap.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_session")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VideoSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_match_id", nullable = false)
    private SkillMatch skillMatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionType type = SessionType.SKILL_EXCHANGE;

    @Column(name = "scheduled_start_time")
    private LocalDateTime scheduledStartTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "initiator_rating")
    private Integer initiatorRating; // 1-5

    @Column(name = "participant_rating")
    private Integer participantRating; // 1-5

    @Column(name = "initiator_feedback", length = 1000)
    private String initiatorFeedback;

    @Column(name = "participant_feedback", length = 1000)
    private String participantFeedback;

    @Column(name = "session_notes", length = 2000)
    private String sessionNotes;

    @Column(name = "connection_quality")
    private String connectionQuality; // JSON metadata sobre calidad de conexión

    @Column(name = "recording_url")
    private String recordingUrl;

    @Column(name = "is_recorded")
    private Boolean isRecorded = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum SessionStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        FAILED
    }

    public enum SessionType {
        SKILL_EXCHANGE,
        CONSULTATION,
        PRACTICE,
        INTERVIEW_PREP
    }

    // Constructores
    public VideoSession() {}

    public VideoSession(SkillMatch skillMatch, User initiator, User participant, 
                       LocalDateTime scheduledStartTime, SessionType type) {
        this.skillMatch = skillMatch;
        this.initiator = initiator;
        this.participant = participant;
        this.scheduledStartTime = scheduledStartTime;
        this.type = type;
        this.sessionId = generateSessionId();
    }

    // Métodos de utilidad
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 10000);
    }

    public void startSession() {
        this.status = SessionStatus.IN_PROGRESS;
        this.actualStartTime = LocalDateTime.now();
    }

    public void endSession() {
        if (this.actualStartTime != null) {
            this.endTime = LocalDateTime.now();
            this.durationMinutes = (int) java.time.Duration.between(
                this.actualStartTime, this.endTime).toMinutes();
        }
        this.status = SessionStatus.COMPLETED;
    }

    public void cancelSession() {
        this.status = SessionStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
    }

    public boolean canBeRated() {
        return this.status == SessionStatus.COMPLETED;
    }

    public boolean isActive() {
        return this.status == SessionStatus.IN_PROGRESS;
    }

    public boolean isPending() {
        return this.status == SessionStatus.PENDING;
    }

    public boolean involvesUser(User user) {
        return this.initiator.getId().equals(user.getId()) || 
               this.participant.getId().equals(user.getId());
    }

    public User getOtherParticipant(User user) {
        if (this.initiator.getId().equals(user.getId())) {
            return this.participant;
        } else if (this.participant.getId().equals(user.getId())) {
            return this.initiator;
        }
        return null;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SkillMatch getSkillMatch() {
        return skillMatch;
    }

    public void setSkillMatch(SkillMatch skillMatch) {
        this.skillMatch = skillMatch;
    }

    public User getInitiator() {
        return initiator;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public LocalDateTime getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(LocalDateTime scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getInitiatorRating() {
        return initiatorRating;
    }

    public void setInitiatorRating(Integer initiatorRating) {
        this.initiatorRating = initiatorRating;
    }

    public Integer getParticipantRating() {
        return participantRating;
    }

    public void setParticipantRating(Integer participantRating) {
        this.participantRating = participantRating;
    }

    public String getInitiatorFeedback() {
        return initiatorFeedback;
    }

    public void setInitiatorFeedback(String initiatorFeedback) {
        this.initiatorFeedback = initiatorFeedback;
    }

    public String getParticipantFeedback() {
        return participantFeedback;
    }

    public void setParticipantFeedback(String participantFeedback) {
        this.participantFeedback = participantFeedback;
    }

    public String getSessionNotes() {
        return sessionNotes;
    }

    public void setSessionNotes(String sessionNotes) {
        this.sessionNotes = sessionNotes;
    }

    public String getConnectionQuality() {
        return connectionQuality;
    }

    public void setConnectionQuality(String connectionQuality) {
        this.connectionQuality = connectionQuality;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }

    public Boolean getIsRecorded() {
        return isRecorded;
    }

    public void setIsRecorded(Boolean isRecorded) {
        this.isRecorded = isRecorded;
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
}
