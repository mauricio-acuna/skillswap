package com.skillswap.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiting Service
 * Implements token bucket algorithm for rate limiting
 */
@Component
public class RateLimitingService implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingService.class);

    // Rate limit configurations
    private static final int DEFAULT_REQUESTS_PER_MINUTE = 60;
    private static final int AUTH_REQUESTS_PER_MINUTE = 5;
    private static final int CREDIT_TRANSFER_PER_HOUR = 10;
    private static final int VIDEO_SESSION_PER_HOUR = 20;

    // Token buckets for different endpoints
    private final ConcurrentHashMap<String, TokenBucket> ipBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TokenBucket> userBuckets = new ConcurrentHashMap<>();
    
    // Specific buckets for sensitive operations
    private final ConcurrentHashMap<String, TokenBucket> authBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TokenBucket> creditBuckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIpAddress(request);
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip rate limiting for health checks and static resources
        if (isExcludedPath(uri)) {
            return true;
        }

        // Check rate limits based on endpoint type
        if (!checkRateLimit(clientIp, uri, method, request)) {
            logger.warn("Rate limit exceeded for IP: {} on endpoint: {} {}", clientIp, method, uri);
            response.setStatus(429); // Too Many Requests
            response.setHeader("Retry-After", "60");
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
            return false;
        }

        return true;
    }

    private boolean checkRateLimit(String clientIp, String uri, String method, HttpServletRequest request) {
        // Get or create IP-based bucket
        TokenBucket ipBucket = ipBuckets.computeIfAbsent(clientIp, 
            k -> new TokenBucket(DEFAULT_REQUESTS_PER_MINUTE, 60));
        
        // Check specific endpoint limits
        if (uri.startsWith("/api/auth/")) {
            TokenBucket authBucket = authBuckets.computeIfAbsent(clientIp,
                k -> new TokenBucket(AUTH_REQUESTS_PER_MINUTE, 60));
            return authBucket.consume() && ipBucket.consume();
        }
        
        if (uri.startsWith("/api/credits/transfer")) {
            String userId = extractUserId(request);
            if (userId != null) {
                TokenBucket creditBucket = creditBuckets.computeIfAbsent(userId,
                    k -> new TokenBucket(CREDIT_TRANSFER_PER_HOUR, 3600));
                return creditBucket.consume() && ipBucket.consume();
            }
        }
        
        if (uri.startsWith("/api/video-sessions/") && "POST".equals(method)) {
            String userId = extractUserId(request);
            if (userId != null) {
                TokenBucket videoBucket = userBuckets.computeIfAbsent(userId,
                    k -> new TokenBucket(VIDEO_SESSION_PER_HOUR, 3600));
                return videoBucket.consume() && ipBucket.consume();
            }
        }

        // Default rate limit
        return ipBucket.consume();
    }

    private String extractUserId(HttpServletRequest request) {
        // Extract user ID from JWT token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // This would need JwtTokenProvider injection
                // For now, return null to skip user-specific rate limiting
                return null;
            } catch (Exception e) {
                logger.debug("Could not extract user ID from token", e);
            }
        }
        return null;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Public method to check if a request is allowed (for use by other components)
     */
    public boolean isRequestAllowed(String endpoint, String clientIp) {
        if (isExcludedPath(endpoint)) {
            return true;
        }
        
        TokenBucket ipBucket = ipBuckets.computeIfAbsent(clientIp, 
            k -> new TokenBucket(DEFAULT_REQUESTS_PER_MINUTE, 60));
        
        // Check specific endpoint limits
        if (endpoint.startsWith("/api/auth/")) {
            TokenBucket authBucket = authBuckets.computeIfAbsent(clientIp,
                k -> new TokenBucket(AUTH_REQUESTS_PER_MINUTE, 60));
            return authBucket.consume() && ipBucket.consume();
        }
        
        // Default rate limit
        return ipBucket.consume();
    }

    private boolean isExcludedPath(String uri) {
        return uri.startsWith("/actuator/health") ||
               uri.startsWith("/favicon.ico") ||
               uri.startsWith("/swagger-ui/") ||
               uri.startsWith("/v3/api-docs/") ||
               uri.equals("/error");
    }

    /**
     * Token Bucket implementation for rate limiting
     */
    private static class TokenBucket {
        private final int capacity;
        private final int refillRate;
        private final AtomicInteger tokens;
        private volatile long lastRefill;

        public TokenBucket(int capacity, int refillIntervalSeconds) {
            this.capacity = capacity;
            this.refillRate = capacity / Math.max(1, refillIntervalSeconds / 60); // tokens per minute
            this.tokens = new AtomicInteger(capacity);
            this.lastRefill = System.currentTimeMillis();
        }

        public boolean consume() {
            refillTokens();
            return tokens.getAndDecrement() > 0;
        }

        private void refillTokens() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefill;
            
            if (timePassed > 60000) { // Refill every minute
                int tokensToAdd = (int) (timePassed / 60000) * refillRate;
                if (tokensToAdd > 0) {
                    tokens.set(Math.min(capacity, tokens.get() + tokensToAdd));
                    lastRefill = now;
                }
            }
        }
    }

    // Public methods for manual rate limit checks
    public boolean checkAuthRateLimit(String identifier) {
        TokenBucket bucket = authBuckets.computeIfAbsent(identifier,
            k -> new TokenBucket(AUTH_REQUESTS_PER_MINUTE, 60));
        return bucket.consume();
    }

    public boolean checkCreditTransferLimit(String userId) {
        TokenBucket bucket = creditBuckets.computeIfAbsent(userId,
            k -> new TokenBucket(CREDIT_TRANSFER_PER_HOUR, 3600));
        return bucket.consume();
    }

    public void blockIp(String ip, int minutes) {
        // Create a bucket with 0 capacity to effectively block the IP
        TokenBucket blockBucket = new TokenBucket(0, minutes * 60);
        ipBuckets.put(ip, blockBucket);
        logger.warn("IP {} blocked for {} minutes", ip, minutes);
    }
}
