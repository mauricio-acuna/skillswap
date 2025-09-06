package com.skillswap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller for SkillSwap Backend
 * Provides basic health and status endpoints
 * 
 * @author SkillSwap Team
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Health check and status endpoints")
public class HealthController {

    @Value("${spring.application.name:skillswap-backend}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Operation(
        summary = "Health Check",
        description = "Returns the health status of the application"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy"),
        @ApiResponse(responseCode = "503", description = "Application is unhealthy")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", applicationName);
        response.put("profile", activeProfile);
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "SkillSwap Backend is running successfully!");
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Application Info",
        description = "Returns basic information about the application"
    )
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", applicationName);
        response.put("version", "1.0.0");
        response.put("description", "P2P Skill Exchange Platform Backend");
        response.put("profile", activeProfile);
        response.put("timestamp", LocalDateTime.now());
        
        Map<String, Object> features = new HashMap<>();
        features.put("authentication", "JWT + OAuth2");
        features.put("database", "PostgreSQL + H2");
        features.put("cache", "Redis");
        features.put("documentation", "OpenAPI/Swagger");
        
        response.put("features", features);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Ready Check",
        description = "Returns readiness status for load balancer"
    )
    @GetMapping("/ready")
    public ResponseEntity<Map<String, String>> ready() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "READY");
        response.put("message", "Application is ready to receive traffic");
        
        return ResponseEntity.ok(response);
    }
}
