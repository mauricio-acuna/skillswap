package com.skillswap.backend.repository;

import com.skillswap.backend.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Buscar conversación entre dos usuarios con paginación
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "((cm.sender.id = :userId1 AND cm.receiver.id = :userId2) OR " +
           "(cm.sender.id = :userId2 AND cm.receiver.id = :userId1)) " +
           "ORDER BY cm.sentAt DESC")
    Page<ChatMessage> findConversationBetweenUsers(@Param("userId1") Long userId1, 
                                                   @Param("userId2") Long userId2, 
                                                   Pageable pageable);

    /**
     * Buscar mensajes de un match específico
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "cm.skillMatch.id = :skillMatchId " +
           "ORDER BY cm.sentAt ASC")
    List<ChatMessage> findBySkillMatchId(@Param("skillMatchId") Long skillMatchId);

    /**
     * Buscar conversaciones activas de un usuario (últimos mensajes)
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "cm.id IN (" +
           "  SELECT MAX(cm2.id) FROM ChatMessage cm2 WHERE " +
           "  (cm2.sender.id = :userId OR cm2.receiver.id = :userId) " +
           "  GROUP BY " +
           "  CASE WHEN cm2.sender.id = :userId THEN cm2.receiver.id ELSE cm2.sender.id END" +
           ") " +
           "ORDER BY cm.sentAt DESC")
    List<ChatMessage> findActiveConversationsForUser(@Param("userId") Long userId);

    /**
     * Contar mensajes no leídos para un usuario
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE " +
           "cm.receiver.id = :userId AND cm.isRead = false")
    long countUnreadMessagesForUser(@Param("userId") Long userId);

    /**
     * Buscar mensajes no leídos entre dos usuarios
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "cm.sender.id = :senderId AND cm.receiver.id = :receiverId AND cm.isRead = false " +
           "ORDER BY cm.sentAt ASC")
    List<ChatMessage> findUnreadMessagesBetweenUsers(@Param("senderId") Long senderId, 
                                                     @Param("receiverId") Long receiverId);

    /**
     * Marcar mensajes como leídos
     */
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true, cm.readAt = :readAt WHERE " +
           "cm.sender.id = :senderId AND cm.receiver.id = :receiverId AND cm.isRead = false")
    int markMessagesAsRead(@Param("senderId") Long senderId, 
                          @Param("receiverId") Long receiverId, 
                          @Param("readAt") LocalDateTime readAt);

    /**
     * Buscar mensajes por tipo
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "(cm.sender.id = :userId OR cm.receiver.id = :userId) AND " +
           "cm.messageType = :messageType " +
           "ORDER BY cm.sentAt DESC")
    List<ChatMessage> findMessagesByType(@Param("userId") Long userId, 
                                        @Param("messageType") ChatMessage.MessageType messageType);

    /**
     * Buscar mensajes por rango de fechas
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "((cm.sender.id = :userId1 AND cm.receiver.id = :userId2) OR " +
           "(cm.sender.id = :userId2 AND cm.receiver.id = :userId1)) AND " +
           "cm.sentAt BETWEEN :startDate AND :endDate " +
           "ORDER BY cm.sentAt ASC")
    List<ChatMessage> findMessagesBetweenUsersInDateRange(@Param("userId1") Long userId1,
                                                         @Param("userId2") Long userId2,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Buscar mensajes que contienen texto específico
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "(cm.sender.id = :userId OR cm.receiver.id = :userId) AND " +
           "LOWER(cm.content) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY cm.sentAt DESC")
    List<ChatMessage> searchMessagesByContent(@Param("userId") Long userId, 
                                             @Param("searchText") String searchText);

    /**
     * Obtener estadísticas de mensajes para un usuario
     */
    @Query("SELECT " +
           "COUNT(cm) as totalMessages, " +
           "SUM(CASE WHEN cm.sender.id = :userId THEN 1 ELSE 0 END) as sentMessages, " +
           "SUM(CASE WHEN cm.receiver.id = :userId THEN 1 ELSE 0 END) as receivedMessages, " +
           "SUM(CASE WHEN cm.receiver.id = :userId AND cm.isRead = false THEN 1 ELSE 0 END) as unreadMessages " +
           "FROM ChatMessage cm WHERE " +
           "(cm.sender.id = :userId OR cm.receiver.id = :userId)")
    Object[] getMessageStatisticsForUser(@Param("userId") Long userId);

    /**
     * Eliminar mensajes antiguos (para limpieza automática)
     */
    @Modifying
    @Query("DELETE FROM ChatMessage cm WHERE cm.sentAt < :cutoffDate")
    int deleteOldMessages(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Buscar mensajes con archivos adjuntos
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "(cm.sender.id = :userId OR cm.receiver.id = :userId) AND " +
           "cm.attachmentUrl IS NOT NULL " +
           "ORDER BY cm.sentAt DESC")
    List<ChatMessage> findMessagesWithAttachments(@Param("userId") Long userId);

    /**
     * Obtener último mensaje entre dos usuarios
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "((cm.sender.id = :userId1 AND cm.receiver.id = :userId2) OR " +
           "(cm.sender.id = :userId2 AND cm.receiver.id = :userId1)) " +
           "ORDER BY cm.sentAt DESC")
    Page<ChatMessage> findLastMessageBetweenUsers(@Param("userId1") Long userId1, 
                                                  @Param("userId2") Long userId2, 
                                                  Pageable pageable);

    /**
     * Verificar si existe conversación entre dos usuarios
     */
    @Query("SELECT CASE WHEN COUNT(cm) > 0 THEN true ELSE false END FROM ChatMessage cm WHERE " +
           "((cm.sender.id = :userId1 AND cm.receiver.id = :userId2) OR " +
           "(cm.sender.id = :userId2 AND cm.receiver.id = :userId1))")
    boolean existsConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * Buscar mensajes recientes (últimas 24 horas)
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "(cm.sender.id = :userId OR cm.receiver.id = :userId) AND " +
           "cm.sentAt >= :since " +
           "ORDER BY cm.sentAt DESC")
    List<ChatMessage> findRecentMessages(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
