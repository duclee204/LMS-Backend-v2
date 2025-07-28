package org.example.lmsbackend.dto;

public class ContentRequestDTO {
    private String title;
    private String contentType;
    private String description;
    private int orderNumber;
    private boolean published;
    private String contentUrl; // For link type content

    // Constructors
    public ContentRequestDTO() {}

    public ContentRequestDTO(String title, String contentType, String description, 
                           int orderNumber, boolean published, String contentUrl) {
        this.title = title;
        this.contentType = contentType;
        this.description = description;
        this.orderNumber = orderNumber;
        this.published = published;
        this.contentUrl = contentUrl;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}
