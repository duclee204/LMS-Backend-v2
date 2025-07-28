package org.example.lmsbackend.dto;

import java.util.List;

public class QuestionsDTO {

    private Integer questionId; // Thêm ID cho edit
    private Integer quizId;
    private String questionText;
    private String type; // "MULTIPLE_CHOICE", "ESSAY"
    private Integer points;
    private String questionFileUrl;
    private String questionFileName;
    private List<AnswerDTO> answers; // Danh sách lựa chọn cho trắc nghiệm

    public QuestionsDTO() {
    }

    public QuestionsDTO(Integer quizId, String questionText, String type, Integer points) {
        this.quizId = quizId;
        this.questionText = questionText;
        this.type = type;
        this.points = points;
    }

    // Getters and Setters
    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }

    public String getQuestionFileUrl() {
        return questionFileUrl;
    }

    public void setQuestionFileUrl(String questionFileUrl) {
        this.questionFileUrl = questionFileUrl;
    }

    public String getQuestionFileName() {
        return questionFileName;
    }

    public void setQuestionFileName(String questionFileName) {
        this.questionFileName = questionFileName;
    }
}
