package org.example.lmsbackend.dto;

public class QuestionResultDTO {
    private Integer questionId;
    private String questionText;
    private String questionType;
    private double points; // điểm tối đa
    private double earnedPoints; // điểm thực tế đạt được
    private boolean isCorrect;
    private String userAnswer;
    private String correctAnswer;
    
    // Constructors
    public QuestionResultDTO() {}
    
    // Getters and Setters
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    
    public double getPoints() { return points; }
    public void setPoints(double points) { this.points = points; }
    
    public double getEarnedPoints() { return earnedPoints; }
    public void setEarnedPoints(double earnedPoints) { this.earnedPoints = earnedPoints; }
    
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}
