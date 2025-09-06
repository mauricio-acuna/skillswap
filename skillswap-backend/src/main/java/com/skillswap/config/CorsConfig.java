package com.skillswap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for SkillSwap Backend
 * Handles Cross-Origin Resource Sharing for mobile and web clients
 * 
 * @author SkillSwap Team
 */
@Configuration
public class CorsConfig {

    @Value("${skillswap.cors.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;

    @Value("${skillswap.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${skillswap.cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${skillswap.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${skillswap.cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        
        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        
        // Set allowed headers
        if (allowedHeaders.length == 1 && "*".equals(allowedHeaders[0])) {
            configuration.addAllowedHeader("*");
        } else {
            configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        }
        
        // Set whether credentials are supported
        configuration.setAllowCredentials(allowCredentials);
        
        // Set preflight response cache time
        configuration.setMaxAge(maxAge);
        
        // Expose headers that clients can access
        configuration.setExposedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "X-Total-Count",
            "X-Page-Count"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
