package com.skillswap.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security Headers Configuration
 * Adds essential security headers to all HTTP responses
 */
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityHeadersInterceptor());
    }

    /**
     * Interceptor that adds security headers to all responses
     */
    public static class SecurityHeadersInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            
            // Prevent MIME type sniffing
            response.setHeader("X-Content-Type-Options", "nosniff");
            
            // Prevent page from being displayed in a frame (clickjacking protection)
            response.setHeader("X-Frame-Options", "DENY");
            
            // XSS Protection (legacy browsers)
            response.setHeader("X-XSS-Protection", "1; mode=block");
            
            // HSTS (HTTP Strict Transport Security)
            response.setHeader("Strict-Transport-Security", 
                "max-age=31536000; includeSubDomains; preload");
            
            // Content Security Policy
            response.setHeader("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:; " +
                "font-src 'self' data:; " +
                "connect-src 'self' wss: https:; " +
                "media-src 'self'; " +
                "object-src 'none'; " +
                "frame-ancestors 'none'");
            
            // Referrer Policy
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            
            // Permissions Policy (formerly Feature Policy)
            response.setHeader("Permissions-Policy", 
                "camera=(), microphone=(), geolocation=(), " +
                "payment=(), usb=(), magnetometer=(), accelerometer=()");
            
            // Remove server information
            response.setHeader("Server", "SkillSwap");
            
            // Prevent caching of sensitive endpoints
            if (request.getRequestURI().startsWith("/api/auth/") || 
                request.getRequestURI().startsWith("/api/credits/") ||
                request.getRequestURI().startsWith("/api/admin/")) {
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
            }
            
            return true;
        }
    }
}
