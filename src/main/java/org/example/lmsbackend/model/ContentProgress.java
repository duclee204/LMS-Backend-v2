package org.example.lmsbackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "content_progress")
public class ContentProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id", nullable = false)
    private Integer progressId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private org.example.lmsbackend.model.User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Lob
    @Column(name = "status")
    private String status = "not_accessed";

    @Column(name = "accessed_at")
    private Instant accessedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public Integer getId() {
        return progressId;
    }

    public void setId(Integer progressId) {
        this.progressId = progressId;
    }

    public org.example.lmsbackend.model.User getUser() {
        return user;
    }

    public void setUser(org.example.lmsbackend.model.User user) {
        this.user = user;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getAccessedAt() {
        return accessedAt;
    }

    public void setAccessedAt(Instant accessedAt) {
        this.accessedAt = accessedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "not_accessed";
    }

    // Helper method to mark content as accessed/completed
    public void markAsAccessed() {
        this.status = "accessed";
        this.accessedAt = Instant.now();
        this.completedAt = Instant.now(); // For content, accessed = completed
    }
}