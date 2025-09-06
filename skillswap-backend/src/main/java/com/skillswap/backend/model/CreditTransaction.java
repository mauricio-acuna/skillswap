package com.skillswap.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_transaction")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CreditTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "reference_id")
    private String referenceId; // ID de referencia para transacciones externas o de skill matches

    @Column(name = "reference_type")
    private String referenceType; // "SKILL_MATCH", "VIDEO_SESSION", "PURCHASE", "BONUS", etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_match_id")
    private SkillMatch skillMatch; // Relacionado si es por intercambio de habilidades

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_session_id")
    private VideoSession videoSession; // Relacionado si es por sesión de video

    @Column(name = "balance_before", precision = 10, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 10, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // Para créditos que expiran

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata; // Información adicional en formato JSON

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum TransactionType {
        EARNED_TEACHING,        // Créditos ganados por enseñar
        SPENT_LEARNING,         // Créditos gastados por aprender
        BONUS_REGISTRATION,     // Bono por registro
        BONUS_REFERRAL,         // Bono por referir a alguien
        BONUS_COMPLETION,       // Bono por completar sesiones
        PURCHASE,               // Compra de créditos
        REFUND,                 // Reembolso
        PENALTY,                // Penalización por cancelación tardía
        ADMIN_ADJUSTMENT,       // Ajuste administrativo
        EXPIRATION,             // Expiración de créditos
        TRANSFER_IN,            // Transferencia recibida
        TRANSFER_OUT            // Transferencia enviada
    }

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED,
        EXPIRED
    }

    // Constructores
    public CreditTransaction() {}

    public CreditTransaction(User user, BigDecimal amount, TransactionType type, String description) {
        this.user = user;
        this.amount = amount;
        this.type = type;
        this.description = description;
    }

    // Métodos de utilidad
    public void markAsCompleted(BigDecimal balanceBefore, BigDecimal balanceAfter) {
        this.status = TransactionStatus.COMPLETED;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = TransactionStatus.FAILED;
        this.processedAt = LocalDateTime.now();
    }

    public boolean isDebit() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isCredit() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isPending() {
        return this.status == TransactionStatus.PENDING;
    }

    public boolean isCompleted() {
        return this.status == TransactionStatus.COMPLETED;
    }

    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public SkillMatch getSkillMatch() {
        return skillMatch;
    }

    public void setSkillMatch(SkillMatch skillMatch) {
        this.skillMatch = skillMatch;
    }

    public VideoSession getVideoSession() {
        return videoSession;
    }

    public void setVideoSession(VideoSession videoSession) {
        this.videoSession = videoSession;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
