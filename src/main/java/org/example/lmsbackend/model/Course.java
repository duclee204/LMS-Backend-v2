package org.example.lmsbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course {

    public enum Status {
        draft, published, archived
    }
    @Column(name = "thumbnail_url", length = 512)
    private String thumbnailUrl;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer courseId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "category_id", insertable = false, updatable = false)
    private Integer categoryId;

    @Column(name = "instructor_id", insertable = false, updatable = false)
    private Integer instructorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_course_category"), nullable = true)
    private Categories category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", foreignKey = @ForeignKey(name = "fk_course_instructor"), nullable = true)
    private User instructor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.draft;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "created_at", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    @Transient
    private String instructorName; // không map với DB, dùng để hiển thị
}
