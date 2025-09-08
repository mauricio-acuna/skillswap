package com.skillswap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;

/**
 * Centralized configuration properties for SkillSwap application
 * This class provides type-safe configuration management with validation
 * 
 * @author SkillSwap Team
 * @version 1.0
 * @since 2024
 */
@Component
@ConfigurationProperties(prefix = "skillswap")
@Validated
public class ApplicationConfigurationProperties {

    @NestedConfigurationProperty
    @Valid
    private final Security security = new Security();

    @NestedConfigurationProperty
    @Valid
    private final Performance performance = new Performance();

    @NestedConfigurationProperty
    @Valid
    private final Business business = new Business();

    @NestedConfigurationProperty
    @Valid
    private final Monitoring monitoring = new Monitoring();

    @NestedConfigurationProperty
    @Valid
    private final Integration integration = new Integration();

    // Getters
    public Security getSecurity() { return security; }
    public Performance getPerformance() { return performance; }
    public Business getBusiness() { return business; }
    public Monitoring getMonitoring() { return monitoring; }
    public Integration getIntegration() { return integration; }

    /**
     * Security related configuration
     */
    public static class Security {
        @NotBlank
        private String jwtSecret = "skillswap-default-secret-key-change-in-production";
        
        @NotNull
        private Duration jwtExpiration = Duration.ofHours(24);
        
        @Min(1)
        @Max(1000)
        private int maxLoginAttempts = 5;
        
        @NotNull
        private Duration lockoutDuration = Duration.ofMinutes(15);
        
        @Min(1)
        @Max(100)
        private int rateLimitRequestsPerMinute = 60;
        
        private boolean enableSecurityAudit = true;
        private boolean enableEncryption = true;
        
        @NotBlank
        private String encryptionAlgorithm = "AES-256-GCM";

        // Getters and setters
        public String getJwtSecret() { return jwtSecret; }
        public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }
        
        public Duration getJwtExpiration() { return jwtExpiration; }
        public void setJwtExpiration(Duration jwtExpiration) { this.jwtExpiration = jwtExpiration; }
        
        public int getMaxLoginAttempts() { return maxLoginAttempts; }
        public void setMaxLoginAttempts(int maxLoginAttempts) { this.maxLoginAttempts = maxLoginAttempts; }
        
        public Duration getLockoutDuration() { return lockoutDuration; }
        public void setLockoutDuration(Duration lockoutDuration) { this.lockoutDuration = lockoutDuration; }
        
        public int getRateLimitRequestsPerMinute() { return rateLimitRequestsPerMinute; }
        public void setRateLimitRequestsPerMinute(int rateLimitRequestsPerMinute) { 
            this.rateLimitRequestsPerMinute = rateLimitRequestsPerMinute; 
        }
        
        public boolean isEnableSecurityAudit() { return enableSecurityAudit; }
        public void setEnableSecurityAudit(boolean enableSecurityAudit) { 
            this.enableSecurityAudit = enableSecurityAudit; 
        }
        
        public boolean isEnableEncryption() { return enableEncryption; }
        public void setEnableEncryption(boolean enableEncryption) { this.enableEncryption = enableEncryption; }
        
