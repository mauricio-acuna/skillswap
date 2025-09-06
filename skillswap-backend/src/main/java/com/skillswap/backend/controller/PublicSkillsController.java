package com.skillswap.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Public Skills Controller
 * Provides public access to basic skill information
 */
@RestController
@RequestMapping("/api/public/skills")
@Tag(name = "Public Skills", description = "Public skill browsing operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PublicSkillsController {

    /**
     * Get skill categories
     */
    @GetMapping("/categories")
    @Operation(summary = "Get skill categories", description = "Get list of available skill categories")
    public ResponseEntity<List<SkillCategory>> getSkillCategories() {
        List<SkillCategory> categories = Arrays.asList(
            new SkillCategory(1L, "Technology", "💻", "Programming, Software Development, IT"),
            new SkillCategory(2L, "Languages", "🌍", "Spanish, English, French, German, etc."),
            new SkillCategory(3L, "Arts & Design", "🎨", "Graphic Design, Photography, Drawing"),
            new SkillCategory(4L, "Music", "🎵", "Guitar, Piano, Singing, Production"),
            new SkillCategory(5L, "Sports & Fitness", "🏃", "Yoga, Running, Swimming, Martial Arts"),
            new SkillCategory(6L, "Cooking", "👨‍🍳", "International Cuisine, Baking, Nutrition"),
            new SkillCategory(7L, "Business", "💼", "Marketing, Finance, Entrepreneurship"),
            new SkillCategory(8L, "Crafts", "✂️", "Knitting, Woodworking, Pottery"),
            new SkillCategory(9L, "Academic", "📚", "Mathematics, Science, History"),
            new SkillCategory(10L, "Other", "🔧", "Life Skills, Hobbies, Miscellaneous")
        );
        
        return ResponseEntity.ok(categories);
    }

    /**
     * Get popular skills
     */
    @GetMapping("/popular")
    @Operation(summary = "Get popular skills", description = "Get list of most popular skills on the platform")
    public ResponseEntity<List<PopularSkill>> getPopularSkills() {
        List<PopularSkill> popularSkills = Arrays.asList(
            new PopularSkill(1L, "JavaScript", "Programming Language", "Technology", 1250),
            new PopularSkill(2L, "Spanish", "Language Learning", "Languages", 890),
            new PopularSkill(3L, "Guitar", "Musical Instrument", "Music", 765),
            new PopularSkill(4L, "Photography", "Visual Arts", "Arts & Design", 654),
            new PopularSkill(5L, "Yoga", "Mind & Body", "Sports & Fitness", 543),
            new PopularSkill(6L, "Python", "Programming Language", "Technology", 512),
            new PopularSkill(7L, "Cooking", "Culinary Arts", "Cooking", 487),
            new PopularSkill(8L, "English", "Language Learning", "Languages", 423),
            new PopularSkill(9L, "React", "Web Framework", "Technology", 398),
            new PopularSkill(10L, "Digital Marketing", "Business Strategy", "Business", 345)
        );
        
        return ResponseEntity.ok(popularSkills);
    }

    /**
     * Search skills by name
     */
    @GetMapping("/search")
    @Operation(summary = "Search skills", description = "Search skills by name or description")
    public ResponseEntity<List<SkillSearchResult>> searchSkills(@RequestParam String q) {
        // This is a mock implementation - in real app, this would query the database
        List<SkillSearchResult> results = Arrays.asList(
            new SkillSearchResult(1L, "JavaScript", "Programming language for web development", "Technology", 1250),
            new SkillSearchResult(2L, "Java", "Object-oriented programming language", "Technology", 890)
        );
        
        return ResponseEntity.ok(results);
    }

    /**
     * Get platform statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Platform statistics", description = "Get general platform statistics")
    public ResponseEntity<PlatformStats> getPlatformStats() {
        PlatformStats stats = new PlatformStats(
            12567L,  // Total users
            89L,      // Available skills
            3456L,    // Completed sessions
            4.8       // Average rating
        );
        
        return ResponseEntity.ok(stats);
    }

    // DTOs
    public static class SkillCategory {
        private Long id;
        private String name;
        private String icon;
        private String description;

        public SkillCategory(Long id, String name, String icon, String description) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.description = description;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class PopularSkill {
        private Long id;
        private String name;
        private String description;
        private String category;
        private Integer userCount;

        public PopularSkill(Long id, String name, String description, String category, Integer userCount) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.category = category;
            this.userCount = userCount;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Integer getUserCount() { return userCount; }
        public void setUserCount(Integer userCount) { this.userCount = userCount; }
    }

    public static class SkillSearchResult {
        private Long id;
        private String name;
        private String description;
        private String category;
        private Integer userCount;

        public SkillSearchResult(Long id, String name, String description, String category, Integer userCount) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.category = category;
            this.userCount = userCount;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Integer getUserCount() { return userCount; }
        public void setUserCount(Integer userCount) { this.userCount = userCount; }
    }

    public static class PlatformStats {
        private Long totalUsers;
        private Long totalSkills;
        private Long completedSessions;
        private Double averageRating;

        public PlatformStats(Long totalUsers, Long totalSkills, Long completedSessions, Double averageRating) {
            this.totalUsers = totalUsers;
            this.totalSkills = totalSkills;
            this.completedSessions = completedSessions;
            this.averageRating = averageRating;
        }

        // Getters and setters
        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
        public Long getTotalSkills() { return totalSkills; }
        public void setTotalSkills(Long totalSkills) { this.totalSkills = totalSkills; }
        public Long getCompletedSessions() { return completedSessions; }
        public void setCompletedSessions(Long completedSessions) { this.completedSessions = completedSessions; }
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    }
}
