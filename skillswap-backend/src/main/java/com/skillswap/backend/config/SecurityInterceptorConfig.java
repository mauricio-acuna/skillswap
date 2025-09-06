package com.skillswap.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.skillswap.backend.security.RateLimitingService;
import com.skillswap.backend.security.SecurityInterceptor;

/**
 * Configuration for security interceptors
 * Registers security interceptor and rate limiting interceptor
 */
@Configuration
public class SecurityInterceptorConfig implements WebMvcConfigurer {
    
    @Autowired
    private SecurityInterceptor securityInterceptor;
    
    @Autowired
    private RateLimitingService rateLimitingService;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add rate limiting interceptor first (runs before security interceptor)
        registry.addInterceptor(rateLimitingService)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/health",
                    "/api/status",
                    "/api/actuator/**",
                    "/error"
                )
                .order(1);
        
        // Add security interceptor second
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/health",
                    "/api/status", 
                    "/api/actuator/**",
                    "/error"
                )
                .order(2);
    }
}
