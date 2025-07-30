package org.example.lmsbackend.dto;

import java.time.Instant;
import java.util.List;

public class DiscussionDTO {
    private Integer id;
    private Integer courseId;
    private Integer userId;
    private String userName;
    private String userRole;
    private String title;
    private String content;
    private String type; // PUBLIC or PRIVATE
    private Integer targetUserId; // For private discussions (single target - backward compatibility)
    private List<Integer> targetUserIds; // For private discussions (multiple targets)
    private String targetUserName;
    private String attachmentUrl; // File attachment URL
    private String attachmentName; // Original file name
    private Instant createdAt;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Integer targetUserId) { this.targetUserId = targetUserId; }
    
    public List<Integer> getTargetUserIds() { return targetUserIds; }
    public void setTargetUserIds(List<Integer> targetUserIds) { this.targetUserIds = targetUserIds; }
    
    public String getTargetUserName() { return targetUserName; }
    public void setTargetUserName(String targetUserName) { this.targetUserName = targetUserName; }
    
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    
    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
