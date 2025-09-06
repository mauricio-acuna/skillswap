package com.skillswap.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class MatchCandidate {
    private Long userId;
    private String name;
    private String profilePictureUrl;
    private String bio;
    private String location;
    private BigDecimal compatibilityScore;
    private List<SkillMatchInfo> skillMatches;
    private String preferredSchedule;
    private Double averageRating;
    private Integer totalRatings;
    private String joinedDate;
    private Boolean isOnline;
    private String lastActive;

    // Constructor principal
    public MatchCandidate(Long userId, String name, String profilePictureUrl, 
                         String bio, String location, double compatibilityScore,
                         List<SkillMatchInfo> skillMatches) {
        this.userId = userId;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
        this.bio = bio;
        this.location = location;
        this.compatibilityScore = BigDecimal.valueOf(compatibilityScore);
        this.skillMatches = skillMatches;
    }

    // Constructor vacío
    public MatchCandidate() {}

    // Getters y Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getCompatibilityScore() {
        return compatibilityScore;
    }

    public void setCompatibilityScore(BigDecimal compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }

    public void setCompatibilityScore(double compatibilityScore) {
        this.compatibilityScore = BigDecimal.valueOf(compatibilityScore);
    }

    public List<SkillMatchInfo> getSkillMatches() {
        return skillMatches;
    }

    public void setSkillMatches(List<SkillMatchInfo> skillMatches) {
        this.skillMatches = skillMatches;
    }

    public String getPreferredSchedule() {
        return preferredSchedule;
    }

    public void setPreferredSchedule(String preferredSchedule) {
        this.preferredSchedule = preferredSchedule;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    // Clase interna para información de skill matching
    public static class SkillMatchInfo {
        private Long skillId;
        private String skillName;
        private String category;
        private String role; // "teacher" o "learner"
        private Integer level;
        private String description;

        public SkillMatchInfo() {}

        public SkillMatchInfo(Long skillId, String skillName, String category, 
                             String role, Integer level, String description) {
            this.skillId = skillId;
            this.skillName = skillName;
            this.category = category;
            this.role = role;
            this.level = level;
            this.description = description;
        }

        // Getters y Setters
        public Long getSkillId() {
            return skillId;
        }

        public void setSkillId(Long skillId) {
            this.skillId = skillId;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @Override
    public String toString() {
        return "MatchCandidate{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", compatibilityScore=" + compatibilityScore +
                ", skillMatches=" + skillMatches +
                '}';
    }
}
