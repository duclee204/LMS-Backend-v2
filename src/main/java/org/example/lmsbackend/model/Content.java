package org.example.lmsbackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "module_id", nullable = false)
    private Modules module;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "type", nullable = false)
    private String type; // e.g., "document", "text", "link"

    @Column(name = "content_url")
    private String contentUrl; // link tới video, file tài liệu,...

    @Column(name = "file_name")
    private String fileName; // chỉ dùng nếu là tài liệu

    @Column(name = "duration")
    private Integer duration; // dùng cho video/audio

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "published")
    private boolean published = false;

    public Integer getId() {
        return id;
    }

    public Modules getModule() {
        return module;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public boolean isPublished() {
        return published;
    }

    // === SETTERS ===
    public void setId(Integer id) {
        this.id = id;
    }

    public void setModule(Modules module) {
        this.module = module;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

}
