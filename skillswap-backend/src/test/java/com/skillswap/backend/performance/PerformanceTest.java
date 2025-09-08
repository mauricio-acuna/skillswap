package com.skillswap.backend.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Performance tests for SkillSwap Backend
 * Tests response times, throughput, and system behavior under load
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("SkillSwap Performance Tests")
class PerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    private static final int CONCURRENT_USERS = 50;
    private static final int REQUESTS_PER_USER = 10;
    private static final long MAX_RESPONSE_TIME_MS = 1000; // 1 second
    private static final long TARGET_RESPONSE_TIME_MS = 200; // 200ms target

    @Test
    @DisplayName("Health Check Response Time")
    void healthCheckResponseTime() throws Exception {
        StopWatch stopWatch = new StopWatch();
        
        // Warm up
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/health"));
        }
        
        // Actual test
        stopWatch.start();
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
        stopWatch.stop();
        
        long responseTime = stopWatch.getLastTaskTimeMillis();
        
        assertThat(responseTime)
            .describedAs("Health check should respond within %d ms", TARGET_RESPONSE_TIME_MS)
            .isLessThan(TARGET_RESPONSE_TIME_MS);
    }

    @Test
    @DisplayName("Concurrent Health Check Requests")
    void concurrentHealthCheckRequests() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<CompletableFuture<Long>> futures = new ArrayList<>();
        
        // Submit concurrent requests
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                try {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    
                    mockMvc.perform(get("/api/health"))
                            .andExpect(status().isOk());
                    
                    stopWatch.stop();
                    return stopWatch.getLastTaskTimeMillis();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);
            
            futures.add(future);
        }
        
        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // Analyze results
        List<Long> responseTimes = new ArrayList<>();
        for (CompletableFuture<Long> future : futures) {
            responseTimes.add(future.join());
        }
        
        double averageResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);
        
        long maxResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0);
        
        long minResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .min()
            .orElse(0);
        
        // Assertions
        assertThat(averageResponseTime)
            .describedAs("Average response time should be under %d ms", MAX_RESPONSE_TIME_MS)
            .isLessThan(MAX_RESPONSE_TIME_MS);
        
        assertThat(maxResponseTime)
            .describedAs("Maximum response time should be under %d ms", MAX_RESPONSE_TIME_MS * 2)
            .isLessThan(MAX_RESPONSE_TIME_MS * 2);
        
        // Log performance metrics
        System.out.printf("Performance Metrics for %d concurrent users:%n", CONCURRENT_USERS);
        System.out.printf("- Average response time: %.2f ms%n", averageResponseTime);
        System.out.printf("- Min response time: %d ms%n", minResponseTime);
        System.out.printf("- Max response time: %d ms%n", maxResponseTime);
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Load Test - Sustained Requests")
    void loadTestSustainedRequests() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<Long> allResponseTimes = new ArrayList<>();
        
        // Each user makes multiple requests
        for (int user = 0; user < CONCURRENT_USERS; user++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Long> userResponseTimes = new ArrayList<>();
                
                for (int request = 0; request < REQUESTS_PER_USER; request++) {
                    try {
                        StopWatch stopWatch = new StopWatch();
                        stopWatch.start();
                        
                        mockMvc.perform(get("/api/health"))
                                .andExpect(status().isOk());
                        
                        stopWatch.stop();
                        userResponseTimes.add(stopWatch.getLastTaskTimeMillis());
                        
                        // Small delay between requests
                        Thread.sleep(100);
                        
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                
                synchronized (allResponseTimes) {
                    allResponseTimes.addAll(userResponseTimes);
                }
            }, executor);
            
            futures.add(future);
        }
        
        // Wait for all users to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // Analyze sustained load performance
        double averageResponseTime = allResponseTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);
        
        long maxResponseTime = allResponseTimes.stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0);
        
        // Calculate percentiles
        allResponseTimes.sort(Long::compareTo);
        int totalRequests = allResponseTimes.size();
        long p95ResponseTime = allResponseTimes.get((int) (totalRequests * 0.95));
        long p99ResponseTime = allResponseTimes.get((int) (totalRequests * 0.99));
        
        // Performance assertions
        assertThat(averageResponseTime)
            .describedAs("Average response time under sustained load should be under %d ms", MAX_RESPONSE_TIME_MS)
            .isLessThan(MAX_RESPONSE_TIME_MS);
        
        assertThat(p95ResponseTime)
            .describedAs("95th percentile response time should be under %d ms", (long)(MAX_RESPONSE_TIME_MS * 1.5))
            .isLessThan((long)(MAX_RESPONSE_TIME_MS * 1.5));
        
        // Log detailed performance metrics
        System.out.printf("Load Test Results (%d users, %d requests each):%n", 
                         CONCURRENT_USERS, REQUESTS_PER_USER);
        System.out.printf("- Total requests: %d%n", totalRequests);
        System.out.printf("- Average response time: %.2f ms%n", averageResponseTime);
        System.out.printf("- 95th percentile: %d ms%n", p95ResponseTime);
        System.out.printf("- 99th percentile: %d ms%n", p99ResponseTime);
        System.out.printf("- Max response time: %d ms%n", maxResponseTime);
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Memory Usage Stability")
    void memoryUsageStability() throws Exception {
        Runtime runtime = Runtime.getRuntime();
        
        // Force garbage collection and get baseline
        System.gc();
        long baselineMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Perform sustained operations
        for (int i = 0; i < 1000; i++) {
            mockMvc.perform(get("/api/health"))
                    .andExpect(status().isOk());
            
            // Check memory every 100 requests
            if (i % 100 == 0) {
                long currentMemory = runtime.totalMemory() - runtime.freeMemory();
                long memoryIncrease = currentMemory - baselineMemory;
                
                // Memory increase should be reasonable
                assertThat(memoryIncrease)
                    .describedAs("Memory increase after %d requests should be under 50MB", i)
                    .isLessThan(50 * 1024 * 1024); // 50MB
            }
        }
        
        // Final memory check
        System.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long totalIncrease = finalMemory - baselineMemory;
        
        System.out.printf("Memory Usage Analysis:%n");
        System.out.printf("- Baseline memory: %.2f MB%n", baselineMemory / (1024.0 * 1024.0));
        System.out.printf("- Final memory: %.2f MB%n", finalMemory / (1024.0 * 1024.0));
        System.out.printf("- Total increase: %.2f MB%n", totalIncrease / (1024.0 * 1024.0));
        
        // Memory should not increase dramatically
        assertThat(totalIncrease)
            .describedAs("Total memory increase should be under 100MB")
            .isLessThan(100 * 1024 * 1024);
    }

    @Test
    @DisplayName("Security Interceptor Performance Impact")
    void securityInterceptorPerformanceImpact() throws Exception {
        // This test would require a way to disable security interceptor
        // For now, we'll measure the current performance with security enabled
        
        List<Long> responseTimes = new ArrayList<>();
        
        // Warm up
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/health"));
        }
        
        // Measure performance with security interceptor
        for (int i = 0; i < 100; i++) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            
            mockMvc.perform(get("/api/health"))
                    .andExpect(status().isOk());
            
            stopWatch.stop();
            responseTimes.add(stopWatch.getLastTaskTimeMillis());
        }
        
        double averageResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);
        
        // Security overhead should not significantly impact performance
        assertThat(averageResponseTime)
            .describedAs("Security interceptor should not add more than %d ms overhead", TARGET_RESPONSE_TIME_MS)
            .isLessThan(TARGET_RESPONSE_TIME_MS);
        
        System.out.printf("Security Interceptor Performance:%n");
        System.out.printf("- Average response time with security: %.2f ms%n", averageResponseTime);
    }

    @Test
    @DisplayName("Rate Limiting Performance")
    void rateLimitingPerformance() throws Exception {
        // Test rate limiting performance under normal load
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < 50; i++) { // Within rate limit
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            
            mockMvc.perform(get("/api/health"))
                    .andExpect(status().isOk());
            
            stopWatch.stop();
            responseTimes.add(stopWatch.getLastTaskTimeMillis());
            
            // Small delay to stay within rate limits
            Thread.sleep(20); // 50 requests per second
        }
        
        double averageResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);
        
        // Rate limiting should not significantly impact performance
        assertThat(averageResponseTime)
            .describedAs("Rate limiting should not add significant overhead")
            .isLessThan(TARGET_RESPONSE_TIME_MS);
        
        System.out.printf("Rate Limiting Performance:%n");
        System.out.printf("- Average response time with rate limiting: %.2f ms%n", averageResponseTime);
    }
}
