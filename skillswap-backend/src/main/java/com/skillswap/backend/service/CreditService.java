package com.skillswap.backend.service;

import com.skillswap.backend.model.CreditTransaction;
import com.skillswap.backend.model.User;
import com.skillswap.backend.model.SkillMatch;
import com.skillswap.backend.model.VideoSession;
import com.skillswap.backend.repository.CreditTransactionRepository;
import com.skillswap.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CreditService {

    private static final Logger logger = LoggerFactory.getLogger(CreditService.class);

    // Configuración de créditos
    private static final BigDecimal REGISTRATION_BONUS = new BigDecimal("10.00");
    private static final BigDecimal REFERRAL_BONUS = new BigDecimal("5.00");
    private static final BigDecimal SESSION_COMPLETION_BONUS = new BigDecimal("2.00");
    private static final BigDecimal TEACHING_RATE_PER_HOUR = new BigDecimal("10.00");
    private static final BigDecimal LEARNING_COST_PER_HOUR = new BigDecimal("8.00");
    private static final BigDecimal CANCELLATION_PENALTY = new BigDecimal("-2.00");
    private static final int CREDIT_EXPIRATION_MONTHS = 12;

    @Autowired
    private CreditTransactionRepository creditTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    // Otorgar bono de registro
    public CreditTransaction grantRegistrationBonus(User user) {
        logger.info("Granting registration bonus to user {}", user.getId());
        
        CreditTransaction transaction = new CreditTransaction(
            user, 
            REGISTRATION_BONUS, 
            CreditTransaction.TransactionType.BONUS_REGISTRATION,
            "Bono de bienvenida por registrarse en SkillSwap"
        );
        
        transaction.setExpiresAt(LocalDateTime.now().plusMonths(CREDIT_EXPIRATION_MONTHS));
        
        return processTransaction(transaction);
    }

    // Otorgar bono por referir a alguien
    public CreditTransaction grantReferralBonus(User referrer, User referred) {
        logger.info("Granting referral bonus to user {} for referring {}", referrer.getId(), referred.getId());
        
        CreditTransaction transaction = new CreditTransaction(
            referrer,
            REFERRAL_BONUS,
            CreditTransaction.TransactionType.BONUS_REFERRAL,
            "Bono por referir a " + referred.getFirstName()
        );
        
        transaction.setReferenceId(referred.getId().toString());
        transaction.setReferenceType("USER_REFERRAL");
        transaction.setExpiresAt(LocalDateTime.now().plusMonths(CREDIT_EXPIRATION_MONTHS));
        
        return processTransaction(transaction);
    }

    // Procesar créditos por enseñar (ganados)
    public CreditTransaction processTeachingEarnings(User teacher, SkillMatch skillMatch, VideoSession videoSession) {
        logger.info("Processing teaching earnings for user {} from skill match {}", teacher.getId(), skillMatch.getId());
        
        // Calcular créditos basado en duración de la sesión
        BigDecimal credits = calculateTeachingCredits(videoSession.getDurationMinutes());
        
        CreditTransaction transaction = new CreditTransaction(
            teacher,
            credits,
            CreditTransaction.TransactionType.EARNED_TEACHING,
            "Créditos ganados por enseñar " + skillMatch.getTeacherSkill().getSkill().getName()
        );
        
        transaction.setSkillMatch(skillMatch);
        transaction.setVideoSession(videoSession);
        transaction.setReferenceId(videoSession.getId().toString());
        transaction.setReferenceType("VIDEO_SESSION");
        transaction.setExpiresAt(LocalDateTime.now().plusMonths(CREDIT_EXPIRATION_MONTHS));
        
        return processTransaction(transaction);
    }

    // Procesar gasto de créditos por aprender
    public CreditTransaction processLearningCost(User learner, SkillMatch skillMatch, VideoSession videoSession) {
        logger.info("Processing learning cost for user {} from skill match {}", learner.getId(), skillMatch.getId());
        
        // Calcular costo basado en duración de la sesión
        BigDecimal cost = calculateLearningCost(videoSession.getDurationMinutes());
        
        // Verificar que el usuario tenga suficientes créditos
        BigDecimal currentBalance = getUserBalance(learner);
        if (currentBalance.compareTo(cost) < 0) {
            throw new RuntimeException("Créditos insuficientes. Balance actual: " + currentBalance + ", Costo requerido: " + cost);
        }
        
        CreditTransaction transaction = new CreditTransaction(
            learner,
            cost.negate(), // Monto negativo para gasto
            CreditTransaction.TransactionType.SPENT_LEARNING,
            "Créditos gastados por aprender " + skillMatch.getLearnerSkill().getSkill().getName()
        );
        
        transaction.setSkillMatch(skillMatch);
        transaction.setVideoSession(videoSession);
        transaction.setReferenceId(videoSession.getId().toString());
        transaction.setReferenceType("VIDEO_SESSION");
        
        return processTransaction(transaction);
    }

    // Otorgar bono por completar sesión
    public CreditTransaction grantSessionCompletionBonus(User user, VideoSession videoSession) {
        logger.info("Granting session completion bonus to user {} for video session {}", user.getId(), videoSession.getId());
        
        CreditTransaction transaction = new CreditTransaction(
            user,
            SESSION_COMPLETION_BONUS,
            CreditTransaction.TransactionType.BONUS_COMPLETION,
            "Bono por completar sesión de video exitosamente"
        );
        
        transaction.setVideoSession(videoSession);
        transaction.setReferenceId(videoSession.getId().toString());
        transaction.setReferenceType("VIDEO_SESSION_COMPLETION");
        transaction.setExpiresAt(LocalDateTime.now().plusMonths(CREDIT_EXPIRATION_MONTHS));
        
        return processTransaction(transaction);
    }

    // Aplicar penalización por cancelación tardía
    public CreditTransaction applyCancellationPenalty(User user, VideoSession videoSession) {
        logger.info("Applying cancellation penalty to user {} for video session {}", user.getId(), videoSession.getId());
        
        CreditTransaction transaction = new CreditTransaction(
            user,
            CANCELLATION_PENALTY,
            CreditTransaction.TransactionType.PENALTY,
            "Penalización por cancelación tardía de sesión"
        );
        
        transaction.setVideoSession(videoSession);
        transaction.setReferenceId(videoSession.getId().toString());
        transaction.setReferenceType("VIDEO_SESSION_CANCELLATION");
        
        return processTransaction(transaction);
    }

    // Procesar transacción y actualizar balance
    private CreditTransaction processTransaction(CreditTransaction transaction) {
        BigDecimal balanceBefore = getUserBalance(transaction.getUser());
        BigDecimal balanceAfter = balanceBefore.add(transaction.getAmount());
        
        transaction.markAsCompleted(balanceBefore, balanceAfter);
        
        CreditTransaction savedTransaction = creditTransactionRepository.save(transaction);
        logger.info("Transaction processed successfully: {} credits for user {}", 
                   transaction.getAmount(), transaction.getUser().getId());
        
        return savedTransaction;
    }

    // Calcular créditos ganados por enseñar
    private BigDecimal calculateTeachingCredits(Integer durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal hours = new BigDecimal(durationMinutes).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
        return TEACHING_RATE_PER_HOUR.multiply(hours);
    }

    // Calcular costo por aprender
    private BigDecimal calculateLearningCost(Integer durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal hours = new BigDecimal(durationMinutes).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
        return LEARNING_COST_PER_HOUR.multiply(hours);
    }

    // Obtener balance actual del usuario
    public BigDecimal getUserBalance(User user) {
        BigDecimal balance = creditTransactionRepository.calculateUserBalance(user);
        return balance != null ? balance : BigDecimal.ZERO;
    }

    // Verificar si el usuario tiene suficientes créditos
    public boolean hasSufficientCredits(User user, BigDecimal requiredAmount) {
        BigDecimal currentBalance = getUserBalance(user);
        return currentBalance.compareTo(requiredAmount) >= 0;
    }

    // Obtener historial de transacciones
    public Page<CreditTransaction> getUserTransactionHistory(User user, Pageable pageable) {
        return creditTransactionRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    // Obtener estadísticas del usuario
    public CreditStatistics getUserCreditStatistics(User user) {
        BigDecimal totalEarned = creditTransactionRepository.getTotalEarnedCredits(user);
        BigDecimal totalSpent = creditTransactionRepository.getTotalSpentCredits(user);
        BigDecimal currentBalance = getUserBalance(user);
        
        List<CreditTransaction> expiringCredits = creditTransactionRepository.findExpiringCredits(
            user, LocalDateTime.now(), LocalDateTime.now().plusDays(30)
        );
        
        BigDecimal expiringAmount = expiringCredits.stream()
            .map(CreditTransaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new CreditStatistics(totalEarned, totalSpent, currentBalance, expiringAmount, expiringCredits.size());
    }

    // Transferir créditos entre usuarios
    public void transferCredits(User fromUser, User toUser, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto de transferencia debe ser positivo");
        }
        
        if (!hasSufficientCredits(fromUser, amount)) {
            throw new RuntimeException("Créditos insuficientes para la transferencia");
        }
        
        // Crear transacción de salida
        CreditTransaction outTransaction = new CreditTransaction(
            fromUser,
            amount.negate(),
            CreditTransaction.TransactionType.TRANSFER_OUT,
            "Transferencia enviada a " + toUser.getFirstName() + ": " + description
        );
        outTransaction.setReferenceId(toUser.getId().toString());
        outTransaction.setReferenceType("CREDIT_TRANSFER");
        
        // Crear transacción de entrada
        CreditTransaction inTransaction = new CreditTransaction(
            toUser,
            amount,
            CreditTransaction.TransactionType.TRANSFER_IN,
            "Transferencia recibida de " + fromUser.getFirstName() + ": " + description
        );
        inTransaction.setReferenceId(fromUser.getId().toString());
        inTransaction.setReferenceType("CREDIT_TRANSFER");
        inTransaction.setExpiresAt(LocalDateTime.now().plusMonths(CREDIT_EXPIRATION_MONTHS));
        
        // Procesar ambas transacciones
        processTransaction(outTransaction);
        processTransaction(inTransaction);
        
        logger.info("Credit transfer completed: {} credits from user {} to user {}", 
                   amount, fromUser.getId(), toUser.getId());
    }

    // Procesar expiración de créditos (tarea programada)
    @Scheduled(cron = "0 0 2 * * ?") // Ejecutar diariamente a las 2 AM
    public void processExpiredCredits() {
        logger.info("Processing expired credits...");
        
        List<CreditTransaction> expiredCredits = creditTransactionRepository.findExpiredCredits(LocalDateTime.now());
        
        for (CreditTransaction expiredCredit : expiredCredits) {
            // Crear transacción de expiración
            CreditTransaction expirationTransaction = new CreditTransaction(
                expiredCredit.getUser(),
                expiredCredit.getAmount().negate(),
                CreditTransaction.TransactionType.EXPIRATION,
                "Expiración de créditos del " + expiredCredit.getCreatedAt().toLocalDate()
            );
            
            expirationTransaction.setReferenceId(expiredCredit.getId().toString());
            expirationTransaction.setReferenceType("CREDIT_EXPIRATION");
            
            processTransaction(expirationTransaction);
            
            // Marcar el crédito original como expirado
            expiredCredit.setStatus(CreditTransaction.TransactionStatus.EXPIRED);
            creditTransactionRepository.save(expiredCredit);
        }
        
        logger.info("Processed {} expired credit transactions", expiredCredits.size());
    }

    // Comprar créditos (integración con sistema de pagos)
    public CreditTransaction purchaseCredits(User user, BigDecimal amount, String paymentReference) {
        logger.info("Processing credit purchase for user {}: {} credits", user.getId(), amount);
        
        CreditTransaction transaction = new CreditTransaction(
            user,
            amount,
            CreditTransaction.TransactionType.PURCHASE,
            "Compra de créditos - Referencia de pago: " + paymentReference
        );
        
        transaction.setReferenceId(paymentReference);
        transaction.setReferenceType("PAYMENT");
        transaction.setExpiresAt(LocalDateTime.now().plusMonths(CREDIT_EXPIRATION_MONTHS));
        
        return processTransaction(transaction);
    }

    // Clase para estadísticas
    public static class CreditStatistics {
        private BigDecimal totalEarned;
        private BigDecimal totalSpent;
        private BigDecimal currentBalance;
        private BigDecimal expiringAmount;
        private int expiringTransactions;

        public CreditStatistics(BigDecimal totalEarned, BigDecimal totalSpent, BigDecimal currentBalance, 
                               BigDecimal expiringAmount, int expiringTransactions) {
            this.totalEarned = totalEarned;
            this.totalSpent = totalSpent;
            this.currentBalance = currentBalance;
            this.expiringAmount = expiringAmount;
            this.expiringTransactions = expiringTransactions;
        }

        // Getters
        public BigDecimal getTotalEarned() { return totalEarned; }
        public BigDecimal getTotalSpent() { return totalSpent; }
        public BigDecimal getCurrentBalance() { return currentBalance; }
        public BigDecimal getExpiringAmount() { return expiringAmount; }
        public int getExpiringTransactions() { return expiringTransactions; }
    }
}
