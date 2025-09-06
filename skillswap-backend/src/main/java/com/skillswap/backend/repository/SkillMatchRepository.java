package com.skillswap.backend.repository;

import com.skillswap.backend.model.SkillMatch;
import com.skillswap.backend.model.SkillMatch.MatchStatus;
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
public interface SkillMatchRepository extends JpaRepository<SkillMatch, Long> {

    /**
     * Busca un match activo entre dos usuarios específicos para un skill determinado
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "((sm.teacher.id = :userId1 AND sm.learner.id = :userId2) OR " +
           "(sm.teacher.id = :userId2 AND sm.learner.id = :userId1)) AND " +
           "sm.skill.id = :skillId AND sm.status = :status")
    Optional<SkillMatch> findActiveMatchBetweenUsers(@Param("userId1") Long userId1, 
                                                   @Param("userId2") Long userId2,
                                                   @Param("skillId") Long skillId,
                                                   @Param("status") MatchStatus status);

    /**
     * Busca todos los matches pendientes para un usuario específico
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "sm.learner.id = :userId AND sm.status = 'PENDING' " +
           "ORDER BY sm.requestedAt DESC")
    List<SkillMatch> findPendingMatchesForUser(@Param("userId") Long userId);

    /**
     * Busca todas las solicitudes enviadas por un usuario
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "sm.teacher.id = :userId AND sm.status = 'PENDING' " +
           "ORDER BY sm.requestedAt DESC")
    List<SkillMatch> findSentRequestsByUser(@Param("userId") Long userId);

    /**
     * Busca todos los matches confirmados/activos para un usuario
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "(sm.teacher.id = :userId OR sm.learner.id = :userId) AND " +
           "sm.status IN ('ACCEPTED', 'ACTIVE') " +
           "ORDER BY sm.matchedAt DESC")
    List<SkillMatch> findActiveMatchesForUser(@Param("userId") Long userId);

    /**
     * Busca matches por skill específico para un usuario
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "(sm.teacher.id = :userId OR sm.learner.id = :userId) AND " +
           "sm.skill.id = :skillId AND sm.status = :status " +
           "ORDER BY sm.requestedAt DESC")
    List<SkillMatch> findMatchesByUserAndSkill(@Param("userId") Long userId,
                                              @Param("skillId") Long skillId,
                                              @Param("status") MatchStatus status);

    /**
     * Busca matches con paginación por usuario
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "(sm.teacher.id = :userId OR sm.learner.id = :userId) " +
           "ORDER BY sm.requestedAt DESC")
    Page<SkillMatch> findMatchesByUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * Busca matches por estado específico con paginación
     */
    Page<SkillMatch> findByStatusOrderByRequestedAtDesc(MatchStatus status, Pageable pageable);

    /**
     * Cuenta matches por usuario y estado
     */
    @Query("SELECT COUNT(sm) FROM SkillMatch sm WHERE " +
           "(sm.teacher.id = :userId OR sm.learner.id = :userId) AND " +
           "sm.status = :status")
    long countMatchesByUserAndStatus(@Param("userId") Long userId, @Param("status") MatchStatus status);

    /**
     * Busca matches que requieren atención (pendientes por más de X tiempo)
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "sm.status = 'PENDING' AND sm.requestedAt < :dateTime " +
           "ORDER BY sm.requestedAt ASC")
    List<SkillMatch> findExpiredPendingMatches(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Busca los mejores matches para un usuario basado en compatibility score
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "(sm.teacher.id = :userId OR sm.learner.id = :userId) AND " +
           "sm.status = 'ACCEPTED' AND sm.compatibilityScore IS NOT NULL " +
           "ORDER BY sm.compatibilityScore DESC")
    List<SkillMatch> findBestMatchesForUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * Busca matches recientes para un usuario (últimos 7 días)
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "(sm.teacher.id = :userId OR sm.learner.id = :userId) AND " +
           "sm.requestedAt >= :dateTime " +
           "ORDER BY sm.requestedAt DESC")
    List<SkillMatch> findRecentMatchesForUser(@Param("userId") Long userId, 
                                            @Param("dateTime") LocalDateTime dateTime);

    /**
     * Busca matches por categoría de skill
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "sm.skill.category.id = :categoryId AND " +
           "sm.status = :status " +
           "ORDER BY sm.compatibilityScore DESC")
    List<SkillMatch> findMatchesBySkillCategory(@Param("categoryId") Long categoryId,
                                               @Param("status") MatchStatus status);

    /**
     * Verifica si existe un match entre dos usuarios para cualquier skill
     */
    @Query("SELECT CASE WHEN COUNT(sm) > 0 THEN true ELSE false END FROM SkillMatch sm WHERE " +
           "((sm.teacher.id = :userId1 AND sm.learner.id = :userId2) OR " +
           "(sm.teacher.id = :userId2 AND sm.learner.id = :userId1)) AND " +
           "sm.status IN ('PENDING', 'ACCEPTED', 'ACTIVE')")
    boolean existsActiveMatchBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * Encuentra el match más reciente para un usuario
     */
    @Query("SELECT sm FROM SkillMatch sm WHERE " +
           "(sm.teacher.id = :userId OR sm.learner.id = :userId) " +
           "ORDER BY sm.requestedAt DESC")
    Optional<SkillMatch> findMostRecentMatchForUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * Estadísticas de matches por usuario
     */
    @Query("SELECT COUNT(sm) as total, " +
           "SUM(CASE WHEN sm.status = 'ACCEPTED' THEN 1 ELSE 0 END) as accepted, " +
           "SUM(CASE WHEN sm.status = 'PENDING' THEN 1 ELSE 0 END) as pending, " +
           "SUM(CASE WHEN sm.status = 'REJECTED' THEN 1 ELSE 0 END) as rejected " +
           "FROM SkillMatch sm WHERE " +
           "(sm.teacher.id = :userId OR sm.learner.id = :userId)")
    Object[] getMatchStatisticsForUser(@Param("userId") Long userId);
}
