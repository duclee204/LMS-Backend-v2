package org.example.lmsbackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "progress")
public class Progress {
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
    private String status = "not_started";

    @Column(name = "percentage_viewed", precision = 5, scale = 2)
    private BigDecimal percentageViewed = BigDecimal.ZERO;

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

    public BigDecimal getPercentageViewed() {
        return percentageViewed;
    }

    public void setPercentageViewed(BigDecimal percentageViewed) {
        this.percentageViewed = percentageViewed;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "not_started";
        if (percentageViewed == null) percentageViewed = BigDecimal.ZERO;
    }
}