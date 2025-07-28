package org.example.lmsbackend.dto;

import java.time.Instant;

public class GradeDTO {
    private Integer attemptId;
    private Integer userId;
    private String studentName;
    private Integer quizId;
    private String quizTitle;
    private String quizType; // MULTIPLE_CHOICE or ESSAY
    private Integer score;
    private Integer maxScore;
    private Instant submittedAt;
    private String status; // COMPLETED, PENDING_GRADE, NOT_SUBMITTED
    
    // For essay questions
    private String textAnswer;
    private String linkAnswer;
    private String fileName;
    private String filePath;
    private Integer questionId;
    private String questionText;
    private Boolean isGraded;
    private String feedback;
    private Integer userAnswerId; // For loading essay details
    
    // Constructors
    public GradeDTO() {}
    
    // Getters and Setters
    public Integer getAttemptId() {
        return attemptId;
    }
    
    public void setAttemptId(Integer attemptId) {
        this.attemptId = attemptId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public Integer getQuizId() {
        return quizId;
    }
    
    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }
    
    public String getQuizTitle() {
        return quizTitle;
    }
    
    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }
    
    public String getQuizType() {
        return quizType;
    }
    
    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public Integer getMaxScore() {
        return maxScore;
    }
    
    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }
    
    public Instant getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTextAnswer() {
        return textAnswer;
    }
    
    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }
    
    public String getLinkAnswer() {
        return linkAnswer;
    }
    
    public void setLinkAnswer(String linkAnswer) {
        this.linkAnswer = linkAnswer;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Integer getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public Boolean getIsGraded() {
        return isGraded;
    }
    
    public void setIsGraded(Boolean isGraded) {
        this.isGraded = isGraded;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public Integer getUserAnswerId() {
        return userAnswerId;
    }
    
    public void setUserAnswerId(Integer userAnswerId) {
        this.userAnswerId = userAnswerId;
    }
}
