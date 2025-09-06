package com.skillswap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for SkillSwap Application
 * Verifies that the Spring Boot context loads successfully
 */
@SpringBootTest
@ActiveProfiles("test")
class SkillswapApplicationTests {

    @Test
    void contextLoads() {
        // This test will pass if the Spring Boot context loads successfully
        // It validates that all configurations are correct
    }

    @Test
    void applicationStarts() {
        // This test verifies that the main application class can be instantiated
        // without any configuration errors
    }
}
