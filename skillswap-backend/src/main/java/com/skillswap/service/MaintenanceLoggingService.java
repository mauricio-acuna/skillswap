package com.skillswap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Advanced logging service for maintenance and troubleshooting
 * Provides structured logging with context, metrics, and health monitoring
 * 
 * @author SkillSwap Team
 * @version 1.0
 * @since 2024
 */
@Service
public class MaintenanceLoggingService implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceLoggingService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");
    private static final Logger errorLogger = LoggerFactory.getLogger("ERROR");
    private static final Logger businessLogger = LoggerFactory.getLogger("BUSINESS");

    private final AtomicLong logEventCounter = new AtomicLong(0);
    private final AtomicLong errorCounter = new AtomicLong(0);
    private final AtomicLong warningCounter = new AtomicLong(0);
    private final Map<String, AtomicLong> operationCounters = new ConcurrentHashMap<>();
    private final Map<String, Long> lastOperationTimestamps = new ConcurrentHashMap<>();

    /**
     * Log application startup and initialization events
     */
    public void logApplicationStart(String component, Map<String, Object> metadata) {
        setLoggingContext("SYSTEM", "APPLICATION_START", "INFO");
        try {
            logger.info("Application component started: {} with metadata: {}", component, metadata);
            businessLogger.info("COMPONENT_STARTED component={} timestamp={}", 
                component, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            incrementOperationCounter("application_starts");
        } finally {
            clearLoggingContext();
        }
    }

    /**
     * Log user operations for audit and troubleshooting
     */
    public void logUserOperation(String userId, String operation, String details, boolean success) {
        setLoggingContext(userId, operation, success ? "INFO" : "WARN");
        try {
            if (success) {
                auditLogger.info("USER_OPERATION userId={} operation={} details={} success=true", 
                    userId, operation, details);
            } else {
                auditLogger.warn("USER_OPERATION userId={} operation={} details={} success=false", 
                    userId, operation, details);
                incrementWarningCounter();
            }
            incrementOperationCounter("user_operations");
            updateLastOperationTimestamp("user_operation");
        } finally {
            clearLoggingContext();
        }
    }

    /**
     * Log security events for maintenance monitoring
     */
    public void logSecurityEvent(String eventType, String details, String severity, String sourceIp) {
        setLoggingContext("SECURITY", eventType, severity);
        MDC.put("sourceIp", sourceIp);
        try {
            switch (severity.toUpperCase()) {
                case "HIGH":
                    auditLogger.error("SECURITY_EVENT type={} details={} severity={} sourceIp={}", 
                        eventType, details, severity, sourceIp);
                    incrementErrorCounter();
                    break;
                case "MEDIUM":
                    auditLogger.warn("SECURITY_EVENT type={} details={} severity={} sourceIp={}", 
                        eventType, details, severity, sourceIp);
                    incrementWarningCounter();
                    break;
                default:
                    auditLogger.info("SECURITY_EVENT type={} details={} severity={} sourceIp={}", 
                        eventType, details, severity, sourceIp);
            }
            incrementOperationCounter("security_events");
            updateLastOperationTimestamp("security_event");
        } finally {
            clearLoggingContext();
        }
    }

    /**
     * Log performance metrics for maintenance analysis
     */
    public void logPerformanceMetric(String operation, long durationMs, Map<String, Object> metrics) {
        setLoggingContext("PERFORMANCE", operation, "INFO");
        try {
            performanceLogger.info("PERFORMANCE_METRIC operation={} duration_ms={} metrics={}", 
                operation, durationMs, metrics);
            
            // Log slow operations
            if (durationMs > 1000) {
                performanceLogger.warn("SLOW_OPERATION operation={} duration_ms={} threshold_exceeded=true", 
                    operation, durationMs);
                incrementWarningCounter();
            }
            
            incrementOperationCounter("performance_metrics");
            updateLastOperationTimestamp("performance_metric");
        } finally {
            clearLoggingContext();
        }
    }

    /**
     * Log business events for analytics and troubleshooting
     */
    public void logBusinessEvent(String eventType, String userId, Map<String, Object> eventData) {
        setLoggingContext(userId, eventType, "INFO");
        try {
            businessLogger.info("BUSINESS_EVENT type={} userId={} data={} timestamp={}", 
                eventType, userId, eventData, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            incrementOperationCounter("business_events");
            updateLastOperationTimestamp("business_event");
        } finally {
            clearLoggingContext();
        }
    }

    /**
     * Log errors with full context for debugging
     */
    public void logError(String component, String operation, Exception error, Map<String, Object> context) {
        setLoggingContext("ERROR", component, "ERROR");
        try {
            errorLogger.error("APPLICATION_ERROR component={} operation={} error_class={} message={} context={}", 
                component, operation, error.getClass().getSimpleName(), error.getMessage(), context, error);
            incrementErrorCounter();
            incrementOperationCounter("application_errors");
            updateLastOperationTimestamp("error");
        } finally {
            clearLoggingContext();
        }
    }

    /**
     * Log database operations for performance monitoring
     */
    public void logDatabaseOperation(String operation, String table, long durationMs, int recordsAffected) {
        setLoggingContext("DATABASE", operation, "INFO");
        try {
            performanceLogger.info("DATABASE_OPERATION operation={} table={} duration_ms={} records_affected={}", 
                operation, table, durationMs, recordsAffected);
            
            // Log slow database operations
            if (durationMs > 500) {
                performanceLogger.warn("SLOW_DATABASE_OPERATION operation={} table={} duration_ms={} threshold_exceeded=true", 
                    operation, table, durationMs);
                incrementWarningCounter();
            }
            
            incrementOperationCounter("database_operations");
            updateLastOperationTimestamp("database_operation");
        } finally {
            clearLoggingContext();
        }
    }

    /**
     * Log API request/response for debugging
     */
    public void logApiRequest(String method, String endpoint, String userId, long durationMs, int statusCode) {
        setLoggingContext(userId, "API_REQUEST", statusCode >= 400 ? "WARN" : "INFO");
        try {
            if (statusCode >= 400) {
                logger.warn("API_REQUEST method={} endpoint={} userId={} duration_ms={} status_code={}", 
                    method, endpoint, userId, durationMs, statusCode);
                incrementWarningCounter();
            } else {
                logger.info("API_REQUEST method={} endpoint={} userId={} duration_ms={} status_code={}", 
                    method, endpoint, userId, durationMs, statusCode);
            }
            incrementOperationCounter("api_requests");
            updateLastOperationTimestamp("api_request");
        } finally {
            clearLoggingContext();
        }
    }

    /**
     * Log system resource usage
     */
    public void logSystemMetrics(long memoryUsedMB, double cpuUsagePercent, int activeConnections) {
        setLoggingContext("SYSTEM", "METRICS", "INFO");
        try {
            performanceLogger.info("SYSTEM_METRICS memory_used_mb={} cpu_usage_percent={} active_connections={}", 
                memoryUsedMB, cpuUsagePercent, activeConnections);
            
            // Log resource warnings
            if (memoryUsedMB > 1000) {
                performanceLogger.warn("HIGH_MEMORY_USAGE memory_used_mb={} threshold_exceeded=true", memoryUsedMB);
                incrementWarningCounter();
            }
            
            if (cpuUsagePercent > 80.0) {
                performanceLogger.warn("HIGH_CPU_USAGE cpu_usage_percent={} threshold_exceeded=true", cpuUsagePercent);
                incrementWarningCounter();
            }
            
            incrementOperationCounter("system_metrics");
            updateLastOperationTimestamp("system_metrics");
        } finally {
            clearLoggingContext();
        }
    }

    /**
     * Get logging statistics for monitoring
     */
    public Map<String, Object> getLoggingStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("total_log_events", logEventCounter.get());
        stats.put("total_errors", errorCounter.get());
        stats.put("total_warnings", warningCounter.get());
        stats.put("operation_counters", new ConcurrentHashMap<>(operationCounters));
        stats.put("last_operation_timestamps", new ConcurrentHashMap<>(lastOperationTimestamps));
        stats.put("health_status", getHealthStatus());
        return stats;
    }

    /**
     * Health check for logging service
     */
    @Override
    public Health health() {
        try {
            Health.Builder healthBuilder = Health.up();
            
            // Check error rate
            long totalEvents = logEventCounter.get();
            long errors = errorCounter.get();
            double errorRate = totalEvents > 0 ? (double) errors / totalEvents : 0.0;
            
            healthBuilder.withDetail("total_events", totalEvents);
            healthBuilder.withDetail("error_count", errors);
            healthBuilder.withDetail("warning_count", warningCounter.get());
            healthBuilder.withDetail("error_rate", String.format("%.2f%%", errorRate * 100));
            
            if (errorRate > 0.1) { // More than 10% error rate
                healthBuilder.status("DEGRADED");
                healthBuilder.withDetail("status", "High error rate detected");
            }
            
            return healthBuilder.build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }

    // Private helper methods
    private void setLoggingContext(String userId, String operation, String level) {
        MDC.put("userId", userId);
        MDC.put("operation", operation);
        MDC.put("level", level);
        MDC.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        MDC.put("thread", Thread.currentThread().getName());
        logEventCounter.incrementAndGet();
    }

    private void clearLoggingContext() {
        MDC.clear();
    }

    private void incrementOperationCounter(String operation) {
        operationCounters.computeIfAbsent(operation, k -> new AtomicLong(0)).incrementAndGet();
    }

    private void updateLastOperationTimestamp(String operation) {
        lastOperationTimestamps.put(operation, System.currentTimeMillis());
    }

    private void incrementErrorCounter() {
        errorCounter.incrementAndGet();
    }

    private void incrementWarningCounter() {
        warningCounter.incrementAndGet();
    }

    private String getHealthStatus() {
        long totalEvents = logEventCounter.get();
        long errors = errorCounter.get();
        double errorRate = totalEvents > 0 ? (double) errors / totalEvents : 0.0;
        
        if (errorRate > 0.1) {
            return "DEGRADED";
        } else if (errorRate > 0.05) {
            return "WARNING";
        } else {
            return "HEALTHY";
        }
    }
}
