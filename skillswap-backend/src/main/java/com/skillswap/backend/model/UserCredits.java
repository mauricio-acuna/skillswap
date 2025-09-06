package com.skillswap.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User credit balances and summary statistics
 */
@Entity
@Table(name = "user_credits")
@EntityListeners(AuditingEntityListener.class)
public class UserCredits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "current_balance", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Current balance cannot be negative")
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "total_earned", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Total earned cannot be negative")
    private BigDecimal totalEarned = BigDecimal.ZERO;

    @Column(name = "total_spent", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Total spent cannot be negative")
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "lifetime_earnings", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Lifetime earnings cannot be negative")
    private BigDecimal lifetimeEarnings = BigDecimal.ZERO;

    @Column(name = "pending_credits", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Pending credits cannot be negative")
    private BigDecimal pendingCredits = BigDecimal.ZERO;

    @Column(name = "reserved_credits", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Reserved credits cannot be negative")
    private BigDecimal reservedCredits = BigDecimal.ZERO;

    @Column(name = "last_transaction_at")
    private LocalDateTime lastTransactionAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public UserCredits() {}

    public UserCredits(User user) {
        this.user = user;
    }

    // Helper methods
    public BigDecimal getAvailableBalance() {
        return currentBalance.subtract(reservedCredits);
    }

    public boolean hasAvailableCredits(BigDecimal amount) {
        return getAvailableBalance().compareTo(amount) >= 0;
    }

    public void addCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.currentBalance = this.currentBalance.add(amount);
            this.totalEarned = this.totalEarned.add(amount);
            this.lifetimeEarnings = this.lifetimeEarnings.add(amount);
            this.lastTransactionAt = LocalDateTime.now();
        }
    }

    public void spendCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && hasAvailableCredits(amount)) {
            this.currentBalance = this.currentBalance.subtract(amount);
            this.totalSpent = this.totalSpent.add(amount);
            this.lastTransactionAt = LocalDateTime.now();
        }
    }

    public void reserveCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && hasAvailableCredits(amount)) {
            this.reservedCredits = this.reservedCredits.add(amount);
        }
    }

    public void releaseReservedCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal toRelease = amount.min(this.reservedCredits);
            this.reservedCredits = this.reservedCredits.subtract(toRelease);
        }
    }

    // Getters and Setters
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

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(BigDecimal totalEarned) {
        this.totalEarned = totalEarned;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getLifetimeEarnings() {
        return lifetimeEarnings;
    }

    public void setLifetimeEarnings(BigDecimal lifetimeEarnings) {
        this.lifetimeEarnings = lifetimeEarnings;
    }

    public BigDecimal getPendingCredits() {
        return pendingCredits;
    }

    public void setPendingCredits(BigDecimal pendingCredits) {
        this.pendingCredits = pendingCredits;
    }

    public BigDecimal getReservedCredits() {
        return reservedCredits;
    }

    public void setReservedCredits(BigDecimal reservedCredits) {
        this.reservedCredits = reservedCredits;
    }

    public LocalDateTime getLastTransactionAt() {
        return lastTransactionAt;
    }

    public void setLastTransactionAt(LocalDateTime lastTransactionAt) {
        this.lastTransactionAt = lastTransactionAt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCredits that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UserCredits{" +
                "id=" + id +
                ", currentBalance=" + currentBalance +
                ", totalEarned=" + totalEarned +
                ", totalSpent=" + totalSpent +
                '}';
    }
}
