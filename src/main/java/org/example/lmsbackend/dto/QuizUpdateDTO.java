package org.example.lmsbackend.dto;

import java.util.List;

public class QuizUpdateDTO {
    private String title;
    private String description;
    private Integer timeLimit;
    private Boolean allowMultipleAttempts;
    private Double totalMarks;
    private Integer maxAttempts;
    private List<QuestionsDTO> questions;
    
    // Constructors
    public QuizUpdateDTO() {}
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }
    
    public Boolean getAllowMultipleAttempts() { return allowMultipleAttempts; }
    public void setAllowMultipleAttempts(Boolean allowMultipleAttempts) { this.allowMultipleAttempts = allowMultipleAttempts; }
    
    public Double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Double totalMarks) { this.totalMarks = totalMarks; }
    
    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }
    
    public List<QuestionsDTO> getQuestions() { return questions; }
    public void setQuestions(List<QuestionsDTO> questions) { this.questions = questions; }
}
