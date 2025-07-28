package org.example.lmsbackend.dto;

public class UserAnswerDTO {
    private Integer questionId;
    private Integer answerId; // For multiple choice questions
    private String answerText; // For essay questions or text input
    private Integer selectedIndex; // For multiple choice questions from frontend
    private String linkAnswer; // For essay link submission
    private String fileName; // For essay file submission
    private String filePath; // For essay file path

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Integer getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(Integer selectedIndex) {
        this.selectedIndex = selectedIndex;
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
}
