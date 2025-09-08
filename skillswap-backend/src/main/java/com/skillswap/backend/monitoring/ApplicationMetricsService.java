package com.skillswap.backend.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Application Metrics Service
 * Provides business and technical metrics for monitoring and alerting
 */
@Service
public class ApplicationMetricsService {

    private final MeterRegistry meterRegistry;
    
    // Counters for business events
    private final Counter userRegistrations;
    private final Counter userLogins;
    private final Counter videoSessionsCreated;
    private final Counter videoSessionsCompleted;
    private final Counter messagesExchanged;
    private final Counter creditsTransferred;
    
    // Counters for security events
    private final Counter securityViolations;
    
    // Timers for performance monitoring
    private final Timer databaseOperationTime;
    private final Timer videoSessionDuration;
    private final Timer apiResponseTime;
    
    // Gauges for system state
    private final AtomicLong activeVideoSessions = new AtomicLong(0);
    private final AtomicLong activeWebSocketConnections = new AtomicLong(0);
    private final AtomicLong totalCreditBalance = new AtomicLong(0);
    private final ConcurrentHashMap<String, AtomicLong> activeUsersByRole = new ConcurrentHashMap<>();
    
    @Autowired
    public ApplicationMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize business metrics
        this.userRegistrations = Counter.builder("skillswap.users.registrations")
                .description("Total number of user registrations")
                .tag("type", "business")
                .register(meterRegistry);
                
        this.userLogins = Counter.builder("skillswap.users.logins")
                .description("Total number of successful user logins")
                .tag("type", "business")
                .register(meterRegistry);
                
        this.videoSessionsCreated = Counter.builder("skillswap.video_sessions.created")
                .description("Total number of video sessions created")
                .tag("type", "business")
                .register(meterRegistry);
                
        this.videoSessionsCompleted = Counter.builder("skillswap.video_sessions.completed")
                .description("Total number of video sessions completed")
                .tag("type", "business")
                .register(meterRegistry);
                
        this.messagesExchanged = Counter.builder("skillswap.messages.exchanged")
                .description("Total number of messages exchanged")
                .tag("type", "business")
                .register(meterRegistry);
                
        this.creditsTransferred = Counter.builder("skillswap.credits.transferred")
                .description("Total number of credit transfers")
                .tag("type", "business")
                .register(meterRegistry);
        
        // Initialize security metrics
        this.securityViolations = Counter.builder("skillswap.security.violations")
                .description("Total number of security violations detected")
                .tag("type", "security")
                .register(meterRegistry);
        
        // Initialize performance metrics
        this.databaseOperationTime = Timer.builder("skillswap.database.operation_time")
                .description("Time taken for database operations")
                .tag("type", "performance")
                .register(meterRegistry);
                
        this.videoSessionDuration = Timer.builder("skillswap.video_sessions.duration")
                .description("Duration of video sessions")
                .tag("type", "business")
                .register(meterRegistry);
                
        this.apiResponseTime = Timer.builder("skillswap.api.response_time")
                .description("API response time")
                .tag("type", "performance")
                .register(meterRegistry);
        
        // Initialize gauges
        Gauge.builder("skillswap.video_sessions.active", activeVideoSessions, AtomicLong::get)
                .description("Number of currently active video sessions")
                .tag("type", "business")
                .register(meterRegistry);
                
        Gauge.builder("skillswap.websocket.connections", activeWebSocketConnections, AtomicLong::get)
                .description("Number of active WebSocket connections")
                .tag("type", "technical")
                .register(meterRegistry);
                
        Gauge.builder("skillswap.credits.total_balance", totalCreditBalance, AtomicLong::get)
                .description("Total credit balance in the system")
                .tag("type", "business")
                .register(meterRegistry);
        
        // Initialize user role counters
        activeUsersByRole.put("STUDENT", new AtomicLong(0));
        activeUsersByRole.put("INSTRUCTOR", new AtomicLong(0));
        activeUsersByRole.put("ADMIN", new AtomicLong(0));
        
