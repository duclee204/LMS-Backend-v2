package org.example.lmsbackend.dto;

public class ModuleResponseDTO {
    private Integer moduleId;
    private String title;
    private String description;
    private Integer orderNumber;
    private Boolean published;
    private Integer courseId;
    private String courseTitle;

    // === GETTERS ===
    public Integer getModuleId() {
        return moduleId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public Boolean getPublished() {
        return published;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    // === SETTERS ===
    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
}
