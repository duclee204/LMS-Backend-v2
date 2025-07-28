package org.example.lmsbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VideoDTO {
    private Long videoId;
    private String title;
    private String description;
    private String fileUrl;
    private Integer duration;
    private Long fileSize;
    private String mimeType;
    private LocalDateTime uploadedAt;
    private Integer courseId;
    private String courseName;
    private Integer instructorId;
    private String instructorName;

    // Constructors
    public VideoDTO() {}

    public VideoDTO(Long videoId, String title, String description, String fileUrl,
                    Integer duration, Long fileSize, String mimeType, LocalDateTime uploadedAt,
                    Integer courseId, String courseName, Integer instructorId, String instructorName) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.duration = duration;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.uploadedAt = uploadedAt;
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
    }

    // Getters and Setters
    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
}
