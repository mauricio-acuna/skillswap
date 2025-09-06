package com.skillswap.backend.service;

import com.skillswap.backend.dto.VideoSessionDTO;
import com.skillswap.backend.model.VideoSession;
import com.skillswap.backend.model.User;
import com.skillswap.backend.model.SkillMatch;
import com.skillswap.backend.repository.VideoSessionRepository;
import com.skillswap.backend.repository.UserRepository;
import com.skillswap.backend.repository.SkillMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VideoSessionService {

    @Autowired
    private VideoSessionRepository videoSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillMatchRepository skillMatchRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Crear nueva sesión de video
    public VideoSessionDTO createVideoSession(Long skillMatchId, Long initiatorId, Long participantId,
                                            LocalDateTime scheduledStartTime, VideoSession.SessionType type) {
        SkillMatch skillMatch = skillMatchRepository.findById(skillMatchId)
            .orElseThrow(() -> new RuntimeException("SkillMatch not found"));
        
        User initiator = userRepository.findById(initiatorId)
            .orElseThrow(() -> new RuntimeException("Initiator user not found"));
        
        User participant = userRepository.findById(participantId)
            .orElseThrow(() -> new RuntimeException("Participant user not found"));

        // Verificar conflictos de horario
        List<VideoSession> conflicts = videoSessionRepository.findConflictingSessions(
            initiator, scheduledStartTime, scheduledStartTime.plusHours(2));
        
        List<VideoSession> participantConflicts = videoSessionRepository.findConflictingSessions(
            participant, scheduledStartTime, scheduledStartTime.plusHours(2));

        if (!conflicts.isEmpty() || !participantConflicts.isEmpty()) {
            throw new RuntimeException("Schedule conflict detected");
        }

        VideoSession videoSession = new VideoSession(skillMatch, initiator, participant, 
                                                    scheduledStartTime, type);
        videoSession = videoSessionRepository.save(videoSession);

        // Notificar al participante via WebSocket
        notifyVideoSessionInvitation(participant, videoSession);

        return new VideoSessionDTO(videoSession);
    }

    // Aceptar invitación de sesión
    public VideoSessionDTO acceptVideoSession(Long sessionId, Long userId) {
        VideoSession videoSession = videoSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Video session not found"));

        if (!videoSession.getParticipant().getId().equals(userId)) {
            throw new RuntimeException("Only the invited participant can accept the session");
        }

        if (videoSession.getStatus() != VideoSession.SessionStatus.PENDING) {
            throw new RuntimeException("Session cannot be accepted in current status: " + videoSession.getStatus());
        }

        videoSession.setStatus(VideoSession.SessionStatus.ACCEPTED);
        videoSession = videoSessionRepository.save(videoSession);

        // Notificar al iniciador
        notifyVideoSessionAccepted(videoSession.getInitiator(), videoSession);

        return new VideoSessionDTO(videoSession);
    }

    // Rechazar invitación de sesión
    public VideoSessionDTO rejectVideoSession(Long sessionId, Long userId) {
        VideoSession videoSession = videoSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Video session not found"));

        if (!videoSession.getParticipant().getId().equals(userId)) {
            throw new RuntimeException("Only the invited participant can reject the session");
        }

        videoSession.setStatus(VideoSession.SessionStatus.REJECTED);
        videoSession = videoSessionRepository.save(videoSession);

        // Notificar al iniciador
        notifyVideoSessionRejected(videoSession.getInitiator(), videoSession);

        return new VideoSessionDTO(videoSession);
    }

    // Iniciar sesión
    public VideoSessionDTO startVideoSession(Long sessionId, Long userId) {
        VideoSession videoSession = videoSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Video session not found"));

        if (!videoSession.involvesUser(userRepository.findById(userId).orElse(null))) {
            throw new RuntimeException("User is not part of this video session");
        }

        if (videoSession.getStatus() != VideoSession.SessionStatus.ACCEPTED) {
            throw new RuntimeException("Session must be accepted before starting");
        }

        videoSession.startSession();
        videoSession = videoSessionRepository.save(videoSession);

        // Notificar a ambos participantes
        notifyVideoSessionStarted(videoSession);

        return new VideoSessionDTO(videoSession);
    }

    // Terminar sesión
    public VideoSessionDTO endVideoSession(Long sessionId, Long userId) {
        VideoSession videoSession = videoSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Video session not found"));

        if (!videoSession.involvesUser(userRepository.findById(userId).orElse(null))) {
            throw new RuntimeException("User is not part of this video session");
        }

        if (!videoSession.isActive()) {
            throw new RuntimeException("Session is not currently active");
        }

        videoSession.endSession();
        videoSession = videoSessionRepository.save(videoSession);

        // Notificar finalización
        notifyVideoSessionEnded(videoSession);

        return new VideoSessionDTO(videoSession);
    }

    // Cancelar sesión
    public VideoSessionDTO cancelVideoSession(Long sessionId, Long userId) {
        VideoSession videoSession = videoSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Video session not found"));

        if (!videoSession.involvesUser(userRepository.findById(userId).orElse(null))) {
            throw new RuntimeException("User is not part of this video session");
        }

        if (videoSession.getStatus() == VideoSession.SessionStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed session");
        }

        videoSession.cancelSession();
        videoSession = videoSessionRepository.save(videoSession);

        // Notificar cancelación
        User otherUser = videoSession.getOtherParticipant(userRepository.findById(userId).orElse(null));
        if (otherUser != null) {
            notifyVideoSessionCancelled(otherUser, videoSession);
        }

        return new VideoSessionDTO(videoSession);
    }

    // Calificar sesión
    public VideoSessionDTO rateVideoSession(Long sessionId, Long userId, int rating, String feedback) {
        VideoSession videoSession = videoSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Video session not found"));

        if (!videoSession.canBeRated()) {
            throw new RuntimeException("Session cannot be rated in current status");
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        if (videoSession.getInitiator().getId().equals(userId)) {
            videoSession.setInitiatorRating(rating);
            videoSession.setInitiatorFeedback(feedback);
        } else if (videoSession.getParticipant().getId().equals(userId)) {
            videoSession.setParticipantRating(rating);
            videoSession.setParticipantFeedback(feedback);
        } else {
            throw new RuntimeException("User is not part of this video session");
        }

        videoSession = videoSessionRepository.save(videoSession);
        return new VideoSessionDTO(videoSession);
    }

    // Obtener sesiones del usuario
    public Page<VideoSessionDTO> getUserVideoSessions(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Page<VideoSession> sessions = videoSessionRepository.findByUser(user, pageable);
        return sessions.map(VideoSessionDTO::new);
    }

    // Obtener sesiones pendientes
    public List<VideoSessionDTO> getPendingVideoSessions(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<VideoSession> sessions = videoSessionRepository.findPendingSessionsForUser(user);
        return sessions.stream().map(VideoSessionDTO::new).collect(Collectors.toList());
    }

    // Obtener próximas sesiones (próximas 24 horas)
    public List<VideoSessionDTO> getUpcomingVideoSessions(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusDays(1);

        List<VideoSession> sessions = videoSessionRepository.findUpcomingSessionsNext24Hours(user, now, next24Hours);
        return sessions.stream().map(VideoSessionDTO::new).collect(Collectors.toList());
    }

    // Obtener sesiones por skill match
    public List<VideoSessionDTO> getVideoSessionsBySkillMatch(Long skillMatchId) {
        SkillMatch skillMatch = skillMatchRepository.findById(skillMatchId)
            .orElseThrow(() -> new RuntimeException("SkillMatch not found"));

        List<VideoSession> sessions = videoSessionRepository.findBySkillMatchOrderByCreatedAtDesc(skillMatch);
        return sessions.stream().map(VideoSessionDTO::new).collect(Collectors.toList());
    }

    // Obtener sesión por sessionId
    public Optional<VideoSessionDTO> getVideoSessionBySessionId(String sessionId) {
        return videoSessionRepository.findBySessionId(sessionId)
            .map(VideoSessionDTO::new);
    }

    // Estadísticas del usuario
    public VideoSessionStats getUserVideoSessionStats(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Long completedSessions = videoSessionRepository.countCompletedSessionsByUser(user);
        Double averageDuration = videoSessionRepository.getAverageSessionDurationByUser(user);
        Long totalDuration = videoSessionRepository.getTotalSessionDurationByUser(user);
        Double avgRatingAsInitiator = videoSessionRepository.getAverageRatingAsInitiator(user);
        Double avgRatingAsParticipant = videoSessionRepository.getAverageRatingAsParticipant(user);

        return new VideoSessionStats(completedSessions, averageDuration, totalDuration, 
                                   avgRatingAsInitiator, avgRatingAsParticipant);
    }

    // Métodos de notificación WebSocket
    private void notifyVideoSessionInvitation(User participant, VideoSession videoSession) {
        messagingTemplate.convertAndSendToUser(
            participant.getEmail(),
            "/queue/video-session-invitation",
            new VideoSessionDTO(videoSession)
        );
    }

    private void notifyVideoSessionAccepted(User initiator, VideoSession videoSession) {
        messagingTemplate.convertAndSendToUser(
            initiator.getEmail(),
            "/queue/video-session-accepted",
            new VideoSessionDTO(videoSession)
        );
    }

    private void notifyVideoSessionRejected(User initiator, VideoSession videoSession) {
        messagingTemplate.convertAndSendToUser(
            initiator.getEmail(),
            "/queue/video-session-rejected",
            new VideoSessionDTO(videoSession)
        );
    }

    private void notifyVideoSessionStarted(VideoSession videoSession) {
        VideoSessionDTO dto = new VideoSessionDTO(videoSession);
        
        messagingTemplate.convertAndSendToUser(
            videoSession.getInitiator().getEmail(),
            "/queue/video-session-started",
            dto
        );
        
        messagingTemplate.convertAndSendToUser(
            videoSession.getParticipant().getEmail(),
            "/queue/video-session-started",
            dto
        );
    }

    private void notifyVideoSessionEnded(VideoSession videoSession) {
        VideoSessionDTO dto = new VideoSessionDTO(videoSession);
        
        messagingTemplate.convertAndSendToUser(
            videoSession.getInitiator().getEmail(),
            "/queue/video-session-ended",
            dto
        );
        
        messagingTemplate.convertAndSendToUser(
            videoSession.getParticipant().getEmail(),
            "/queue/video-session-ended",
            dto
        );
    }

    private void notifyVideoSessionCancelled(User user, VideoSession videoSession) {
        messagingTemplate.convertAndSendToUser(
            user.getEmail(),
            "/queue/video-session-cancelled",
            new VideoSessionDTO(videoSession)
        );
    }

    // Clase para estadísticas
    public static class VideoSessionStats {
        private Long completedSessions;
        private Double averageDurationMinutes;
        private Long totalDurationMinutes;
        private Double averageRatingAsInitiator;
        private Double averageRatingAsParticipant;

        public VideoSessionStats(Long completedSessions, Double averageDurationMinutes, 
                               Long totalDurationMinutes, Double averageRatingAsInitiator, 
                               Double averageRatingAsParticipant) {
            this.completedSessions = completedSessions;
            this.averageDurationMinutes = averageDurationMinutes;
            this.totalDurationMinutes = totalDurationMinutes;
            this.averageRatingAsInitiator = averageRatingAsInitiator;
            this.averageRatingAsParticipant = averageRatingAsParticipant;
        }

        // Getters
        public Long getCompletedSessions() { return completedSessions; }
        public Double getAverageDurationMinutes() { return averageDurationMinutes; }
        public Long getTotalDurationMinutes() { return totalDurationMinutes; }
        public Double getAverageRatingAsInitiator() { return averageRatingAsInitiator; }
        public Double getAverageRatingAsParticipant() { return averageRatingAsParticipant; }
    }
}
