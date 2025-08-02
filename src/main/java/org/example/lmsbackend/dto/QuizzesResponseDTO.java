package org.example.lmsbackend.dto;

import org.example.lmsbackend.model.Quizzes;

import java.time.LocalDateTime;

public class QuizzesResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private Quizzes.QuizType quizType;
    private Integer timeLimit;
    private Boolean shuffleAnswers;
    private Boolean allowMultipleAttempts;
    private Integer maxAttempts;
    private Boolean showQuizResponses;
    private Boolean showOneQuestionAtATime;
    private LocalDateTime dueDate;
    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;
    private Boolean publish;

    private Integer courseId;
    private String courseTitle;
    private String categoryName;

    // --- Getters & Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Quizzes.QuizType getQuizType() {
        return quizType;
    }

    public void setQuizType(Quizzes.QuizType quizType) {
        this.quizType = quizType;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Boolean getShuffleAnswers() {
        return shuffleAnswers;
    }

    public void setShuffleAnswers(Boolean shuffleAnswers) {
        this.shuffleAnswers = shuffleAnswers;
    }

    public Boolean getAllowMultipleAttempts() {
        return allowMultipleAttempts;
    }

    public void setAllowMultipleAttempts(Boolean allowMultipleAttempts) {
        this.allowMultipleAttempts = allowMultipleAttempts;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Boolean getShowQuizResponses() {
        return showQuizResponses;
    }

    public void setShowQuizResponses(Boolean showQuizResponses) {
        this.showQuizResponses = showQuizResponses;
    }

    public Boolean getShowOneQuestionAtATime() {
        return showOneQuestionAtATime;
    }

    public void setShowOneQuestionAtATime(Boolean showOneQuestionAtATime) {
        this.showOneQuestionAtATime = showOneQuestionAtATime;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDateTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalDateTime getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(LocalDateTime availableUntil) {
        this.availableUntil = availableUntil;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }
}
