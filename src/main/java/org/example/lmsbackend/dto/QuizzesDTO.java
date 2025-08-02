package org.example.lmsbackend.dto;

import org.example.lmsbackend.model.Quizzes;
import org.example.lmsbackend.model.Quizzes.QuizType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

public class QuizzesDTO {
    private Integer quizId;
    private String title;
    private String description;
    private Quizzes.QuizType quizType;
    private Integer timeLimit;
    private Boolean shuffleAnswers;
    private Boolean allowMultipleAttempts;
    private Integer maxAttempts;
    private Boolean showQuizResponses;
    private Boolean showOneQuestionAtATime;
    private Boolean publish;
    private Integer courseId;
    private Integer moduleId;

    // Getters and Setters
    public Integer getQuizId() { return quizId; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public QuizType getQuizType() { return quizType; }
    public void setQuizType(QuizType quizType) { this.quizType = quizType; }

    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }

    public Boolean getShuffleAnswers() { return shuffleAnswers; }
    public void setShuffleAnswers(Boolean shuffleAnswers) { this.shuffleAnswers = shuffleAnswers; }

    public Boolean getAllowMultipleAttempts() { return allowMultipleAttempts; }
    public void setAllowMultipleAttempts(Boolean allowMultipleAttempts) { this.allowMultipleAttempts = allowMultipleAttempts; }

    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }

    public Boolean getShowQuizResponses() { return showQuizResponses; }
    public void setShowQuizResponses(Boolean showQuizResponses) { this.showQuizResponses = showQuizResponses; }

    public Boolean getShowOneQuestionAtATime() { return showOneQuestionAtATime; }
    public void setShowOneQuestionAtATime(Boolean showOneQuestionAtATime) { this.showOneQuestionAtATime = showOneQuestionAtATime; }

    @JsonProperty("published")
    @JsonAlias({"publish", "published"})
    public Boolean getPublish() { return publish; }
    public void setPublish(Boolean publish) { this.publish = publish; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public Integer getModuleId() { return moduleId; }
    public void setModuleId(Integer moduleId) { this.moduleId = moduleId; }
}
