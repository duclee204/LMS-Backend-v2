package org.example.lmsbackend.dto;

public class EnrollmentsDTO {
    private int courseId;
    private String courseTitle;
    private String status;
    private String enrolledAt;

    public EnrollmentsDTO() {}

    public EnrollmentsDTO(int courseId, String courseTitle, String status, String enrolledAt) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.status = status;
        this.enrolledAt = enrolledAt;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(String enrolledAt) {
        this.enrolledAt = enrolledAt;
    }
}

