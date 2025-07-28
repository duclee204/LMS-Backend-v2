package org.example.lmsbackend.dto;

import org.springframework.web.multipart.MultipartFile;

public class EssaySubmissionDTO {
    private Integer quizId;
    private Integer questionId;
    private String textAnswer;
    private String linkAnswer;
    private MultipartFile fileAnswer;
    
    // Constructors
    public EssaySubmissionDTO() {}
    
    public EssaySubmissionDTO(Integer quizId, Integer questionId, String textAnswer, String linkAnswer) {
        this.quizId = quizId;
        this.questionId = questionId;
        this.textAnswer = textAnswer;
        this.linkAnswer = linkAnswer;
    }
    
    // Getters and Setters
    public Integer getQuizId() {
        return quizId;
    }
    
    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }
    
    public Integer getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
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
    
    public MultipartFile getFileAnswer() {
        return fileAnswer;
    }
    
    public void setFileAnswer(MultipartFile fileAnswer) {
        this.fileAnswer = fileAnswer;
    }
}
