package com.skillswap.backend.service;

import com.skillswap.backend.model.User;
import com.skillswap.backend.model.UserSkill;
import com.skillswap.backend.model.SkillMatch;
import com.skillswap.backend.dto.MatchCandidate;
import com.skillswap.backend.repository.UserRepository;
import com.skillswap.backend.repository.SkillMatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchingService {

    private static final Logger logger = LoggerFactory.getLogger(MatchingService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillMatchRepository skillMatchRepository;

    /**
     * Buscar candidatos potenciales para intercambio de skills
     */
    public List<MatchCandidate> findMatchCandidates(Long userId, Long skillId, int limit) {
        logger.info("Finding match candidates for user ID: {} and skill ID: {}", userId, skillId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + userId);
        }

        User user = userOpt.get();
        
        // Obtener usuarios activos excepto el actual
        List<User> potentialMatches = userRepository.findActiveUsers().stream()
                .filter(u -> !u.getId().equals(userId))
                .limit(limit * 2) // Obtener más para filtrar
                .collect(Collectors.toList());

        // Convertir a candidatos y calcular compatibilidad
        List<MatchCandidate> candidates = potentialMatches.stream()
                .map(match -> createMatchCandidate(user, match))
                .filter(Objects::nonNull)
                .sorted((a, b) -> b.getCompatibilityScore().compareTo(a.getCompatibilityScore()))
                .limit(limit)
                .collect(Collectors.toList());

        logger.info("Found {} match candidates for user {}", candidates.size(), userId);
        return candidates;
    }

    /**
     * Enviar solicitud de match
     */
    public SkillMatch sendMatchRequest(Long requesterId, Long targetUserId, Long skillId, String message) {
        logger.info("Creating match request from {} to {} for skill {}", requesterId, targetUserId, skillId);

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found: " + requesterId));
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found: " + targetUserId));

        // Verificar que no existe ya un match activo
        Optional<SkillMatch> existingMatch = skillMatchRepository.findActiveMatchBetweenUsers(
            requesterId, targetUserId, skillId, SkillMatch.MatchStatus.PENDING);
        if (existingMatch.isPresent()) {
            throw new RuntimeException("Match request already exists between these users");
        }

        // Verificar match aceptado
        Optional<SkillMatch> acceptedMatch = skillMatchRepository.findActiveMatchBetweenUsers(
            requesterId, targetUserId, skillId, SkillMatch.MatchStatus.ACCEPTED);
        if (acceptedMatch.isPresent()) {
            throw new RuntimeException("Active match already exists between these users");
        }

        // Crear nueva solicitud de match
        SkillMatch match = new SkillMatch();
        match.setLearnerUser(requester);
        match.setTeacherUser(target);
        match.setStatus(SkillMatch.MatchStatus.PENDING);
        match.setMessage(message);
        match.setRequestedAt(LocalDateTime.now());
        
        // Calcular compatibilidad
        double compatibilityScore = calculateCompatibilityScore(requester, target);
        match.setCompatibilityScore(compatibilityScore);

        return skillMatchRepository.save(match);
    }

    /**
     * Aceptar solicitud de match
     */
    public SkillMatch acceptMatch(Long matchId, Long userId) {
        logger.info("User {} accepting match {}", userId, matchId);

        SkillMatch match = skillMatchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        // Verificar que el usuario puede aceptar este match
        if (!match.getTeacherUser().getId().equals(userId)) {
            throw new RuntimeException("Only the teacher can accept this match");
        }

        if (match.getStatus() != SkillMatch.MatchStatus.PENDING) {
            throw new RuntimeException("Only pending matches can be accepted");
        }

        match.setStatus(SkillMatch.MatchStatus.ACCEPTED);
        match.setAcceptedAt(LocalDateTime.now());
        match.setMatchedAt(LocalDateTime.now());

        return skillMatchRepository.save(match);
    }

    /**
     * Rechazar solicitud de match
     */
    public void rejectMatch(Long matchId, Long userId, String reason) {
        logger.info("User {} rejecting match {} with reason: {}", userId, matchId, reason);

        SkillMatch match = skillMatchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        // Verificar que el usuario puede rechazar este match
        if (!match.getTeacherUser().getId().equals(userId)) {
            throw new RuntimeException("Only the teacher can reject this match");
        }

        if (match.getStatus() != SkillMatch.MatchStatus.PENDING) {
            throw new RuntimeException("Only pending matches can be rejected");
        }

        match.setStatus(SkillMatch.MatchStatus.REJECTED);
        skillMatchRepository.save(match);
    }

    /**
     * Obtener solicitudes pendientes recibidas
     */
    public List<SkillMatch> getPendingMatchesForUser(Long userId) {
        return skillMatchRepository.findPendingMatchesForUser(userId);
    }

    /**
     * Obtener solicitudes enviadas
     */
    public List<SkillMatch> getSentRequestsByUser(Long userId) {
        return skillMatchRepository.findSentRequestsByUser(userId);
    }

    /**
     * Obtener matches activos/confirmados
     */
    public List<SkillMatch> getActiveMatchesForUser(Long userId) {
        return skillMatchRepository.findActiveMatchesForUser(userId);
    }

    /**
     * Obtener detalles de un match específico
     */
    public SkillMatch getMatchDetails(Long matchId, Long userId) {
        SkillMatch match = skillMatchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
        
        // Verificar que el usuario es parte del match
        if (!match.getLearnerUser().getId().equals(userId) && 
            !match.getTeacherUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to view this match");
        }
        
        return match;
    }

    /**
     * Cancelar un match activo
     */
    public void cancelMatch(Long matchId, Long userId, String reason) {
        SkillMatch match = getMatchDetails(matchId, userId);
        
        if (match.getStatus() != SkillMatch.MatchStatus.ACCEPTED && 
            match.getStatus() != SkillMatch.MatchStatus.ACTIVE) {
            throw new RuntimeException("Only active matches can be cancelled");
        }
        
        match.setStatus(SkillMatch.MatchStatus.CANCELLED);
        match.setCancelledAt(LocalDateTime.now());
        skillMatchRepository.save(match);
        
        logger.info("Match {} cancelled by user {} with reason: {}", matchId, userId, reason);
    }

    /**
     * Marcar match como completado
     */
    public SkillMatch completeMatch(Long matchId, Long userId, Integer rating, String feedback) {
        SkillMatch match = getMatchDetails(matchId, userId);
        
        if (match.getStatus() != SkillMatch.MatchStatus.ACTIVE) {
            throw new RuntimeException("Only active matches can be completed");
        }
        
        match.setStatus(SkillMatch.MatchStatus.COMPLETED);
        match.setCompletedAt(LocalDateTime.now());
        
        // TODO: Guardar rating y feedback en tabla separada de reviews
        logger.info("Match {} completed by user {} with rating: {} and feedback: {}", 
                   matchId, userId, rating, feedback);
        
        return skillMatchRepository.save(match);
    }

    /**
     * Obtener matches por usuario y skill
     */
    public List<SkillMatch> getMatchesByUserAndSkill(Long userId, Long skillId, SkillMatch.MatchStatus status) {
        return skillMatchRepository.findMatchesByUserAndSkill(userId, skillId, status);
    }

    /**
     * Obtener estadísticas de matching del usuario
     */
    public Map<String, Object> getMatchingStatsForUser(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<SkillMatch> allMatches = skillMatchRepository.findActiveMatchesForUser(userId);
        stats.put("totalMatches", allMatches.size());
        stats.put("activeMatches", skillMatchRepository.countMatchesByUserAndStatus(userId, SkillMatch.MatchStatus.ACTIVE));
        stats.put("pendingMatches", skillMatchRepository.countMatchesByUserAndStatus(userId, SkillMatch.MatchStatus.PENDING));
        stats.put("completedMatches", skillMatchRepository.countMatchesByUserAndStatus(userId, SkillMatch.MatchStatus.COMPLETED));
        
        return stats;
    }

    /**
     * Obtener matches recomendados
     */
    public List<MatchCandidate> getRecommendedMatches(Long userId, int limit) {
        return findMatchCandidates(userId, null, limit);
    }

    /**
     * Reportar un match
     */
    public void reportMatch(Long matchId, Long userId, String reason, String description) {
        // Verificar que el usuario tiene acceso al match
        getMatchDetails(matchId, userId);
        
        // TODO: Guardar reporte en tabla de reportes
        logger.warn("Match {} reported by user {} for reason: {} - Description: {}", 
                   matchId, userId, reason, description);
    }

    /**
     * Obtener historial de matches
     */
    public List<SkillMatch> getMatchHistory(Long userId, int page, int size) {
        // TODO: Implementar paginación completa
        List<SkillMatch> allMatches = skillMatchRepository.findActiveMatchesForUser(userId);
        
        int start = page * size;
        int end = Math.min(start + size, allMatches.size());
        
        if (start >= allMatches.size()) {
            return new ArrayList<>();
        }
        
        return allMatches.subList(start, end);
    }

    /**
     * Configurar matching automático
     */
    public void setAutoMatching(Long userId, boolean enabled) {
        // TODO: Guardar preferencia en tabla de configuración de usuario
        logger.info("Auto matching {} for user {}", enabled ? "enabled" : "disabled", userId);
    }

    /**
     * Obtener preferencias de matching
     */
    public Map<String, Object> getMatchingPreferences(Long userId) {
        Map<String, Object> preferences = new HashMap<>();
        
        // TODO: Obtener desde tabla de configuración de usuario
        preferences.put("autoMatching", false);
        preferences.put("maxDistance", 50);
        preferences.put("preferredLanguages", new ArrayList<>());
        preferences.put("skillCategories", new ArrayList<>());
        
        return preferences;
    }

    /**
     * Actualizar preferencias de matching
     */
    public void updateMatchingPreferences(Long userId, Map<String, Object> preferences) {
        // TODO: Guardar en tabla de configuración de usuario
        logger.info("Updating matching preferences for user {}: {}", userId, preferences);
    }

    // Métodos privados de ayuda

    /**
     * Crear candidato de match a partir de dos usuarios
     */
    private MatchCandidate createMatchCandidate(User user, User potentialMatch) {
        // Obtener skills del usuario actual
        Set<UserSkill> userSkillsToLearn = user.getUserSkills().stream()
                .filter(UserSkill::wantsToLearn)
                .collect(Collectors.toSet());

        Set<UserSkill> userSkillsToTeach = user.getUserSkills().stream()
                .filter(UserSkill::canTeach)
                .collect(Collectors.toSet());

        // Obtener skills del match potencial
        Set<UserSkill> matchSkillsToTeach = potentialMatch.getUserSkills().stream()
                .filter(UserSkill::canTeach)
                .collect(Collectors.toSet());

        Set<UserSkill> matchSkillsToLearn = potentialMatch.getUserSkills().stream()
                .filter(UserSkill::wantsToLearn)
                .collect(Collectors.toSet());

        // Calcular compatibilidad
        double compatibilityScore = calculateCompatibilityScore(user, potentialMatch);

        // Encontrar skills coincidentes
        List<MatchCandidate.SkillMatchInfo> skillMatches = new ArrayList<>();
        
        // Skills que el match puede enseñar y el usuario quiere aprender
        userSkillsToLearn.forEach(userSkill -> {
            matchSkillsToTeach.stream()
                    .filter(matchSkill -> matchSkill.getSkill().getId().equals(userSkill.getSkill().getId()))
                    .forEach(matchSkill -> {
                        skillMatches.add(new MatchCandidate.SkillMatchInfo(
                                matchSkill.getSkill().getId(),
                                matchSkill.getSkill().getName(),
                                matchSkill.getSkill().getCategory(),
                                "teacher",
                                matchSkill.getProficiencyLevel().ordinal() + 1,
                                ""
                        ));
                    });
        });

        // Skills que el usuario puede enseñar y el match quiere aprender
        userSkillsToTeach.forEach(userSkill -> {
            matchSkillsToLearn.stream()
                    .filter(matchSkill -> matchSkill.getSkill().getId().equals(userSkill.getSkill().getId()))
                    .forEach(matchSkill -> {
                        skillMatches.add(new MatchCandidate.SkillMatchInfo(
                                userSkill.getSkill().getId(),
                                userSkill.getSkill().getName(),
                                userSkill.getSkill().getCategory(),
                                "learner",
                                userSkill.getProficiencyLevel().ordinal() + 1,
                                ""
                        ));
                    });
        });

        // Solo crear candidato si hay al menos una coincidencia de skills
        if (skillMatches.isEmpty()) {
            return null;
        }

        return new MatchCandidate(
                potentialMatch.getId(),
                potentialMatch.getDisplayNameOrFullName(),
                potentialMatch.getProfilePictureUrl(),
                potentialMatch.getBio(),
                potentialMatch.getCountry(),
                compatibilityScore,
                skillMatches
        );
    }

    /**
     * Calcular score de compatibilidad entre dos usuarios
     */
    private double calculateCompatibilityScore(User user1, User user2) {
        double score = 0.0;

        // Factor 1: Compatibilidad de skills (40%)
        double skillScore = calculateSkillCompatibilityScore(user1, user2);
        score += skillScore * 0.4;

        // Factor 2: Proximidad de ubicación (20%)
        double locationScore = calculateLocationScore(user1, user2);
        score += locationScore * 0.2;

        // Factor 3: Nivel de actividad (20%)
        double activityScore = calculateActivityScore(user1, user2);
        score += activityScore * 0.2;

        // Factor 4: Calificaciones de usuario (20%)
        double ratingScore = calculateRatingScore(user1, user2);
        score += ratingScore * 0.2;

        return BigDecimal.valueOf(score)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double calculateSkillCompatibilityScore(User user1, User user2) {
        // Implementación básica - puede ser mejorada
        return 0.8; // 80% por defecto
    }

    private double calculateLocationScore(User user1, User user2) {
        // Si ambos tienen el mismo país, alta compatibilidad
        if (user1.getCountry() != null && user1.getCountry().equals(user2.getCountry())) {
            return 1.0;
        }
        return 0.5; // Compatibilidad media si no hay info de ubicación
    }

    private double calculateActivityScore(User user1, User user2) {
        // Implementación básica basada en fecha de último acceso
        return 0.7; // 70% por defecto
    }

    private double calculateRatingScore(User user1, User user2) {
        // Implementación básica - puede usar calificaciones reales cuando estén disponibles
        return 0.8; // 80% por defecto
    }
}