        public String getEncryptionAlgorithm() { return encryptionAlgorithm; }
        public void setEncryptionAlgorithm(String encryptionAlgorithm) { 
            this.encryptionAlgorithm = encryptionAlgorithm; 
        }
    }

    /**
     * Performance related configuration
     */
    public static class Performance {
        @NotNull
        private Duration requestTimeout = Duration.ofSeconds(30);
        
        @Min(1)
        @Max(1000)
        private int maxConcurrentSessions = 100;
        
        @Min(1)
        @Max(10000)
        private int connectionPoolSize = 20;
        
        @NotNull
        private Duration cacheExpiration = Duration.ofMinutes(30);
        
        private boolean enableCaching = true;
        private boolean enableCompression = true;
        
        @Min(1)
        @Max(100)
        private int maxRetryAttempts = 3;

        // Getters and setters
        public Duration getRequestTimeout() { return requestTimeout; }
        public void setRequestTimeout(Duration requestTimeout) { this.requestTimeout = requestTimeout; }
        
        public int getMaxConcurrentSessions() { return maxConcurrentSessions; }
        public void setMaxConcurrentSessions(int maxConcurrentSessions) { 
            this.maxConcurrentSessions = maxConcurrentSessions; 
        }
        
        public int getConnectionPoolSize() { return connectionPoolSize; }
        public void setConnectionPoolSize(int connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }
        
        public Duration getCacheExpiration() { return cacheExpiration; }
        public void setCacheExpiration(Duration cacheExpiration) { this.cacheExpiration = cacheExpiration; }
        
        public boolean isEnableCaching() { return enableCaching; }
        public void setEnableCaching(boolean enableCaching) { this.enableCaching = enableCaching; }
        
        public boolean isEnableCompression() { return enableCompression; }
        public void setEnableCompression(boolean enableCompression) { this.enableCompression = enableCompression; }
        
        public int getMaxRetryAttempts() { return maxRetryAttempts; }
        public void setMaxRetryAttempts(int maxRetryAttempts) { this.maxRetryAttempts = maxRetryAttempts; }
    }

    /**
     * Business logic configuration
     */
    public static class Business {
        @Min(1)
        @Max(1000)
        private int defaultCreditBalance = 100;
        
        @Min(1)
        @Max(60)
        private int minSessionDurationMinutes = 15;
        
        @Min(1)
        @Max(480)
        private int maxSessionDurationMinutes = 120;
        
        @Min(1)
        @Max(100)
        private int maxConcurrentSessionsPerUser = 3;
        
        private boolean enableCreditTransfers = true;
        private boolean enableSessionRecording = false;
        
        @NotNull
        private Duration sessionIdleTimeout = Duration.ofMinutes(5);

        // Getters and setters
        public int getDefaultCreditBalance() { return defaultCreditBalance; }
        public void setDefaultCreditBalance(int defaultCreditBalance) { 
            this.defaultCreditBalance = defaultCreditBalance; 
        }
        
        public int getMinSessionDurationMinutes() { return minSessionDurationMinutes; }
        public void setMinSessionDurationMinutes(int minSessionDurationMinutes) { 
            this.minSessionDurationMinutes = minSessionDurationMinutes; 
        }
        
        public int getMaxSessionDurationMinutes() { return maxSessionDurationMinutes; }
        public void setMaxSessionDurationMinutes(int maxSessionDurationMinutes) { 
            this.maxSessionDurationMinutes = maxSessionDurationMinutes; 
        }
        
        public int getMaxConcurrentSessionsPerUser() { return maxConcurrentSessionsPerUser; }
        public void setMaxConcurrentSessionsPerUser(int maxConcurrentSessionsPerUser) { 
            this.maxConcurrentSessionsPerUser = maxConcurrentSessionsPerUser; 
        }
        
        public boolean isEnableCreditTransfers() { return enableCreditTransfers; }
        public void setEnableCreditTransfers(boolean enableCreditTransfers) { 
            this.enableCreditTransfers = enableCreditTransfers; 
        }
        
        public boolean isEnableSessionRecording() { return enableSessionRecording; }
        public void setEnableSessionRecording(boolean enableSessionRecording) { 
            this.enableSessionRecording = enableSessionRecording; 
        }
        
        public Duration getSessionIdleTimeout() { return sessionIdleTimeout; }
        public void setSessionIdleTimeout(Duration sessionIdleTimeout) { 
            this.sessionIdleTimeout = sessionIdleTimeout; 
        }
    }

    /**
     * Monitoring and observability configuration
     */
    public static class Monitoring {
        private boolean enableMetrics = true;
        private boolean enableHealthChecks = true;
        private boolean enableTracing = true;
        
        @NotNull
        private Duration metricsCollectionInterval = Duration.ofSeconds(30);
        
        @Min(1)
        @Max(10000)
        private int maxMetricsSamples = 1000;
        
        private boolean enableDetailedLogging = true;
        private boolean enablePerformanceLogging = true;

        // Getters and setters
        public boolean isEnableMetrics() { return enableMetrics; }
        public void setEnableMetrics(boolean enableMetrics) { this.enableMetrics = enableMetrics; }
        
        public boolean isEnableHealthChecks() { return enableHealthChecks; }
        public void setEnableHealthChecks(boolean enableHealthChecks) { 
            this.enableHealthChecks = enableHealthChecks; 
        }
        
        public boolean isEnableTracing() { return enableTracing; }
        public void setEnableTracing(boolean enableTracing) { this.enableTracing = enableTracing; }
        
        public Duration getMetricsCollectionInterval() { return metricsCollectionInterval; }
        public void setMetricsCollectionInterval(Duration metricsCollectionInterval) { 
            this.metricsCollectionInterval = metricsCollectionInterval; 
        }
        
        public int getMaxMetricsSamples() { return maxMetricsSamples; }
        public void setMaxMetricsSamples(int maxMetricsSamples) { this.maxMetricsSamples = maxMetricsSamples; }
        
        public boolean isEnableDetailedLogging() { return enableDetailedLogging; }
        public void setEnableDetailedLogging(boolean enableDetailedLogging) { 
            this.enableDetailedLogging = enableDetailedLogging; 
        }
        
        public boolean isEnablePerformanceLogging() { return enablePerformanceLogging; }
        public void setEnablePerformanceLogging(boolean enablePerformanceLogging) { 
            this.enablePerformanceLogging = enablePerformanceLogging; 
        }
    }

    /**
     * External integrations configuration
     */
    public static class Integration {
        private boolean enableWebRTC = true;
        private boolean enableWebSocket = true;
        private boolean enableRedis = true;
        
        @NotNull
        private Duration webSocketHeartbeat = Duration.ofSeconds(30);
        
        @NotNull
        private Duration redisConnectionTimeout = Duration.ofSeconds(5);
        
        @Min(1)
        @Max(100)
        private int maxWebSocketConnections = 1000;

        // Getters and setters
        public boolean isEnableWebRTC() { return enableWebRTC; }
        public void setEnableWebRTC(boolean enableWebRTC) { this.enableWebRTC = enableWebRTC; }
        
        public boolean isEnableWebSocket() { return enableWebSocket; }
        public void setEnableWebSocket(boolean enableWebSocket) { this.enableWebSocket = enableWebSocket; }
        
        public boolean isEnableRedis() { return enableRedis; }
        public void setEnableRedis(boolean enableRedis) { this.enableRedis = enableRedis; }
        
        public Duration getWebSocketHeartbeat() { return webSocketHeartbeat; }
        public void setWebSocketHeartbeat(Duration webSocketHeartbeat) { 
            this.webSocketHeartbeat = webSocketHeartbeat; 
        }
        
        public Duration getRedisConnectionTimeout() { return redisConnectionTimeout; }
        public void setRedisConnectionTimeout(Duration redisConnectionTimeout) { 
            this.redisConnectionTimeout = redisConnectionTimeout; 
        }
        
        public int getMaxWebSocketConnections() { return maxWebSocketConnections; }
        public void setMaxWebSocketConnections(int maxWebSocketConnections) { 
            this.maxWebSocketConnections = maxWebSocketConnections; 
        }
    }
}
