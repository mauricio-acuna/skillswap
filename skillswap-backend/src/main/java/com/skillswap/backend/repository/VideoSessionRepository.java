package com.skillswap.backend.repository;

import com.skillswap.backend.model.VideoSession;
import com.skillswap.backend.model.User;
import com.skillswap.backend.model.SkillMatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoSessionRepository extends JpaRepository<VideoSession, Long> {

    // Buscar por sessionId único
    Optional<VideoSession> findBySessionId(String sessionId);

    // Sesiones por usuario (como iniciador o participante)
    @Query("SELECT vs FROM VideoSession vs WHERE vs.initiator = :user OR vs.participant = :user")
    Page<VideoSession> findByUser(@Param("user") User user, Pageable pageable);

    // Sesiones por usuario y estado
    @Query("SELECT vs FROM VideoSession vs WHERE (vs.initiator = :user OR vs.participant = :user) " +
           "AND vs.status = :status")
    List<VideoSession> findByUserAndStatus(@Param("user") User user, 
                                          @Param("status") VideoSession.SessionStatus status);

    // Sesiones pendientes para un usuario
    @Query("SELECT vs FROM VideoSession vs WHERE vs.participant = :user AND vs.status = 'PENDING'")
    List<VideoSession> findPendingSessionsForUser(@Param("user") User user);

    // Sesiones activas para un usuario
    @Query("SELECT vs FROM VideoSession vs WHERE (vs.initiator = :user OR vs.participant = :user) " +
           "AND vs.status = 'IN_PROGRESS'")
    List<VideoSession> findActiveSessionsForUser(@Param("user") User user);

    // Sesiones completadas para un usuario
    @Query("SELECT vs FROM VideoSession vs WHERE (vs.initiator = :user OR vs.participant = :user) " +
           "AND vs.status = 'COMPLETED'")
    Page<VideoSession> findCompletedSessionsForUser(@Param("user") User user, Pageable pageable);

    // Sesiones por skill match
    List<VideoSession> findBySkillMatchOrderByCreatedAtDesc(SkillMatch skillMatch);

    // Sesiones programadas en un rango de tiempo
    @Query("SELECT vs FROM VideoSession vs WHERE vs.scheduledStartTime BETWEEN :startTime AND :endTime " +
           "AND vs.status IN ('PENDING', 'ACCEPTED')")
    List<VideoSession> findScheduledSessionsBetween(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    // Sesiones que necesitan calificación
    @Query("SELECT vs FROM VideoSession vs WHERE vs.status = 'COMPLETED' " +
           "AND ((vs.initiator = :user AND vs.initiatorRating IS NULL) " +
           "OR (vs.participant = :user AND vs.participantRating IS NULL))")
    List<VideoSession> findSessionsNeedingRatingByUser(@Param("user") User user);

    // Estadísticas de sesiones por usuario
    @Query("SELECT COUNT(vs) FROM VideoSession vs WHERE (vs.initiator = :user OR vs.participant = :user) " +
           "AND vs.status = 'COMPLETED'")
    Long countCompletedSessionsByUser(@Param("user") User user);

    @Query("SELECT AVG(vs.durationMinutes) FROM VideoSession vs WHERE (vs.initiator = :user OR vs.participant = :user) " +
           "AND vs.status = 'COMPLETED' AND vs.durationMinutes IS NOT NULL")
    Double getAverageSessionDurationByUser(@Param("user") User user);

    @Query("SELECT SUM(vs.durationMinutes) FROM VideoSession vs WHERE (vs.initiator = :user OR vs.participant = :user) " +
           "AND vs.status = 'COMPLETED' AND vs.durationMinutes IS NOT NULL")
    Long getTotalSessionDurationByUser(@Param("user") User user);

    // Calificación promedio como profesor/iniciador
    @Query("SELECT AVG(vs.initiatorRating) FROM VideoSession vs WHERE vs.initiator = :user " +
           "AND vs.initiatorRating IS NOT NULL")
    Double getAverageRatingAsInitiator(@Param("user") User user);

    // Calificación promedio como participante
    @Query("SELECT AVG(vs.participantRating) FROM VideoSession vs WHERE vs.participant = :user " +
           "AND vs.participantRating IS NOT NULL")
    Double getAverageRatingAsParticipant(@Param("user") User user);

    // Sesiones recientes entre dos usuarios
    @Query("SELECT vs FROM VideoSession vs WHERE " +
           "((vs.initiator = :user1 AND vs.participant = :user2) OR " +
           "(vs.initiator = :user2 AND vs.participant = :user1)) " +
           "ORDER BY vs.createdAt DESC")
    List<VideoSession> findSessionsBetweenUsers(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);

    // Sesiones por tipo
    @Query("SELECT vs FROM VideoSession vs WHERE (vs.initiator = :user OR vs.participant = :user) " +
           "AND vs.type = :type")
    List<VideoSession> findByUserAndType(@Param("user") User user, 
                                        @Param("type") VideoSession.SessionType type);

    // Sesiones grabadas
    @Query("SELECT vs FROM VideoSession vs WHERE vs.isRecorded = true " +
           "AND (vs.initiator = :user OR vs.participant = :user)")
    List<VideoSession> findRecordedSessionsByUser(@Param("user") User user);

    // Sesiones canceladas en el último mes
    @Query("SELECT COUNT(vs) FROM VideoSession vs WHERE (vs.initiator = :user OR vs.participant = :user) " +
           "AND vs.status = 'CANCELLED' AND vs.updatedAt >= :oneMonthAgo")
    Long countCancelledSessionsLastMonth(@Param("user") User user, @Param("oneMonthAgo") LocalDateTime oneMonthAgo);

    // Sesiones próximas (próximas 24 horas)
    @Query("SELECT vs FROM VideoSession vs WHERE (vs.initiator = :user OR vs.participant = :user) " +
           "AND vs.status IN ('PENDING', 'ACCEPTED') " +
           "AND vs.scheduledStartTime BETWEEN :now AND :next24Hours " +
           "ORDER BY vs.scheduledStartTime ASC")
    List<VideoSession> findUpcomingSessionsNext24Hours(@Param("user") User user, 
                                                       @Param("now") LocalDateTime now,
                                                       @Param("next24Hours") LocalDateTime next24Hours);

    // Buscar conflictos de horario para un usuario
    @Query("SELECT vs FROM VideoSession vs WHERE (vs.initiator = :user OR vs.participant = :user) " +
           "AND vs.status IN ('PENDING', 'ACCEPTED', 'IN_PROGRESS') " +
           "AND vs.scheduledStartTime < :endTime " +
           "AND (vs.scheduledStartTime + 2 HOUR) > :startTime")
    List<VideoSession> findConflictingSessions(@Param("user") User user,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
}
