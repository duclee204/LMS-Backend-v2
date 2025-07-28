package org.example.lmsbackend.dto;

import java.util.List;
import java.util.ArrayList;

public class QuizResultDTO {
    private Integer attemptId;
    private Integer quizId;
    private Integer userId;
    private double totalPoints;
    private double earnedPoints;
    private double score; // Phần trăm
    private List<QuestionResultDTO> questionResults = new ArrayList<>();
    
    // Constructors
    public QuizResultDTO() {}
    
    // Getters and Setters
    public Integer getAttemptId() { return attemptId; }
    public void setAttemptId(Integer attemptId) { this.attemptId = attemptId; }
    
    public Integer getQuizId() { return quizId; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public double getTotalPoints() { return totalPoints; }
    public void setTotalPoints(double totalPoints) { this.totalPoints = totalPoints; }
    
    public double getEarnedPoints() { return earnedPoints; }
    public void setEarnedPoints(double earnedPoints) { this.earnedPoints = earnedPoints; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    public List<QuestionResultDTO> getQuestionResults() { return questionResults; }
    public void setQuestionResults(List<QuestionResultDTO> questionResults) { this.questionResults = questionResults; }
}
