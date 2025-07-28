package org.example.lmsbackend.dto;

public class AnswerDTO {
    private Integer answerId;
    private Integer questionId;
    private String answerText;
    private Boolean isCorrect;
    private Integer orderNumber;

    // Constructors
    public AnswerDTO() {
    }

    public AnswerDTO(Integer answerId, Integer questionId, String answerText, Boolean isCorrect, Integer orderNumber) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
        this.orderNumber = orderNumber;
    }

    // Getters and Setters
    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }
}
