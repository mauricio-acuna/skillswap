package com.skillswap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.cache.annotation.EnableCaching;

/**
 * SkillSwap Backend Application
 * P2P Skill Exchange Platform
 * 
 * @author SkillSwap Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableCaching
public class SkillswapApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillswapApplication.class, args);
    }
}
