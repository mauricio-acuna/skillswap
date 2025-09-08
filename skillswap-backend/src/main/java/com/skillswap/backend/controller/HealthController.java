package com.skillswap.backend.controller;

import com.skillswap.backend.monitoring.ApplicationMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check and System Monitoring Controller
 * Provides endpoints for system health monitoring and metrics
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private ApplicationMetricsService metricsService;

    /**
     * Basic health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "skillswap-backend");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }

    /**
     * Detailed system status with metrics
     */
    @GetMapping("/health/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Basic info
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "skillswap-backend");
        health.put("version", "1.0.0");
        
        // System metrics
        ApplicationMetricsService.SystemHealthMetrics metrics = metricsService.getSystemHealth();
        Map<String, Object> systemMetrics = new HashMap<>();
        systemMetrics.put("activeVideoSessions", metrics.getActiveVideoSessions());
        systemMetrics.put("activeWebSocketConnections", metrics.getActiveWebSocketConnections());
        systemMetrics.put("totalActiveUsers", metrics.getTotalUsers());
        systemMetrics.put("totalCreditBalance", metrics.getTotalCreditBalance());
        systemMetrics.put("totalRegistrations", metrics.getTotalRegistrations());
        systemMetrics.put("totalSecurityViolations", metrics.getTotalSecurityViolations());
        
        health.put("metrics", systemMetrics);
        
        // System resources
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> resources = new HashMap<>();
        resources.put("totalMemoryMB", runtime.totalMemory() / (1024 * 1024));
        resources.put("freeMemoryMB", runtime.freeMemory() / (1024 * 1024));
        resources.put("usedMemoryMB", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024));
        resources.put("maxMemoryMB", runtime.maxMemory() / (1024 * 1024));
        resources.put("availableProcessors", runtime.availableProcessors());
        
        health.put("resources", resources);
        
        // Security status
        Map<String, Object> security = new HashMap<>();
        security.put("securityViolations", metrics.getTotalSecurityViolations());
        security.put("securityStatus", metrics.getTotalSecurityViolations() < 100 ? "GOOD" : "ALERT");
        
        health.put("security", security);
        
        return ResponseEntity.ok(health);
    }

    /**
     * Simple status endpoint for load balancers
     */
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Readiness probe for Kubernetes
     */
    @GetMapping("/health/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        Map<String, Object> readiness = new HashMap<>();
        readiness.put("status", "READY");
        readiness.put("timestamp", LocalDateTime.now());
        
        // Check if system is ready to accept traffic
        ApplicationMetricsService.SystemHealthMetrics metrics = metricsService.getSystemHealth();
        boolean isReady = metrics.getTotalSecurityViolations() < 1000; // Threshold for readiness
        
        if (isReady) {
            readiness.put("ready", true);
            return ResponseEntity.ok(readiness);
        } else {
            readiness.put("ready", false);
            readiness.put("reason", "Too many security violations");
            return ResponseEntity.status(503).body(readiness);
        }
    }

    /**
     * Liveness probe for Kubernetes
     */
    @GetMapping("/health/live")
    public ResponseEntity<Map<String, Object>> live() {
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("status", "ALIVE");
        liveness.put("timestamp", LocalDateTime.now());
        
        // Basic liveness check - if we can respond, we're alive
        liveness.put("alive", true);
        
        return ResponseEntity.ok(liveness);
    }

    /**
     * Performance metrics endpoint
     */
    @GetMapping("/metrics/performance")
    public ResponseEntity<Map<String, Object>> performanceMetrics() {
        Map<String, Object> performance = new HashMap<>();
        
        // Memory metrics
        Runtime runtime = Runtime.getRuntime();
        performance.put("memoryUsagePercent", 
            ((double)(runtime.totalMemory() - runtime.freeMemory()) / runtime.maxMemory()) * 100);
        
        // System load (simplified)
        performance.put("availableProcessors", runtime.availableProcessors());
        
        // Application metrics
        ApplicationMetricsService.SystemHealthMetrics metrics = metricsService.getSystemHealth();
        performance.put("activeConnections", 
            metrics.getActiveVideoSessions() + metrics.getActiveWebSocketConnections());
        
        performance.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(performance);
    }

    /**
     * Security metrics endpoint
     */
    @GetMapping("/metrics/security")
    public ResponseEntity<Map<String, Object>> securityMetrics() {
        Map<String, Object> security = new HashMap<>();
        
        ApplicationMetricsService.SystemHealthMetrics metrics = metricsService.getSystemHealth();
        security.put("totalSecurityViolations", metrics.getTotalSecurityViolations());
        security.put("securityLevel", 
            metrics.getTotalSecurityViolations() < 10 ? "LOW" :
            metrics.getTotalSecurityViolations() < 100 ? "MEDIUM" : "HIGH");
        
        security.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(security);
    }
}
