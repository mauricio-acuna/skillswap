package com.skillswap.backend.service;

import com.skillswap.backend.dto.ChatMessageDTO;
import com.skillswap.backend.model.ChatMessage;
import com.skillswap.backend.model.SkillMatch;
import com.skillswap.backend.model.User;
import com.skillswap.backend.repository.ChatMessageRepository;
import com.skillswap.backend.repository.SkillMatchRepository;
import com.skillswap.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillMatchRepository skillMatchRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Enviar mensaje y notificar por WebSocket
     */
    public ChatMessageDTO sendMessage(Long senderId, Long receiverId, String content, 
                                     ChatMessage.MessageType messageType, Long skillMatchId) {
        logger.info("Sending message from user {} to user {}", senderId, receiverId);

        // Validar usuarios
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found: " + receiverId));

        // Validar skill match si se proporciona
        SkillMatch skillMatch = null;
        if (skillMatchId != null) {
            skillMatch = skillMatchRepository.findById(skillMatchId)
                    .orElseThrow(() -> new RuntimeException("Skill match not found: " + skillMatchId));
            
            // Verificar que el sender es parte del match
            if (!skillMatch.getLearnerUser().getId().equals(senderId) && 
                !skillMatch.getTeacherUser().getId().equals(senderId)) {
                throw new RuntimeException("User not authorized to send messages in this match");
            }
        }

        // Crear y guardar mensaje
        ChatMessage message = new ChatMessage(sender, receiver, content, messageType);
        if (skillMatch != null) {
            message.setSkillMatch(skillMatch);
        }
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        ChatMessageDTO messageDTO = new ChatMessageDTO(savedMessage);

        // Notificar por WebSocket
        notifyMessageReceived(messageDTO);

        logger.info("Message sent successfully with ID: {}", savedMessage.getId());
        return messageDTO;
    }

    /**
     * Obtener conversación entre dos usuarios con paginación
     */
    public List<ChatMessageDTO> getConversation(Long userId1, Long userId2, int page, int size) {
        logger.info("Getting conversation between users {} and {}", userId1, userId2);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = chatMessageRepository.findConversationBetweenUsers(userId1, userId2, pageable);
        
        return messages.getContent().stream()
                .map(ChatMessageDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtener mensajes de un match específico
     */
    public List<ChatMessageDTO> getMatchMessages(Long skillMatchId, Long userId) {
        // Verificar que el usuario es parte del match
        SkillMatch match = skillMatchRepository.findById(skillMatchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + skillMatchId));
        
        if (!match.getLearnerUser().getId().equals(userId) && 
            !match.getTeacherUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to view messages in this match");
        }

        List<ChatMessage> messages = chatMessageRepository.findBySkillMatchId(skillMatchId);
        return messages.stream()
                .map(ChatMessageDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtener conversaciones activas del usuario
     */
    public List<ChatMessageDTO> getActiveConversations(Long userId) {
        logger.info("Getting active conversations for user {}", userId);
        
        List<ChatMessage> lastMessages = chatMessageRepository.findActiveConversationsForUser(userId);
        return lastMessages.stream()
                .map(ChatMessageDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Marcar mensajes como leídos
     */
    public void markMessagesAsRead(Long senderId, Long receiverId) {
        logger.info("Marking messages as read from {} to {}", senderId, receiverId);
        
        int updatedCount = chatMessageRepository.markMessagesAsRead(senderId, receiverId, LocalDateTime.now());
        
        if (updatedCount > 0) {
            // Notificar que los mensajes han sido leídos
            notifyMessagesRead(senderId, receiverId);
        }
        
        logger.info("{} messages marked as read", updatedCount);
    }

    /**
     * Obtener conteo de mensajes no leídos
     */
    public long getUnreadMessageCount(Long userId) {
        return chatMessageRepository.countUnreadMessagesForUser(userId);
    }

    /**
     * Buscar mensajes por contenido
     */
    public List<ChatMessageDTO> searchMessages(Long userId, String searchText) {
        logger.info("Searching messages for user {} with text: {}", userId, searchText);
        
        List<ChatMessage> messages = chatMessageRepository.searchMessagesByContent(userId, searchText);
        return messages.stream()
                .map(ChatMessageDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtener estadísticas de mensajes
     */
    public Map<String, Object> getMessageStatistics(Long userId) {
        Object[] stats = chatMessageRepository.getMessageStatisticsForUser(userId);
        
        return Map.of(
                "totalMessages", stats[0],
                "sentMessages", stats[1],
                "receivedMessages", stats[2],
                "unreadMessages", stats[3]
        );
    }

    /**
     * Obtener mensajes recientes (últimas 24 horas)
     */
    public List<ChatMessageDTO> getRecentMessages(Long userId) {
        LocalDateTime since = LocalDateTime.now().minusDays(1);
        List<ChatMessage> messages = chatMessageRepository.findRecentMessages(userId, since);
        
        return messages.stream()
                .map(ChatMessageDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Editar mensaje
     */
    public ChatMessageDTO editMessage(Long messageId, Long userId, String newContent) {
        logger.info("Editing message {} by user {}", messageId, userId);
        
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));
        
        // Verificar que el usuario es el remitente
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("Only the sender can edit the message");
        }
        
        // No permitir editar mensajes muy antiguos (por ejemplo, más de 24 horas)
        if (message.getSentAt().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new RuntimeException("Cannot edit messages older than 24 hours");
        }
        
        message.setContent(newContent);
        message.markAsEdited();
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        ChatMessageDTO messageDTO = new ChatMessageDTO(savedMessage);
        
        // Notificar edición por WebSocket
        notifyMessageEdited(messageDTO);
        
        return messageDTO;
    }

    /**
     * Eliminar mensaje
     */
    public void deleteMessage(Long messageId, Long userId) {
        logger.info("Deleting message {} by user {}", messageId, userId);
        
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));
        
        // Verificar que el usuario es el remitente
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("Only the sender can delete the message");
        }
        
        chatMessageRepository.delete(message);
        
        // Notificar eliminación por WebSocket
        notifyMessageDeleted(messageId, message.getReceiver().getId());
    }

    /**
     * Enviar mensaje del sistema (para notificaciones automáticas)
     */
    public ChatMessageDTO sendSystemMessage(Long receiverId, String content, Long skillMatchId) {
        logger.info("Sending system message to user {}", receiverId);
        
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found: " + receiverId));
        
        ChatMessage message = new ChatMessage();
        message.setReceiver(receiver);
        message.setContent(content);
        message.setMessageType(ChatMessage.MessageType.SYSTEM);
        message.setSentAt(LocalDateTime.now());
        
        if (skillMatchId != null) {
            SkillMatch skillMatch = skillMatchRepository.findById(skillMatchId)
                    .orElseThrow(() -> new RuntimeException("Skill match not found: " + skillMatchId));
            message.setSkillMatch(skillMatch);
        }
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        ChatMessageDTO messageDTO = new ChatMessageDTO(savedMessage);
        
        // Notificar por WebSocket
        notifyMessageReceived(messageDTO);
        
        return messageDTO;
    }

    // Métodos privados para notificaciones WebSocket

    private void notifyMessageReceived(ChatMessageDTO messageDTO) {
        try {
            // Enviar al receptor específico
            messagingTemplate.convertAndSendToUser(
                    messageDTO.getReceiverId().toString(),
                    "/queue/messages",
                    messageDTO
            );
            
            // También enviar confirmación al remitente
            messagingTemplate.convertAndSendToUser(
                    messageDTO.getSenderId().toString(),
                    "/queue/message-sent",
                    messageDTO
            );
        } catch (Exception e) {
            logger.error("Error sending WebSocket notification", e);
        }
    }

    private void notifyMessagesRead(Long senderId, Long receiverId) {
        try {
            Map<String, Object> readNotification = Map.of(
                    "type", "messages_read",
                    "senderId", senderId,
                    "receiverId", receiverId,
                    "timestamp", LocalDateTime.now()
            );
            
            messagingTemplate.convertAndSendToUser(
                    senderId.toString(),
                    "/queue/read-receipts",
                    readNotification
            );
        } catch (Exception e) {
            logger.error("Error sending read receipt notification", e);
        }
    }

    private void notifyMessageEdited(ChatMessageDTO messageDTO) {
        try {
            messagingTemplate.convertAndSendToUser(
                    messageDTO.getReceiverId().toString(),
                    "/queue/message-edited",
                    messageDTO
            );
        } catch (Exception e) {
            logger.error("Error sending message edited notification", e);
        }
    }

    private void notifyMessageDeleted(Long messageId, Long receiverId) {
        try {
            Map<String, Object> deleteNotification = Map.of(
                    "type", "message_deleted",
                    "messageId", messageId,
                    "timestamp", LocalDateTime.now()
            );
            
            messagingTemplate.convertAndSendToUser(
                    receiverId.toString(),
                    "/queue/message-deleted",
                    deleteNotification
            );
        } catch (Exception e) {
            logger.error("Error sending message deleted notification", e);
        }
    }
}
