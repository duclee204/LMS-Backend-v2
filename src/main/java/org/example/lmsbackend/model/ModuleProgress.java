package org.example.lmsbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "module_progress")
public class ModuleProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long progressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Modules module;

    @Column(name = "content_completed")
    private Boolean contentCompleted = false;

    @Column(name = "video_completed")
    private Boolean videoCompleted = false;

    @Column(name = "test_completed")
    private Boolean testCompleted = false;

    @Column(name = "test_unlocked")
    private Boolean testUnlocked = false;

    @Column(name = "module_completed")
    private Boolean moduleCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
        
        // Auto-calculate module completion
        if (contentCompleted && videoCompleted && testCompleted && !moduleCompleted) {
            this.moduleCompleted = true;
            this.completedAt = LocalDateTime.now();
        }
        
        // Removed learning sequence restriction - test is always unlocked for free access
        this.testUnlocked = true;
    }

    // Getters and Setters
    public Long getProgressId() {
        return progressId;
    }

    public void setProgressId(Long progressId) {
        this.progressId = progressId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Modules getModule() {
        return module;
    }

    public void setModule(Modules module) {
        this.module = module;
    }

    public Boolean getContentCompleted() {
        return contentCompleted;
    }

    public void setContentCompleted(Boolean contentCompleted) {
        this.contentCompleted = contentCompleted;
    }

    public Boolean getVideoCompleted() {
        return videoCompleted;
    }

    public void setVideoCompleted(Boolean videoCompleted) {
        this.videoCompleted = videoCompleted;
    }

    public Boolean getTestCompleted() {
        return testCompleted;
    }

    public void setTestCompleted(Boolean testCompleted) {
        this.testCompleted = testCompleted;
    }

    public Boolean getTestUnlocked() {
        return testUnlocked;
    }

    public void setTestUnlocked(Boolean testUnlocked) {
        this.testUnlocked = testUnlocked;
    }

    public Boolean getModuleCompleted() {
        return moduleCompleted;
    }

    public void setModuleCompleted(Boolean moduleCompleted) {
        this.moduleCompleted = moduleCompleted;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
