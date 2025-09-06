package com.skillswap.backend.controller;

import com.skillswap.backend.dto.MatchCandidate;
import com.skillswap.backend.model.SkillMatch;
import com.skillswap.backend.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matching")
@PreAuthorize("hasRole('USER')")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    /**
     * Buscar candidatos potenciales para intercambio de skills
     */
    @GetMapping("/candidates")
    public ResponseEntity<List<MatchCandidate>> findMatchCandidates(
            @RequestParam Long skillId,
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        List<MatchCandidate> candidates = matchingService.findMatchCandidates(userId, skillId, limit);
        return ResponseEntity.ok(candidates);
    }

    /**
     * Enviar solicitud de match
     */
    @PostMapping("/request")
    public ResponseEntity<SkillMatch> sendMatchRequest(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        Long targetUserId = Long.valueOf(request.get("targetUserId").toString());
        Long skillId = Long.valueOf(request.get("skillId").toString());
        String message = request.get("message") != null ? request.get("message").toString() : "";
        
        SkillMatch match = matchingService.sendMatchRequest(userId, targetUserId, skillId, message);
        return ResponseEntity.ok(match);
    }

    /**
     * Aceptar solicitud de match
     */
    @PostMapping("/accept/{matchId}")
    public ResponseEntity<SkillMatch> acceptMatch(
            @PathVariable Long matchId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        SkillMatch match = matchingService.acceptMatch(matchId, userId);
        return ResponseEntity.ok(match);
    }

    /**
     * Rechazar solicitud de match
     */
    @PostMapping("/reject/{matchId}")
    public ResponseEntity<Void> rejectMatch(
            @PathVariable Long matchId,
            @RequestBody(required = false) Map<String, String> requestBody,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        String reason = requestBody != null ? requestBody.get("reason") : "";
        matchingService.rejectMatch(matchId, userId, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtener solicitudes pendientes recibidas
     */
    @GetMapping("/pending/received")
    public ResponseEntity<List<SkillMatch>> getPendingMatches(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<SkillMatch> matches = matchingService.getPendingMatchesForUser(userId);
        return ResponseEntity.ok(matches);
    }

    /**
     * Obtener solicitudes enviadas
     */
    @GetMapping("/pending/sent")
    public ResponseEntity<List<SkillMatch>> getSentRequests(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<SkillMatch> matches = matchingService.getSentRequestsByUser(userId);
        return ResponseEntity.ok(matches);
    }

    /**
     * Obtener matches activos/confirmados
     */
    @GetMapping("/active")
    public ResponseEntity<List<SkillMatch>> getActiveMatches(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<SkillMatch> matches = matchingService.getActiveMatchesForUser(userId);
        return ResponseEntity.ok(matches);
    }

    /**
     * Obtener detalles de un match específico
     */
    @GetMapping("/{matchId}")
    public ResponseEntity<SkillMatch> getMatchDetails(
            @PathVariable Long matchId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        SkillMatch match = matchingService.getMatchDetails(matchId, userId);
        return ResponseEntity.ok(match);
    }

    /**
     * Cancelar un match activo
     */
    @PostMapping("/cancel/{matchId}")
    public ResponseEntity<Void> cancelMatch(
            @PathVariable Long matchId,
            @RequestBody(required = false) Map<String, String> requestBody,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        String reason = requestBody != null ? requestBody.get("reason") : "";
        matchingService.cancelMatch(matchId, userId, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * Marcar match como completado
     */
    @PostMapping("/complete/{matchId}")
    public ResponseEntity<SkillMatch> completeMatch(
            @PathVariable Long matchId,
            @RequestBody(required = false) Map<String, Object> requestBody,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        Integer rating = requestBody != null && requestBody.get("rating") != null ? 
                        Integer.valueOf(requestBody.get("rating").toString()) : null;
        String feedback = requestBody != null && requestBody.get("feedback") != null ? 
                         requestBody.get("feedback").toString() : "";
        
        SkillMatch match = matchingService.completeMatch(matchId, userId, rating, feedback);
        return ResponseEntity.ok(match);
    }

    /**
     * Buscar matches por skill específico
     */
    @GetMapping("/by-skill/{skillId}")
    public ResponseEntity<List<SkillMatch>> getMatchesBySkill(
            @PathVariable Long skillId,
            @RequestParam(defaultValue = "ACCEPTED") String status,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        SkillMatch.MatchStatus matchStatus;
        
        try {
            matchStatus = SkillMatch.MatchStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            matchStatus = SkillMatch.MatchStatus.ACCEPTED;
        }
        
        List<SkillMatch> matches = matchingService.getMatchesByUserAndSkill(userId, skillId, matchStatus);
        return ResponseEntity.ok(matches);
    }

    /**
     * Obtener estadísticas de matching del usuario
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMatchingStats(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Map<String, Object> stats = matchingService.getMatchingStatsForUser(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Buscar matches recomendados basados en compatibilidad
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<MatchCandidate>> getRecommendations(
            @RequestParam(defaultValue = "5") int limit,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        List<MatchCandidate> recommendations = matchingService.getRecommendedMatches(userId, limit);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Reportar un match por comportamiento inapropiado
     */
    @PostMapping("/report/{matchId}")
    public ResponseEntity<Void> reportMatch(
            @PathVariable Long matchId,
            @RequestBody Map<String, String> reportData,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        String reason = reportData.get("reason");
        String description = reportData.get("description");
        
        matchingService.reportMatch(matchId, userId, reason, description);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtener historial de matches del usuario
     */
    @GetMapping("/history")
    public ResponseEntity<List<SkillMatch>> getMatchHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        List<SkillMatch> history = matchingService.getMatchHistory(userId, page, size);
        return ResponseEntity.ok(history);
    }

    /**
     * Activar/desactivar modo de matching automático
     */
    @PostMapping("/auto-matching")
    public ResponseEntity<Void> toggleAutoMatching(
            @RequestBody Map<String, Boolean> request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        boolean enabled = request.get("enabled");
        matchingService.setAutoMatching(userId, enabled);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtener configuración de matching del usuario
     */
    @GetMapping("/preferences")
    public ResponseEntity<Map<String, Object>> getMatchingPreferences(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Map<String, Object> preferences = matchingService.getMatchingPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    /**
     * Actualizar configuración de matching del usuario
     */
    @PutMapping("/preferences")
    public ResponseEntity<Void> updateMatchingPreferences(
            @RequestBody Map<String, Object> preferences,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        matchingService.updateMatchingPreferences(userId, preferences);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // Asume que el principal contiene el ID del usuario
        return Long.valueOf(authentication.getName());
    }
}
