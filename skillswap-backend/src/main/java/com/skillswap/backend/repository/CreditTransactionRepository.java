package com.skillswap.backend.repository;

import com.skillswap.backend.model.CreditTransaction;
import com.skillswap.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Long> {

    // Historial de transacciones por usuario
    Page<CreditTransaction> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Transacciones por usuario y estado
    List<CreditTransaction> findByUserAndStatusOrderByCreatedAtDesc(User user, CreditTransaction.TransactionStatus status);

    // Transacciones por tipo
    List<CreditTransaction> findByUserAndTypeOrderByCreatedAtDesc(User user, CreditTransaction.TransactionType type);

    // Balance actual del usuario (suma de todas las transacciones completadas)
    @Query("SELECT COALESCE(SUM(ct.amount), 0) FROM CreditTransaction ct WHERE ct.user = :user AND ct.status = 'COMPLETED'")
    BigDecimal calculateUserBalance(@Param("user") User user);

    // Transacciones pendientes
    List<CreditTransaction> findByUserAndStatusOrderByCreatedAtAsc(User user, CreditTransaction.TransactionStatus status);

    // Créditos ganados por enseñar
    @Query("SELECT COALESCE(SUM(ct.amount), 0) FROM CreditTransaction ct WHERE ct.user = :user " +
           "AND ct.status = 'COMPLETED' AND ct.type = 'EARNED_TEACHING'")
    BigDecimal getTotalEarnedCredits(@Param("user") User user);

    // Créditos gastados aprendiendo
    @Query("SELECT COALESCE(SUM(ABS(ct.amount)), 0) FROM CreditTransaction ct WHERE ct.user = :user " +
           "AND ct.status = 'COMPLETED' AND ct.type = 'SPENT_LEARNING'")
    BigDecimal getTotalSpentCredits(@Param("user") User user);

    // Transacciones en un rango de fechas
    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.user = :user " +
           "AND ct.createdAt BETWEEN :startDate AND :endDate ORDER BY ct.createdAt DESC")
    List<CreditTransaction> findByUserAndDateRange(@Param("user") User user,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    // Transacciones por referencia (skill match, video session, etc.)
    List<CreditTransaction> findByReferenceIdAndReferenceTypeOrderByCreatedAtDesc(String referenceId, String referenceType);

    // Créditos próximos a expirar
    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.user = :user AND ct.status = 'COMPLETED' " +
           "AND ct.expiresAt IS NOT NULL AND ct.expiresAt BETWEEN :now AND :expirationWindow")
    List<CreditTransaction> findExpiringCredits(@Param("user") User user,
                                               @Param("now") LocalDateTime now,
                                               @Param("expirationWindow") LocalDateTime expirationWindow);

    // Créditos expirados no procesados
    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.status = 'COMPLETED' " +
           "AND ct.expiresAt IS NOT NULL AND ct.expiresAt < :now")
    List<CreditTransaction> findExpiredCredits(@Param("now") LocalDateTime now);

    // Estadísticas mensuales de un usuario
    @Query("SELECT DATE_FORMAT(ct.createdAt, '%Y-%m') as month, " +
           "SUM(CASE WHEN ct.amount > 0 THEN ct.amount ELSE 0 END) as earned, " +
           "SUM(CASE WHEN ct.amount < 0 THEN ABS(ct.amount) ELSE 0 END) as spent " +
           "FROM CreditTransaction ct WHERE ct.user = :user AND ct.status = 'COMPLETED' " +
           "GROUP BY DATE_FORMAT(ct.createdAt, '%Y-%m') ORDER BY month DESC")
    List<Object[]> getMonthlyStatistics(@Param("user") User user);

    // Top usuarios por créditos ganados
    @Query("SELECT ct.user, SUM(ct.amount) as totalEarned FROM CreditTransaction ct " +
           "WHERE ct.status = 'COMPLETED' AND ct.type = 'EARNED_TEACHING' " +
           "GROUP BY ct.user ORDER BY totalEarned DESC")
    List<Object[]> getTopEarners(Pageable pageable);

    // Transacciones recientes del sistema
    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.status = 'COMPLETED' " +
           "ORDER BY ct.processedAt DESC")
    List<CreditTransaction> getRecentTransactions(Pageable pageable);

    // Valor total de créditos en el sistema
    @Query("SELECT COALESCE(SUM(ct.amount), 0) FROM CreditTransaction ct WHERE ct.status = 'COMPLETED'")
    BigDecimal getTotalSystemCredits();

    // Transacciones fallidas recientes
    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.status = 'FAILED' " +
           "AND ct.createdAt >= :since ORDER BY ct.createdAt DESC")
    List<CreditTransaction> getFailedTransactionsSince(@Param("since") LocalDateTime since);

    // Verificar si existe transacción para una referencia específica
    boolean existsByReferenceIdAndReferenceTypeAndType(String referenceId, String referenceType, CreditTransaction.TransactionType type);

    // Buscar transacciones por skill match
    List<CreditTransaction> findBySkillMatchOrderByCreatedAtDesc(Long skillMatchId);

    // Buscar transacciones por video session
    List<CreditTransaction> findByVideoSessionOrderByCreatedAtDesc(Long videoSessionId);

    // Balance diario del usuario (para gráficos)
    @Query("SELECT DATE(ct.processedAt) as date, " +
           "SUM(ct.amount) OVER (ORDER BY DATE(ct.processedAt)) as runningBalance " +
           "FROM CreditTransaction ct WHERE ct.user = :user AND ct.status = 'COMPLETED' " +
           "GROUP BY DATE(ct.processedAt) ORDER BY date DESC")
    List<Object[]> getDailyBalanceHistory(@Param("user") User user, Pageable pageable);

    // Créditos ganados por categoría de habilidad
    @Query("SELECT s.category, SUM(ct.amount) FROM CreditTransaction ct " +
           "JOIN ct.skillMatch sm JOIN sm.teacherSkill ts JOIN ts.skill s " +
           "WHERE ct.user = :user AND ct.status = 'COMPLETED' AND ct.type = 'EARNED_TEACHING' " +
           "GROUP BY s.category ORDER BY SUM(ct.amount) DESC")
    List<Object[]> getCreditsBySkillCategory(@Param("user") User user);

    // Transacciones con metadata específica
    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.metadata LIKE %:metadataPattern%")
    List<CreditTransaction> findByMetadataContaining(@Param("metadataPattern") String metadataPattern);
}
