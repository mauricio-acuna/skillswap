package com.skillswap.backend.repository;

import com.skillswap.backend.model.MatchReport;
import com.skillswap.backend.model.User;
import com.skillswap.backend.model.SkillMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchReportRepository extends JpaRepository<MatchReport, Long> {

    /**
     * Buscar reportes por usuario reportado
     */
    List<MatchReport> findByReportedUser(User reportedUser);

    /**
     * Buscar reportes por usuario reportador
     */
    List<MatchReport> findByReporter(User reporter);

    /**
     * Buscar reportes por skill match
     */
    List<MatchReport> findBySkillMatch(SkillMatch skillMatch);

    /**
     * Buscar reportes por estatus
     */
    List<MatchReport> findByStatus(MatchReport.ReportStatus status);

    /**
     * Buscar reportes pendientes
     */
    List<MatchReport> findByStatusOrderByCreatedAtAsc(MatchReport.ReportStatus status);

    /**
     * Contar reportes por usuario en un período
     */
    @Query("SELECT COUNT(mr) FROM MatchReport mr WHERE mr.reportedUser = :user " +
           "AND mr.createdAt >= :startDate AND mr.createdAt <= :endDate")
    Long countReportsForUserInPeriod(@Param("user") User user, 
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Buscar reportes por tipo
     */
    List<MatchReport> findByReportType(MatchReport.ReportType reportType);

    /**
     * Buscar reportes recientes (últimos 30 días)
     */
    @Query("SELECT mr FROM MatchReport mr WHERE mr.createdAt >= :date ORDER BY mr.createdAt DESC")
    List<MatchReport> findRecentReports(@Param("date") LocalDateTime date);

    /**
     * Verificar si un usuario ya reportó un match específico
     */
    boolean existsBySkillMatchAndReporter(SkillMatch skillMatch, User reporter);
}
