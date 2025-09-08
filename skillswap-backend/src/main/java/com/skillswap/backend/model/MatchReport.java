package com.skillswap.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entidad para almacenar reportes de matches
 */
@Entity
@Table(name = "match_reports")
public class MatchReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_match_id", nullable = false)
    private SkillMatch skillMatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @Column(name = "reason", nullable = false, length = 100)
    @NotNull
    @Size(min = 3, max = 100, message = "Report reason must be between 3 and 100 characters")
    private String reason;

    @Column(name = "description", length = 1000)
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 30)
    private ReportType reportType = ReportType.INAPPROPRIATE_BEHAVIOR;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "admin_notes", length = 500)
    private String adminNotes;

    // Constructors
    public MatchReport() {}

    public MatchReport(SkillMatch skillMatch, User reporter, User reportedUser, 
                      String reason, String description) {
        this.skillMatch = skillMatch;
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reason = reason;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    // Enums
    public enum ReportType {
        INAPPROPRIATE_BEHAVIOR,
        NO_SHOW,
        POOR_TEACHING_QUALITY,
        HARASSMENT,
        SPAM,
        TECHNICAL_ISSUES,
        OTHER
    }

    public enum ReportStatus {
        PENDING,
        UNDER_REVIEW,
        RESOLVED,
        DISMISSED,
        ESCALATED
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SkillMatch getSkillMatch() {
        return skillMatch;
    }

    public void setSkillMatch(SkillMatch skillMatch) {
        this.skillMatch = skillMatch;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getReportedUser() {
        return reportedUser;
    }

    public void setReportedUser(User reportedUser) {
        this.reportedUser = reportedUser;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    // Utility methods
    public boolean isPending() {
        return ReportStatus.PENDING.equals(status);
    }

    public boolean isResolved() {
        return ReportStatus.RESOLVED.equals(status);
    }

    public void markAsReviewed(User admin, String notes) {
        this.reviewedBy = admin;
        this.reviewedAt = LocalDateTime.now();
        this.adminNotes = notes;
        this.status = ReportStatus.UNDER_REVIEW;
    }

    public void resolve(String notes) {
        this.status = ReportStatus.RESOLVED;
        this.adminNotes = notes;
        if (this.reviewedAt == null) {
            this.reviewedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Auto-update reviewed timestamp when status changes
        if (this.reviewedAt == null && !ReportStatus.PENDING.equals(this.status)) {
            this.reviewedAt = LocalDateTime.now();
        }
    }
}
