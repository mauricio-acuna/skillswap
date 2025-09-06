package com.skillswap.backend.controller;

import com.skillswap.backend.model.ChatMessage;
import com.skillswap.backend.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Manejar mensajes enviados por WebSocket
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Map<String, Object> messageData, Principal principal) {
        try {
            Long senderId = getUserIdFromPrincipal(principal);
            Long receiverId = Long.valueOf(messageData.get("receiverId").toString());
            String content = messageData.get("content").toString();
            
            ChatMessage.MessageType messageType = ChatMessage.MessageType.TEXT;
            if (messageData.containsKey("messageType")) {
                messageType = ChatMessage.MessageType.valueOf(messageData.get("messageType").toString());
            }
            
            Long skillMatchId = null;
            if (messageData.containsKey("skillMatchId")) {
                skillMatchId = Long.valueOf(messageData.get("skillMatchId").toString());
            }

            // Enviar mensaje (el servicio se encarga de las notificaciones WebSocket)
            chatService.sendMessage(senderId, receiverId, content, messageType, skillMatchId);
            
            logger.info("WebSocket message sent from {} to {}", senderId, receiverId);
            
        } catch (Exception e) {
            logger.error("Error processing WebSocket message", e);
        }
    }

    /**
     * Manejar indicadores de escritura (typing indicators)
     */
    @MessageMapping("/chat.typing")
    @SendTo("/topic/typing")
    public Map<String, Object> handleTyping(@Payload Map<String, Object> typingData, Principal principal) {
        try {
            Long senderId = getUserIdFromPrincipal(principal);
            Long receiverId = Long.valueOf(typingData.get("receiverId").toString());
            boolean isTyping = Boolean.parseBoolean(typingData.get("isTyping").toString());
            
            return Map.of(
                    "senderId", senderId,
                    "receiverId", receiverId,
                    "isTyping", isTyping,
                    "timestamp", System.currentTimeMillis()
            );
            
        } catch (Exception e) {
            logger.error("Error processing typing indicator", e);
            return Map.of("error", "Error processing typing indicator");
        }
    }

    /**
     * Manejar marcado de mensajes como leídos
     */
    @MessageMapping("/chat.read")
    public void markAsRead(@Payload Map<String, Object> readData, Principal principal) {
        try {
            Long receiverId = getUserIdFromPrincipal(principal);
            Long senderId = Long.valueOf(readData.get("senderId").toString());
            
            chatService.markMessagesAsRead(senderId, receiverId);
            
            logger.info("Messages marked as read by user {} from user {}", receiverId, senderId);
            
        } catch (Exception e) {
            logger.error("Error marking messages as read", e);
        }
    }

    /**
     * Manejar presencia de usuario (online/offline)
     */
    @MessageMapping("/user.status")
    @SendTo("/topic/user.status")
    public Map<String, Object> updateUserStatus(@Payload Map<String, Object> statusData, Principal principal) {
        try {
            Long userId = getUserIdFromPrincipal(principal);
            String status = statusData.get("status").toString(); // "online", "offline", "away"
            
            return Map.of(
                    "userId", userId,
                    "status", status,
                    "timestamp", System.currentTimeMillis()
            );
            
        } catch (Exception e) {
            logger.error("Error updating user status", e);
            return Map.of("error", "Error updating user status");
        }
    }

    /**
     * Manejar suscripción a conversaciones privadas
     */
    @SubscribeMapping("/user/{userId}/queue/messages")
    public void subscribeToPrivateMessages(@DestinationVariable Long userId, Principal principal) {
        try {
            Long authenticatedUserId = getUserIdFromPrincipal(principal);
            
            // Verificar que el usuario solo se pueda suscribir a sus propios mensajes
            if (!authenticatedUserId.equals(userId)) {
                logger.warn("User {} attempted to subscribe to messages for user {}", authenticatedUserId, userId);
                return;
            }
            
            logger.info("User {} subscribed to private messages", userId);
            
        } catch (Exception e) {
            logger.error("Error handling private message subscription", e);
        }
    }

    /**
     * Manejar conexión de usuario
     */
    @MessageMapping("/user.connect")
    @SendTo("/topic/user.connect")
    public Map<String, Object> userConnect(Principal principal) {
        try {
            Long userId = getUserIdFromPrincipal(principal);
            
            logger.info("User {} connected to WebSocket", userId);
            
            return Map.of(
                    "userId", userId,
                    "status", "connected",
                    "timestamp", System.currentTimeMillis()
            );
            
        } catch (Exception e) {
            logger.error("Error handling user connection", e);
            return Map.of("error", "Error handling user connection");
        }
    }

    /**
     * Manejar desconexión de usuario
     */
    @MessageMapping("/user.disconnect")
    @SendTo("/topic/user.disconnect")
    public Map<String, Object> userDisconnect(Principal principal) {
        try {
            Long userId = getUserIdFromPrincipal(principal);
            
            logger.info("User {} disconnected from WebSocket", userId);
            
            return Map.of(
                    "userId", userId,
                    "status", "disconnected",
                    "timestamp", System.currentTimeMillis()
            );
            
        } catch (Exception e) {
            logger.error("Error handling user disconnection", e);
            return Map.of("error", "Error handling user disconnection");
        }
    }

    /**
     * Manejar solicitudes de video chat
     */
    @MessageMapping("/video.call")
    public void handleVideoCall(@Payload Map<String, Object> callData, Principal principal) {
        try {
            Long callerId = getUserIdFromPrincipal(principal);
            Long receiverId = Long.valueOf(callData.get("receiverId").toString());
            String callType = callData.get("callType").toString(); // "audio", "video"
            String action = callData.get("action").toString(); // "initiate", "accept", "reject", "end"
            
            // Enviar notificación de llamada al receptor
            Map<String, Object> callNotification = Map.of(
                    "callerId", callerId,
                    "receiverId", receiverId,
                    "callType", callType,
                    "action", action,
                    "timestamp", System.currentTimeMillis(),
                    "callId", callData.getOrDefault("callId", java.util.UUID.randomUUID().toString())
            );
            
            // Notificar al receptor específico
            messagingTemplate.convertAndSendToUser(
                    receiverId.toString(),
                    "/queue/video-call",
                    callNotification
            );
            
            logger.info("Video call {} from {} to {}", action, callerId, receiverId);
            
        } catch (Exception e) {
            logger.error("Error handling video call", e);
        }
    }

    /**
     * Extraer ID de usuario del principal de autenticación
     */
    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        try {
            // Asumir que el nombre del principal es el ID del usuario
            return Long.valueOf(principal.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID in authentication: " + principal.getName());
        }
    }
}
