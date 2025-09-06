package com.skillswap.backend.controller;

import com.skillswap.backend.dto.VideoSessionDTO;
import com.skillswap.backend.model.VideoSession;
import com.skillswap.backend.service.VideoSessionService;
import com.skillswap.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/video-sessions")
@CrossOrigin(origins = "*")
public class VideoSessionController {

    @Autowired
    private VideoSessionService videoSessionService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Crear nueva sesión de video
    @PostMapping
    public ResponseEntity<?> createVideoSession(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateVideoSessionRequest request) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));

            VideoSessionDTO videoSession = videoSessionService.createVideoSession(
                request.getSkillMatchId(),
                userId, // initiator
                request.getParticipantId(),
                request.getScheduledStartTime(),
                request.getType()
            );

            return ResponseEntity.ok(videoSession);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Aceptar invitación de sesión
    @PutMapping("/{sessionId}/accept")
    public ResponseEntity<?> acceptVideoSession(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId) {
        try {
            String email = jwtUtil.getEmailFromToken(token.substring(7));
            Long userId = jwtUtil.getUserIdFromEmail(email);

            VideoSessionDTO videoSession = videoSessionService.acceptVideoSession(sessionId, userId);
            return ResponseEntity.ok(videoSession);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Rechazar invitación de sesión
    @PutMapping("/{sessionId}/reject")
    public ResponseEntity<?> rejectVideoSession(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId) {
        try {
            String email = jwtUtil.getEmailFromToken(token.substring(7));
            Long userId = jwtUtil.getUserIdFromEmail(email);

            VideoSessionDTO videoSession = videoSessionService.rejectVideoSession(sessionId, userId);
            return ResponseEntity.ok(videoSession);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Iniciar sesión
    @PutMapping("/{sessionId}/start")
    public ResponseEntity<?> startVideoSession(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId) {
        try {
            String email = jwtUtil.getEmailFromToken(token.substring(7));
            Long userId = jwtUtil.getUserIdFromEmail(email);

            VideoSessionDTO videoSession = videoSessionService.startVideoSession(sessionId, userId);
            return ResponseEntity.ok(videoSession);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Terminar sesión
    @PutMapping("/{sessionId}/end")
    public ResponseEntity<?> endVideoSession(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId) {
        try {
            String email = jwtUtil.getEmailFromToken(token.substring(7));
            Long userId = jwtUtil.getUserIdFromEmail(email);

            VideoSessionDTO videoSession = videoSessionService.endVideoSession(sessionId, userId);
            return ResponseEntity.ok(videoSession);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Cancelar sesión
    @PutMapping("/{sessionId}/cancel")
    public ResponseEntity<?> cancelVideoSession(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId) {
        try {
            String email = jwtUtil.getEmailFromToken(token.substring(7));
            Long userId = jwtUtil.getUserIdFromEmail(email);

            VideoSessionDTO videoSession = videoSessionService.cancelVideoSession(sessionId, userId);
            return ResponseEntity.ok(videoSession);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Calificar sesión
    @PutMapping("/{sessionId}/rate")
    public ResponseEntity<?> rateVideoSession(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId,
            @RequestBody RateSessionRequest request) {
        try {
            String email = jwtUtil.getEmailFromToken(token.substring(7));
            Long userId = jwtUtil.getUserIdFromEmail(email);

            VideoSessionDTO videoSession = videoSessionService.rateVideoSession(
                sessionId, userId, request.getRating(), request.getFeedback());
            return ResponseEntity.ok(videoSession);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener sesiones del usuario
    @GetMapping("/my-sessions")
    public ResponseEntity<?> getMyVideoSessions(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            String email = jwtUtil.getEmailFromToken(token.substring(7));
            Long userId = jwtUtil.getUserIdFromEmail(email);

            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<VideoSessionDTO> sessions = videoSessionService.getUserVideoSessions(userId, pageable);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener sesiones pendientes
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingVideoSessions(
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.substring(7));
            Long userId = jwtUtil.getUserIdFromEmail(email);

            List<VideoSessionDTO> sessions = videoSessionService.getPendingVideoSessions(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener próximas sesiones
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingVideoSessions(
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.substring(7));
            Long userId = jwtUtil.getUserIdFromEmail(email);

            List<VideoSessionDTO> sessions = videoSessionService.getUpcomingVideoSessions(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener sesiones por skill match
    @GetMapping("/skill-match/{skillMatchId}")
    public ResponseEntity<?> getVideoSessionsBySkillMatch(
            @RequestHeader("Authorization") String token,
            @PathVariable Long skillMatchId) {
        try {
            List<VideoSessionDTO> sessions = videoSessionService.getVideoSessionsBySkillMatch(skillMatchId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener sesión por sessionId
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getVideoSessionBySessionId(
            @RequestHeader("Authorization") String token,
            @PathVariable String sessionId) {
        try {
            return videoSessionService.getVideoSessionBySessionId(sessionId)
                .map(session -> ResponseEntity.ok().body(session))
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener estadísticas del usuario
    @GetMapping("/stats")
    public ResponseEntity<?> getUserVideoSessionStats(
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.substring(7));
            Long userId = jwtUtil.getUserIdFromEmail(email);

            VideoSessionService.VideoSessionStats stats = videoSessionService.getUserVideoSessionStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Clases internas para requests
    public static class CreateVideoSessionRequest {
        private Long skillMatchId;
        private Long participantId;
        private LocalDateTime scheduledStartTime;
        private VideoSession.SessionType type;

        // Constructors
        public CreateVideoSessionRequest() {}

        // Getters y Setters
        public Long getSkillMatchId() {
            return skillMatchId;
        }

        public void setSkillMatchId(Long skillMatchId) {
            this.skillMatchId = skillMatchId;
        }

        public Long getParticipantId() {
            return participantId;
        }

        public void setParticipantId(Long participantId) {
            this.participantId = participantId;
        }

        public LocalDateTime getScheduledStartTime() {
            return scheduledStartTime;
        }

        public void setScheduledStartTime(LocalDateTime scheduledStartTime) {
            this.scheduledStartTime = scheduledStartTime;
        }

        public VideoSession.SessionType getType() {
            return type;
        }

        public void setType(VideoSession.SessionType type) {
            this.type = type;
        }
    }

    public static class RateSessionRequest {
        private int rating;
        private String feedback;

        // Constructors
        public RateSessionRequest() {}

        // Getters y Setters
        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }
    }
}
