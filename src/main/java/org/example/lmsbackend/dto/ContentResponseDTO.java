package org.example.lmsbackend.dto;

public class ContentResponseDTO {
    private Integer contentId;
    private Integer moduleId; // Thêm moduleId
    private String title;
    private String type;
    private String contentUrl;
    private String fileName;
    private Integer duration;
    private Integer orderNumber;
    private boolean published;

    // === GETTERS ===
    public Integer getContentId() {
        return contentId;
    }

    public Integer getModuleId() {
        return moduleId;
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
    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
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
