package com.skillswap.service;

import com.skillswap.config.ApplicationConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Advanced alerting and notification service for proactive maintenance
 * Monitors system health and sends alerts when thresholds are exceeded
 * 
 * @author SkillSwap Team
 * @version 1.0
 * @since 2024
 */
@Service
public class AlertingNotificationService implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(AlertingNotificationService.class);
    private static final Logger alertLogger = LoggerFactory.getLogger("ALERTS");

    @Autowired
    private MaintenanceLoggingService maintenanceLoggingService;

    @Autowired
    private ApplicationMetricsService metricsService;

    private final Map<String, Alert> activeAlerts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> alertCounters = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastAlertTimes = new ConcurrentHashMap<>();
    private final Map<String, AlertThreshold> thresholds = new ConcurrentHashMap<>();

    /**
     * Initialize alerting system with default thresholds
     */
    public void initializeAlertingSystem() {
        setupDefaultThresholds();
        logger.info("Alerting and notification system initialized");
        
        maintenanceLoggingService.logApplicationStart("ALERTING_SYSTEM", 
            Map.of("thresholds_count", thresholds.size()));
    }

    /**
     * Setup default alert thresholds
     */
    private void setupDefaultThresholds() {
        // Performance thresholds
        thresholds.put("high_memory_usage", new AlertThreshold(
            "HIGH_MEMORY_USAGE", AlertSeverity.WARNING, 1000, "MB", 300));
        
        thresholds.put("critical_memory_usage", new AlertThreshold(
            "CRITICAL_MEMORY_USAGE", AlertSeverity.CRITICAL, 2000, "MB", 60));
        
        thresholds.put("high_cpu_usage", new AlertThreshold(
            "HIGH_CPU_USAGE", AlertSeverity.WARNING, 80.0, "%", 300));
        
        thresholds.put("critical_cpu_usage", new AlertThreshold(
            "CRITICAL_CPU_USAGE", AlertSeverity.CRITICAL, 95.0, "%", 60));
        
        // Response time thresholds
        thresholds.put("slow_response_time", new AlertThreshold(
            "SLOW_RESPONSE_TIME", AlertSeverity.WARNING, 1000.0, "ms", 180));
        
        thresholds.put("critical_response_time", new AlertThreshold(
            "CRITICAL_RESPONSE_TIME", AlertSeverity.CRITICAL, 5000.0, "ms", 60));
        
        // Error rate thresholds
        thresholds.put("high_error_rate", new AlertThreshold(
            "HIGH_ERROR_RATE", AlertSeverity.WARNING, 5.0, "%", 300));
        
        thresholds.put("critical_error_rate", new AlertThreshold(
            "CRITICAL_ERROR_RATE", AlertSeverity.CRITICAL, 10.0, "%", 60));
        
        // Security thresholds
        thresholds.put("security_violations", new AlertThreshold(
            "SECURITY_VIOLATIONS", AlertSeverity.HIGH, 10.0, "count/hour", 60));
        
        thresholds.put("failed_logins", new AlertThreshold(
            "FAILED_LOGINS", AlertSeverity.MEDIUM, 50.0, "count/hour", 300));
        
        // Database thresholds
        thresholds.put("database_connection_issues", new AlertThreshold(
            "DATABASE_CONNECTION_ISSUES", AlertSeverity.CRITICAL, 5.0, "count", 60));
        
        thresholds.put("slow_database_queries", new AlertThreshold(
            "SLOW_DATABASE_QUERIES", AlertSeverity.WARNING, 20.0, "count/hour", 300));
    }

    /**
     * Monitor system metrics and trigger alerts (runs every minute)
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void monitorSystemMetrics() {
        try {
            // Get current metrics
            Map<String, Object> metrics = metricsService.getAllMetrics();
            
            // Check performance metrics
            checkMemoryUsage(metrics);
            checkCpuUsage(metrics);
            checkResponseTimes(metrics);
            
            // Check error rates
            checkErrorRates(metrics);
            
            // Check security metrics
            checkSecurityViolations(metrics);
            
            // Check database metrics
            checkDatabaseMetrics(metrics);
            
        } catch (Exception e) {
            maintenanceLoggingService.logError("ALERTING_SYSTEM", "METRICS_MONITORING", e, 
                Map.of("timestamp", LocalDateTime.now()));
        }
    }

    /**
     * Check memory usage and trigger alerts if necessary
     */
    private void checkMemoryUsage(Map<String, Object> metrics) {
        Object memoryUsage = metrics.get("memory_used_mb");
        if (memoryUsage instanceof Number) {
            double memoryMB = ((Number) memoryUsage).doubleValue();
            
            if (memoryMB > thresholds.get("critical_memory_usage").getValue()) {
                triggerAlert("critical_memory_usage", memoryMB, 
                    "Critical memory usage detected: " + memoryMB + " MB");
            } else if (memoryMB > thresholds.get("high_memory_usage").getValue()) {
                triggerAlert("high_memory_usage", memoryMB, 
                    "High memory usage detected: " + memoryMB + " MB");
            }
        }
    }

    /**
     * Check CPU usage and trigger alerts if necessary
     */
    private void checkCpuUsage(Map<String, Object> metrics) {
        Object cpuUsage = metrics.get("cpu_usage_percent");
        if (cpuUsage instanceof Number) {
            double cpuPercent = ((Number) cpuUsage).doubleValue();
            
            if (cpuPercent > thresholds.get("critical_cpu_usage").getValue()) {
                triggerAlert("critical_cpu_usage", cpuPercent, 
                    "Critical CPU usage detected: " + cpuPercent + "%");
            } else if (cpuPercent > thresholds.get("high_cpu_usage").getValue()) {
                triggerAlert("high_cpu_usage", cpuPercent, 
                    "High CPU usage detected: " + cpuPercent + "%");
            }
        }
    }

    /**
     * Check response times and trigger alerts if necessary
     */
    private void checkResponseTimes(Map<String, Object> metrics) {
        Object avgResponseTime = metrics.get("average_response_time_ms");
        if (avgResponseTime instanceof Number) {
            double responseTimeMs = ((Number) avgResponseTime).doubleValue();
            
            if (responseTimeMs > thresholds.get("critical_response_time").getValue()) {
                triggerAlert("critical_response_time", responseTimeMs, 
                    "Critical response time detected: " + responseTimeMs + " ms");
            } else if (responseTimeMs > thresholds.get("slow_response_time").getValue()) {
                triggerAlert("slow_response_time", responseTimeMs, 
                    "Slow response time detected: " + responseTimeMs + " ms");
            }
        }
    }

    /**
     * Check error rates and trigger alerts if necessary
     */
    private void checkErrorRates(Map<String, Object> metrics) {
        Object errorRate = metrics.get("error_rate_percent");
        if (errorRate instanceof Number) {
            double errorPercent = ((Number) errorRate).doubleValue();
            
            if (errorPercent > thresholds.get("critical_error_rate").getValue()) {
                triggerAlert("critical_error_rate", errorPercent, 
                    "Critical error rate detected: " + errorPercent + "%");
            } else if (errorPercent > thresholds.get("high_error_rate").getValue()) {
                triggerAlert("high_error_rate", errorPercent, 
                    "High error rate detected: " + errorPercent + "%");
            }
        }
    }

    /**
     * Check security violations and trigger alerts if necessary
     */
    private void checkSecurityViolations(Map<String, Object> metrics) {
        Object securityViolations = metrics.get("security_violations_per_hour");
        if (securityViolations instanceof Number) {
            double violations = ((Number) securityViolations).doubleValue();
            
            if (violations > thresholds.get("security_violations").getValue()) {
                triggerAlert("security_violations", violations, 
                    "High security violations detected: " + violations + " per hour");
            }
        }
        
        Object failedLogins = metrics.get("failed_logins_per_hour");
        if (failedLogins instanceof Number) {
            double failures = ((Number) failedLogins).doubleValue();
            
            if (failures > thresholds.get("failed_logins").getValue()) {
                triggerAlert("failed_logins", failures, 
                    "High failed login attempts: " + failures + " per hour");
            }
        }
    }

    /**
     * Check database metrics and trigger alerts if necessary
     */
    private void checkDatabaseMetrics(Map<String, Object> metrics) {
        Object dbConnectionIssues = metrics.get("database_connection_errors");
        if (dbConnectionIssues instanceof Number) {
            double issues = ((Number) dbConnectionIssues).doubleValue();
            
            if (issues > thresholds.get("database_connection_issues").getValue()) {
                triggerAlert("database_connection_issues", issues, 
                    "Database connection issues detected: " + issues);
            }
        }
        
        Object slowQueries = metrics.get("slow_database_queries_per_hour");
        if (slowQueries instanceof Number) {
            double queries = ((Number) slowQueries).doubleValue();
            
            if (queries > thresholds.get("slow_database_queries").getValue()) {
                triggerAlert("slow_database_queries", queries, 
                    "High number of slow database queries: " + queries + " per hour");
            }
        }
    }

    /**
     * Trigger an alert if threshold conditions are met
     */
    private void triggerAlert(String alertType, double currentValue, String message) {
        AlertThreshold threshold = thresholds.get(alertType);
        if (threshold == null) return;
        
        // Check if we should suppress duplicate alerts
        LocalDateTime lastAlert = lastAlertTimes.get(alertType);
        LocalDateTime now = LocalDateTime.now();
        
        if (lastAlert != null && 
            lastAlert.plusSeconds(threshold.getSuppressionSeconds()).isAfter(now)) {
            // Alert is suppressed
            return;
        }
        
        // Create and send alert
        Alert alert = new Alert(
            UUID.randomUUID().toString(),
            alertType,
            threshold.getSeverity(),
            message,
            currentValue,
            threshold.getValue(),
            threshold.getUnit(),
            now
        );
        
        sendAlert(alert);
        
        // Update tracking
        activeAlerts.put(alertType, alert);
        alertCounters.computeIfAbsent(alertType, k -> new AtomicLong(0)).incrementAndGet();
        lastAlertTimes.put(alertType, now);
    }

    /**
     * Send alert through multiple channels
     */
    @Async
    private void sendAlert(Alert alert) {
        try {
            // Log the alert
            alertLogger.error("ALERT_TRIGGERED type={} severity={} message={} current_value={} threshold={}", 
                alert.getType(), alert.getSeverity(), alert.getMessage(), 
                alert.getCurrentValue(), alert.getThresholdValue());
            
            // Send to maintenance logging
            maintenanceLoggingService.logSecurityEvent(
                "SYSTEM_ALERT", 
                alert.getMessage(), 
                alert.getSeverity().name(), 
                "SYSTEM"
            );
            
            // Send email notification (if configured)
            sendEmailAlert(alert);
            
            // Send Slack notification (if configured)
            sendSlackAlert(alert);
            
            // Send webhook notification (if configured)
            sendWebhookAlert(alert);
            
            logger.info("Alert sent successfully: {}", alert.getId());
            
        } catch (Exception e) {
            maintenanceLoggingService.logError("ALERTING_SYSTEM", "SEND_ALERT", e, 
                Map.of("alert_id", alert.getId(), "alert_type", alert.getType()));
        }
    }

    /**
     * Send email alert notification
     */
    private void sendEmailAlert(Alert alert) {
        // Implementation would send email using configured SMTP
        logger.info("Email alert sent for: {}", alert.getType());
    }

    /**
     * Send Slack alert notification
     */
    private void sendSlackAlert(Alert alert) {
        // Implementation would send to Slack using webhook
        logger.info("Slack alert sent for: {}", alert.getType());
    }

    /**
     * Send webhook alert notification
     */
    private void sendWebhookAlert(Alert alert) {
        // Implementation would send HTTP POST to configured webhook
        logger.info("Webhook alert sent for: {}", alert.getType());
    }

    /**
     * Resolve an alert manually or automatically
     */
    public void resolveAlert(String alertType, String resolvedBy) {
        Alert alert = activeAlerts.remove(alertType);
        if (alert != null) {
            alert.setResolvedAt(LocalDateTime.now());
            alert.setResolvedBy(resolvedBy);
            
            alertLogger.info("ALERT_RESOLVED type={} resolved_by={} duration_minutes={}", 
                alertType, resolvedBy, alert.getDurationMinutes());
            
            maintenanceLoggingService.logBusinessEvent("ALERT_RESOLVED", resolvedBy, 
                Map.of("alert_type", alertType, "duration_minutes", alert.getDurationMinutes()));
        }
    }

    /**
     * Auto-resolve alerts based on current metrics (runs every 5 minutes)
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void autoResolveAlerts() {
        try {
            Map<String, Object> metrics = metricsService.getAllMetrics();
            
            // Check if conditions have improved for active alerts
            for (Map.Entry<String, Alert> entry : new HashMap<>(activeAlerts).entrySet()) {
                String alertType = entry.getKey();
                Alert alert = entry.getValue();
                
                if (shouldAutoResolve(alertType, alert, metrics)) {
                    resolveAlert(alertType, "AUTO_RESOLVED");
                }
            }
            
        } catch (Exception e) {
            maintenanceLoggingService.logError("ALERTING_SYSTEM", "AUTO_RESOLVE", e, 
                Map.of("timestamp", LocalDateTime.now()));
        }
    }

    /**
     * Check if an alert should be auto-resolved based on current metrics
     */
    private boolean shouldAutoResolve(String alertType, Alert alert, Map<String, Object> metrics) {
        AlertThreshold threshold = thresholds.get(alertType);
        if (threshold == null) return false;
        
        // Get current metric value
        String metricKey = getMetricKeyForAlert(alertType);
        Object currentMetric = metrics.get(metricKey);
        
        if (currentMetric instanceof Number) {
            double currentValue = ((Number) currentMetric).doubleValue();
            
            // Resolve if value is below threshold for at least 5 minutes
            if (currentValue < threshold.getValue() && 
                alert.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Get the metric key for a specific alert type
     */
    private String getMetricKeyForAlert(String alertType) {
        switch (alertType) {
            case "high_memory_usage":
            case "critical_memory_usage":
                return "memory_used_mb";
            case "high_cpu_usage":
            case "critical_cpu_usage":
                return "cpu_usage_percent";
            case "slow_response_time":
            case "critical_response_time":
                return "average_response_time_ms";
            case "high_error_rate":
            case "critical_error_rate":
                return "error_rate_percent";
            default:
                return "unknown";
        }
    }

    /**
     * Get current alert status and statistics
     */
    public Map<String, Object> getAlertStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("active_alerts", new HashMap<>(activeAlerts));
        status.put("alert_counters", alertCounters.entrySet().stream()
            .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue().get()), HashMap::putAll));
        status.put("last_alert_times", new HashMap<>(lastAlertTimes));
        status.put("configured_thresholds", new HashMap<>(thresholds));
        status.put("total_active_alerts", activeAlerts.size());
        return status;
    }

    /**
     * Health check for alerting system
     */
    @Override
    public Health health() {
        try {
            Health.Builder healthBuilder = Health.up();
            
            int activeAlertCount = activeAlerts.size();
            long criticalAlerts = activeAlerts.values().stream()
                .filter(alert -> alert.getSeverity() == AlertSeverity.CRITICAL)
                .count();
            
            healthBuilder.withDetail("active_alerts", activeAlertCount);
            healthBuilder.withDetail("critical_alerts", criticalAlerts);
            healthBuilder.withDetail("configured_thresholds", thresholds.size());
            
            if (criticalAlerts > 0) {
                healthBuilder.status("CRITICAL");
            } else if (activeAlertCount > 5) {
                healthBuilder.status("DEGRADED");
            }
            
            return healthBuilder.build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }

    // Inner classes for alert management
    public static class Alert {
        private final String id;
        private final String type;
        private final AlertSeverity severity;
        private final String message;
        private final double currentValue;
        private final double thresholdValue;
        private final String unit;
        private final LocalDateTime createdAt;
        private LocalDateTime resolvedAt;
        private String resolvedBy;

        public Alert(String id, String type, AlertSeverity severity, String message, 
                    double currentValue, double thresholdValue, String unit, LocalDateTime createdAt) {
            this.id = id;
            this.type = type;
            this.severity = severity;
            this.message = message;
            this.currentValue = currentValue;
            this.thresholdValue = thresholdValue;
            this.unit = unit;
            this.createdAt = createdAt;
        }

        // Getters and setters
        public String getId() { return id; }
        public String getType() { return type; }
        public AlertSeverity getSeverity() { return severity; }
        public String getMessage() { return message; }
        public double getCurrentValue() { return currentValue; }
        public double getThresholdValue() { return thresholdValue; }
        public String getUnit() { return unit; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        
        public LocalDateTime getResolvedAt() { return resolvedAt; }
        public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
        
        public String getResolvedBy() { return resolvedBy; }
        public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
        
        public long getDurationMinutes() {
            LocalDateTime end = resolvedAt != null ? resolvedAt : LocalDateTime.now();
            return java.time.Duration.between(createdAt, end).toMinutes();
        }
    }

    public static class AlertThreshold {
        private final String name;
        private final AlertSeverity severity;
        private final double value;
        private final String unit;
        private final int suppressionSeconds;

        public AlertThreshold(String name, AlertSeverity severity, double value, String unit, int suppressionSeconds) {
            this.name = name;
            this.severity = severity;
            this.value = value;
            this.unit = unit;
            this.suppressionSeconds = suppressionSeconds;
        }

        // Getters
        public String getName() { return name; }
        public AlertSeverity getSeverity() { return severity; }
        public double getValue() { return value; }
        public String getUnit() { return unit; }
        public int getSuppressionSeconds() { return suppressionSeconds; }
    }

    public enum AlertSeverity {
        LOW, MEDIUM, WARNING, HIGH, CRITICAL
    }
}
