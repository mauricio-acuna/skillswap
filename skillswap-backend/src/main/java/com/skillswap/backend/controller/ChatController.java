package com.skillswap.backend.controller;

import com.skillswap.backend.dto.ChatMessageDTO;
import com.skillswap.backend.model.ChatMessage;
import com.skillswap.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@PreAuthorize("hasRole('USER')")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * Enviar mensaje
     */
    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        Long senderId = getUserIdFromAuth(authentication);
        Long receiverId = Long.valueOf(request.get("receiverId").toString());
        String content = request.get("content").toString();
        
        ChatMessage.MessageType messageType = ChatMessage.MessageType.TEXT;
        if (request.containsKey("messageType")) {
            messageType = ChatMessage.MessageType.valueOf(request.get("messageType").toString());
        }
        
        Long skillMatchId = null;
        if (request.containsKey("skillMatchId")) {
            skillMatchId = Long.valueOf(request.get("skillMatchId").toString());
        }
        
        ChatMessageDTO message = chatService.sendMessage(senderId, receiverId, content, messageType, skillMatchId);
        return ResponseEntity.ok(message);
    }

    /**
     * Obtener conversación entre dos usuarios
     */
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<ChatMessageDTO>> getConversation(
            @PathVariable Long otherUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        List<ChatMessageDTO> conversation = chatService.getConversation(userId, otherUserId, page, size);
        return ResponseEntity.ok(conversation);
    }

    /**
     * Obtener mensajes de un match específico
     */
    @GetMapping("/match/{skillMatchId}")
    public ResponseEntity<List<ChatMessageDTO>> getMatchMessages(
            @PathVariable Long skillMatchId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        List<ChatMessageDTO> messages = chatService.getMatchMessages(skillMatchId, userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Obtener conversaciones activas del usuario
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ChatMessageDTO>> getActiveConversations(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<ChatMessageDTO> conversations = chatService.getActiveConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Marcar mensajes como leídos
     */
    @PostMapping("/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @RequestBody Map<String, Long> request,
            Authentication authentication) {
        
        Long receiverId = getUserIdFromAuth(authentication);
        Long senderId = request.get("senderId");
        
        chatService.markMessagesAsRead(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtener conteo de mensajes no leídos
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        long unreadCount = chatService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    /**
     * Buscar mensajes por contenido
     */
    @GetMapping("/search")
    public ResponseEntity<List<ChatMessageDTO>> searchMessages(
            @RequestParam String query,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        List<ChatMessageDTO> messages = chatService.searchMessages(userId, query);
        return ResponseEntity.ok(messages);
    }

    /**
     * Obtener estadísticas de mensajes
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMessageStatistics(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Map<String, Object> stats = chatService.getMessageStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtener mensajes recientes (últimas 24 horas)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ChatMessageDTO>> getRecentMessages(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<ChatMessageDTO> messages = chatService.getRecentMessages(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Editar mensaje
     */
    @PutMapping("/{messageId}")
    public ResponseEntity<ChatMessageDTO> editMessage(
            @PathVariable Long messageId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        String newContent = request.get("content");
        
        ChatMessageDTO editedMessage = chatService.editMessage(messageId, userId, newContent);
        return ResponseEntity.ok(editedMessage);
    }

    /**
     * Eliminar mensaje
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        chatService.deleteMessage(messageId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Enviar mensaje del sistema (solo para administradores)
     */
    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChatMessageDTO> sendSystemMessage(
            @RequestBody Map<String, Object> request) {
        
        Long receiverId = Long.valueOf(request.get("receiverId").toString());
        String content = request.get("content").toString();
        Long skillMatchId = null;
        
        if (request.containsKey("skillMatchId")) {
            skillMatchId = Long.valueOf(request.get("skillMatchId").toString());
        }
        
        ChatMessageDTO message = chatService.sendSystemMessage(receiverId, content, skillMatchId);
        return ResponseEntity.ok(message);
    }

    /**
     * Obtener información de presencia de usuarios (online/offline)
     */
    @GetMapping("/presence/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPresence(@PathVariable Long userId) {
        // TODO: Implementar sistema de presencia real
        // Por ahora devolver datos mock
        Map<String, Object> presence = Map.of(
                "userId", userId,
                "status", "online", // online, offline, away
                "lastSeen", System.currentTimeMillis()
        );
        return ResponseEntity.ok(presence);
    }

    /**
     * Obtener métricas de chat para dashboard administrativo
     */
    @GetMapping("/admin/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getChatMetrics() {
        // TODO: Implementar métricas reales
        Map<String, Object> metrics = Map.of(
                "totalMessages", 0,
                "activeConversations", 0,
                "averageResponseTime", 0,
                "mostActiveUsers", List.of()
        );
        return ResponseEntity.ok(metrics);
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // Asumir que el principal contiene el ID del usuario
        return Long.valueOf(authentication.getName());
    }
}
