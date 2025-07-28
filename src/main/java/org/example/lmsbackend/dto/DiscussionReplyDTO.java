package org.example.lmsbackend.dto;

import java.time.Instant;

public class DiscussionReplyDTO {
    private Integer replyId;
    private Integer discussionId;
    private Integer userId;
    private String userName;
    private String userRole;
    private String userAvatar;
    private String content;
    private Integer parentReplyId; // ID of parent reply for nested replies
    private String attachmentUrl; // File attachment URL
    private String attachmentName; // Original file name
    private Instant createdAt;

    // Constructors
    public DiscussionReplyDTO() {}

    public DiscussionReplyDTO(Integer discussionId, Integer userId, String content) {
        this.discussionId = discussionId;
        this.userId = userId;
        this.content = content;
        this.createdAt = Instant.now();
    }

    // Getters and Setters
    public Integer getReplyId() {
        return replyId;
    }

    public void setReplyId(Integer replyId) {
        this.replyId = replyId;
    }

    public Integer getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(Integer discussionId) {
        this.discussionId = discussionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public Integer getParentReplyId() {
        return parentReplyId;
    }

    public void setParentReplyId(Integer parentReplyId) {
        this.parentReplyId = parentReplyId;
    }
}
