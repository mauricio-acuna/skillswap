package com.skillswap.backend.dto;

import com.skillswap.backend.model.VideoSession;
import com.skillswap.backend.model.User;
import com.skillswap.backend.model.SkillMatch;

import java.time.LocalDateTime;

public class VideoSessionDTO {
    private Long id;
    private String sessionId;
    private Long skillMatchId;
    private String skillMatchTitle;
    private UserSummaryDTO initiator;
    private UserSummaryDTO participant;
    private VideoSession.SessionStatus status;
    private VideoSession.SessionType type;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Integer initiatorRating;
    private Integer participantRating;
    private String initiatorFeedback;
    private String participantFeedback;
    private String sessionNotes;
    private String connectionQuality;
    private String recordingUrl;
    private Boolean isRecorded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructores
    public VideoSessionDTO() {}

    public VideoSessionDTO(VideoSession videoSession) {
        this.id = videoSession.getId();
        this.sessionId = videoSession.getSessionId();
        this.skillMatchId = videoSession.getSkillMatch().getId();
        this.skillMatchTitle = generateSkillMatchTitle(videoSession.getSkillMatch());
        this.initiator = new UserSummaryDTO(videoSession.getInitiator());
        this.participant = new UserSummaryDTO(videoSession.getParticipant());
        this.status = videoSession.getStatus();
        this.type = videoSession.getType();
        this.scheduledStartTime = videoSession.getScheduledStartTime();
        this.actualStartTime = videoSession.getActualStartTime();
        this.endTime = videoSession.getEndTime();
        this.durationMinutes = videoSession.getDurationMinutes();
        this.initiatorRating = videoSession.getInitiatorRating();
        this.participantRating = videoSession.getParticipantRating();
        this.initiatorFeedback = videoSession.getInitiatorFeedback();
        this.participantFeedback = videoSession.getParticipantFeedback();
        this.sessionNotes = videoSession.getSessionNotes();
        this.connectionQuality = videoSession.getConnectionQuality();
        this.recordingUrl = videoSession.getRecordingUrl();
        this.isRecorded = videoSession.getIsRecorded();
        this.createdAt = videoSession.getCreatedAt();
        this.updatedAt = videoSession.getUpdatedAt();
    }

    private String generateSkillMatchTitle(SkillMatch skillMatch) {
        return skillMatch.getTeacherSkill().getSkill().getName() + " ↔ " + 
               skillMatch.getLearnerSkill().getSkill().getName();
    }

    // Clase interna para resumen de usuario
    public static class UserSummaryDTO {
        private Long id;
        private String name;
        private String email;
        private String profileImageUrl;

        public UserSummaryDTO() {}

        public UserSummaryDTO(User user) {
            this.id = user.getId();
            this.name = user.getFirstName() + " " + user.getLastName();
            this.email = user.getEmail();
            this.profileImageUrl = user.getProfilePictureUrl();
        }

        // Getters y Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getProfileImageUrl() { return profileImageUrl; }
        public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
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

    public Long getSkillMatchId() {
        return skillMatchId;
    }

    public void setSkillMatchId(Long skillMatchId) {
        this.skillMatchId = skillMatchId;
    }

    public String getSkillMatchTitle() {
        return skillMatchTitle;
    }

    public void setSkillMatchTitle(String skillMatchTitle) {
        this.skillMatchTitle = skillMatchTitle;
    }

    public UserSummaryDTO getInitiator() {
        return initiator;
    }

    public void setInitiator(UserSummaryDTO initiator) {
        this.initiator = initiator;
    }

    public UserSummaryDTO getParticipant() {
        return participant;
    }

    public void setParticipant(UserSummaryDTO participant) {
        this.participant = participant;
    }

    public VideoSession.SessionStatus getStatus() {
        return status;
    }

    public void setStatus(VideoSession.SessionStatus status) {
        this.status = status;
    }

    public VideoSession.SessionType getType() {
        return type;
    }

    public void setType(VideoSession.SessionType type) {
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
