package com.skillswap.backend.repository;

import com.skillswap.backend.model.MatchReview;
import com.skillswap.backend.model.User;
import com.skillswap.backend.model.SkillMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchReviewRepository extends JpaRepository<MatchReview, Long> {

    /**
     * Buscar reviews por usuario revisado
     */
    List<MatchReview> findByReviewedUser(User reviewedUser);

    /**
     * Buscar reviews por reviewer
     */
    List<MatchReview> findByReviewer(User reviewer);

    /**
     * Buscar reviews por skill match
     */
    List<MatchReview> findBySkillMatch(SkillMatch skillMatch);

    /**
     * Verificar si ya existe review de un usuario para un match específico
     */
    Optional<MatchReview> findBySkillMatchAndReviewer(SkillMatch skillMatch, User reviewer);

    /**
     * Obtener promedio de rating para un usuario
     */
    @Query("SELECT AVG(mr.rating) FROM MatchReview mr WHERE mr.reviewedUser = :user")
    Double getAverageRatingForUser(@Param("user") User user);

    /**
     * Contar reviews positivas (rating >= 4) para un usuario
     */
    @Query("SELECT COUNT(mr) FROM MatchReview mr WHERE mr.reviewedUser = :user AND mr.rating >= 4")
    Long countPositiveReviewsForUser(@Param("user") User user);

    /**
     * Obtener reviews recientes para un usuario (últimas 10)
     */
    @Query("SELECT mr FROM MatchReview mr WHERE mr.reviewedUser = :user " +
           "ORDER BY mr.createdAt DESC LIMIT 10")
    List<MatchReview> findRecentReviewsForUser(@Param("user") User user);

    /**
     * Buscar reviews por rating específico
     */
    List<MatchReview> findByRating(Integer rating);

    /**
     * Buscar reviews con feedback detallado (más de 20 caracteres)
     */
    @Query("SELECT mr FROM MatchReview mr WHERE LENGTH(mr.feedback) > 20")
    List<MatchReview> findReviewsWithDetailedFeedback();
}
