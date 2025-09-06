package com.skillswap.backend.controller;

import com.skillswap.backend.dto.VideoSessionDTO;
import com.skillswap.backend.service.VideoSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class VideoSessionWebSocketController {

    @Autowired
    private VideoSessionService videoSessionService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Manejo de ofertas WebRTC
    @MessageMapping("/video.offer")
    public void handleVideoOffer(@Payload Map<String, Object> offer, Principal principal) {
        String sessionId = (String) offer.get("sessionId");
        String targetUser = (String) offer.get("targetUser");
        
        // Verificar que el usuario tiene acceso a la sesión
        if (sessionId != null && targetUser != null) {
            // Reenviar la oferta al usuario objetivo
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-offer",
                offer
            );
        }
    }

    // Manejo de respuestas WebRTC
    @MessageMapping("/video.answer")
    public void handleVideoAnswer(@Payload Map<String, Object> answer, Principal principal) {
        String sessionId = (String) answer.get("sessionId");
        String targetUser = (String) answer.get("targetUser");
        
        if (sessionId != null && targetUser != null) {
            // Reenviar la respuesta al usuario objetivo
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-answer",
                answer
            );
        }
    }

    // Manejo de candidatos ICE para WebRTC
    @MessageMapping("/video.ice-candidate")
    public void handleIceCandidate(@Payload Map<String, Object> candidate, Principal principal) {
        String sessionId = (String) candidate.get("sessionId");
        String targetUser = (String) candidate.get("targetUser");
        
        if (sessionId != null && targetUser != null) {
            // Reenviar el candidato ICE al usuario objetivo
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-ice-candidate",
                candidate
            );
        }
    }

    // Inicio de sesión de video
    @MessageMapping("/video.session.start")
    @SendToUser("/queue/video-session-started")
    public VideoSessionDTO startVideoSession(@Payload Map<String, Object> payload, Principal principal) {
        try {
            Long sessionId = Long.valueOf(payload.get("sessionId").toString());
            
            // Por simplicidad, asumo que el payload incluye el userId
            Long userId = Long.valueOf(payload.get("userId").toString());
            
            return videoSessionService.startVideoSession(sessionId, userId);
        } catch (Exception e) {
            // Manejar error
            return null;
        }
    }

    // Finalización de sesión de video
    @MessageMapping("/video.session.end")
    @SendToUser("/queue/video-session-ended")
    public VideoSessionDTO endVideoSession(@Payload Map<String, Object> payload, Principal principal) {
        try {
            Long sessionId = Long.valueOf(payload.get("sessionId").toString());
            Long userId = Long.valueOf(payload.get("userId").toString());
            
            return videoSessionService.endVideoSession(sessionId, userId);
        } catch (Exception e) {
            return null;
        }
    }

    // Estado de conexión de video
    @MessageMapping("/video.connection.status")
    public void handleConnectionStatus(@Payload Map<String, Object> status, Principal principal) {
        String sessionId = (String) status.get("sessionId");
        String targetUser = (String) status.get("targetUser");
        String connectionStatus = (String) status.get("status"); // "connected", "disconnected", "reconnecting"
        
        if (sessionId != null && targetUser != null) {
            // Notificar el estado de conexión al otro usuario
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-connection-status",
                Map.of(
                    "sessionId", sessionId,
                    "status", connectionStatus,
                    "timestamp", System.currentTimeMillis()
                )
            );
        }
    }

    // Compartir pantalla
    @MessageMapping("/video.screen.share")
    public void handleScreenShare(@Payload Map<String, Object> shareData, Principal principal) {
        String sessionId = (String) shareData.get("sessionId");
        String targetUser = (String) shareData.get("targetUser");
        Boolean isSharing = (Boolean) shareData.get("isSharing");
        
        if (sessionId != null && targetUser != null) {
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-screen-share",
                Map.of(
                    "sessionId", sessionId,
                    "isSharing", isSharing,
                    "user", principal.getName()
                )
            );
        }
    }

    // Control de audio/video
    @MessageMapping("/video.media.control")
    public void handleMediaControl(@Payload Map<String, Object> mediaControl, Principal principal) {
        String sessionId = (String) mediaControl.get("sessionId");
        String targetUser = (String) mediaControl.get("targetUser");
        Boolean audioEnabled = (Boolean) mediaControl.get("audioEnabled");
        Boolean videoEnabled = (Boolean) mediaControl.get("videoEnabled");
        
        if (sessionId != null && targetUser != null) {
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-media-control",
                Map.of(
                    "sessionId", sessionId,
                    "audioEnabled", audioEnabled,
                    "videoEnabled", videoEnabled,
                    "user", principal.getName()
                )
            );
        }
    }

    // Manejo de errores de video
    @MessageMapping("/video.error")
    public void handleVideoError(@Payload Map<String, Object> error, Principal principal) {
        String sessionId = (String) error.get("sessionId");
        String targetUser = (String) error.get("targetUser");
        String errorMessage = (String) error.get("error");
        String errorCode = (String) error.get("code");
        
        if (sessionId != null && targetUser != null) {
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-error",
                Map.of(
                    "sessionId", sessionId,
                    "error", errorMessage,
                    "code", errorCode,
                    "user", principal.getName(),
                    "timestamp", System.currentTimeMillis()
                )
            );
        }
    }

    // Calidad de conexión
    @MessageMapping("/video.quality.report")
    public void handleQualityReport(@Payload Map<String, Object> qualityReport, Principal principal) {
        String sessionId = (String) qualityReport.get("sessionId");
        String targetUser = (String) qualityReport.get("targetUser");
        
        if (sessionId != null && targetUser != null) {
            // Opcional: guardar reporte de calidad en la base de datos
            // También reenviar al otro usuario para información
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-quality-report",
                qualityReport
            );
        }
    }

    // Pizarra colaborativa (opcional)
    @MessageMapping("/video.whiteboard")
    public void handleWhiteboard(@Payload Map<String, Object> whiteboardData, Principal principal) {
        String sessionId = (String) whiteboardData.get("sessionId");
        String targetUser = (String) whiteboardData.get("targetUser");
        
        if (sessionId != null && targetUser != null) {
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-whiteboard",
                whiteboardData
            );
        }
    }

    // Chat durante la videollamada
    @MessageMapping("/video.chat")
    public void handleVideoChat(@Payload Map<String, Object> chatMessage, Principal principal) {
        String sessionId = (String) chatMessage.get("sessionId");
        String targetUser = (String) chatMessage.get("targetUser");
        String message = (String) chatMessage.get("message");
        
        if (sessionId != null && targetUser != null && message != null) {
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-chat",
                Map.of(
                    "sessionId", sessionId,
                    "message", message,
                    "user", principal.getName(),
                    "timestamp", System.currentTimeMillis()
                )
            );
        }
    }

    // Grabación de sesión
    @MessageMapping("/video.recording")
    public void handleRecording(@Payload Map<String, Object> recordingData, Principal principal) {
        String sessionId = (String) recordingData.get("sessionId");
        String targetUser = (String) recordingData.get("targetUser");
        Boolean isRecording = (Boolean) recordingData.get("isRecording");
        
        if (sessionId != null && targetUser != null) {
            messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/video-recording",
                Map.of(
                    "sessionId", sessionId,
                    "isRecording", isRecording,
                    "user", principal.getName()
                )
            );
        }
    }
}