        for (String role : activeUsersByRole.keySet()) {
            AtomicLong counter = activeUsersByRole.get(role);
            Gauge.builder("skillswap.users.active_by_role", counter, AtomicLong::get)
                    .description("Number of active users by role")
                    .tag("role", role)
                    .tag("type", "business")
                    .register(meterRegistry);
        }
    }
    
    // Business Metrics Methods
    
    public void recordUserRegistration() {
        userRegistrations.increment();
    }
    
    public void recordUserLogin(String userRole) {
        userLogins.increment();
        incrementActiveUsersByRole(userRole);
    }
    
    public void recordUserLogout(String userRole) {
        decrementActiveUsersByRole(userRole);
    }
    
    public void recordVideoSessionCreated() {
        videoSessionsCreated.increment();
        activeVideoSessions.incrementAndGet();
    }
    
    public void recordVideoSessionCompleted(long durationMinutes) {
        videoSessionsCompleted.increment();
        activeVideoSessions.decrementAndGet();
        videoSessionDuration.record(durationMinutes, java.util.concurrent.TimeUnit.MINUTES);
    }
    
    public void recordMessageExchanged() {
        messagesExchanged.increment();
    }
    
    public void recordCreditTransfer(double amount) {
        creditsTransferred.increment();
        // Note: This is a simplified approach - in reality you'd track actual balance changes
    }
    
    // Security Metrics Methods
    
    public void recordSecurityViolation(String violationType) {
        Counter.builder("skillswap.security.violations")
                .tag("violation_type", violationType)
                .register(meterRegistry)
                .increment();
    }
    
    public void recordRateLimitExceeded(String endpoint) {
        Counter.builder("skillswap.security.rate_limit_exceeded")
                .tag("endpoint", endpoint)
                .register(meterRegistry)
                .increment();
    }
    
    public void recordAuthenticationFailure(String reason) {
        Counter.builder("skillswap.security.auth_failures")
                .tag("reason", reason)
                .register(meterRegistry)
                .increment();
    }
    
    public void recordSuspiciousActivity(String activityType) {
        Counter.builder("skillswap.security.suspicious_activity")
                .tag("activity_type", activityType)
                .register(meterRegistry)
                .increment();
    }
    
    // Performance Metrics Methods
    
    public Timer.Sample startDatabaseTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordDatabaseOperation(Timer.Sample sample, String operation) {
        sample.stop(Timer.builder("skillswap.database.operation_time")
                .tag("operation", operation)
                .register(meterRegistry));
    }
    
    public Timer.Sample startApiTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordApiResponse(Timer.Sample sample, String endpoint, String method, int statusCode) {
        sample.stop(Timer.builder("skillswap.api.response_time")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status_code", String.valueOf(statusCode))
                .register(meterRegistry));
    }
    
    // WebSocket Metrics
    
    public void recordWebSocketConnection() {
        activeWebSocketConnections.incrementAndGet();
    }
    
    public void recordWebSocketDisconnection() {
        activeWebSocketConnections.decrementAndGet();
    }
    
    // System State Methods
    
    public void updateTotalCreditBalance(long balance) {
        totalCreditBalance.set(balance);
    }
    
    private void incrementActiveUsersByRole(String role) {
        activeUsersByRole.computeIfAbsent(role, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    private void decrementActiveUsersByRole(String role) {
        AtomicLong counter = activeUsersByRole.get(role);
        if (counter != null && counter.get() > 0) {
            counter.decrementAndGet();
        }
    }
    
    // Health Check Methods
    
    /**
     * Get system health metrics
     */
    public SystemHealthMetrics getSystemHealth() {
        return SystemHealthMetrics.builder()
                .activeVideoSessions(activeVideoSessions.get())
                .activeWebSocketConnections(activeWebSocketConnections.get())
                .totalUsers(activeUsersByRole.values().stream().mapToLong(AtomicLong::get).sum())
                .totalCreditBalance(totalCreditBalance.get())
                .totalRegistrations(userRegistrations.count())
                .totalSecurityViolations(securityViolations.count())
                .build();
    }
    
    /**
     * Data class for system health metrics
     */
    public static class SystemHealthMetrics {
        private final long activeVideoSessions;
        private final long activeWebSocketConnections;
        private final long totalUsers;
        private final long totalCreditBalance;
        private final double totalRegistrations;
        private final double totalSecurityViolations;
        
        private SystemHealthMetrics(Builder builder) {
            this.activeVideoSessions = builder.activeVideoSessions;
            this.activeWebSocketConnections = builder.activeWebSocketConnections;
            this.totalUsers = builder.totalUsers;
            this.totalCreditBalance = builder.totalCreditBalance;
            this.totalRegistrations = builder.totalRegistrations;
            this.totalSecurityViolations = builder.totalSecurityViolations;
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        // Getters
        public long getActiveVideoSessions() { return activeVideoSessions; }
        public long getActiveWebSocketConnections() { return activeWebSocketConnections; }
        public long getTotalUsers() { return totalUsers; }
        public long getTotalCreditBalance() { return totalCreditBalance; }
        public double getTotalRegistrations() { return totalRegistrations; }
        public double getTotalSecurityViolations() { return totalSecurityViolations; }
        
        public static class Builder {
            private long activeVideoSessions;
            private long activeWebSocketConnections;
            private long totalUsers;
            private long totalCreditBalance;
            private double totalRegistrations;
            private double totalSecurityViolations;
            
            public Builder activeVideoSessions(long activeVideoSessions) {
                this.activeVideoSessions = activeVideoSessions;
                return this;
            }
            
            public Builder activeWebSocketConnections(long activeWebSocketConnections) {
                this.activeWebSocketConnections = activeWebSocketConnections;
                return this;
            }
            
            public Builder totalUsers(long totalUsers) {
                this.totalUsers = totalUsers;
                return this;
            }
            
            public Builder totalCreditBalance(long totalCreditBalance) {
                this.totalCreditBalance = totalCreditBalance;
                return this;
            }
            
            public Builder totalRegistrations(double totalRegistrations) {
                this.totalRegistrations = totalRegistrations;
                return this;
            }
            
            public Builder totalSecurityViolations(double totalSecurityViolations) {
                this.totalSecurityViolations = totalSecurityViolations;
                return this;
            }
            
            public SystemHealthMetrics build() {
                return new SystemHealthMetrics(this);
            }
        }
    }
}
