package org.example.lmsbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "quizzes")
public class Quizzes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Integer quizId;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    public enum QuizType {
        MULTIPLE_CHOICE,
        ESSAY
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type", nullable = false)
    private QuizType quizType;

    private Integer timeLimit;
    private Boolean shuffleAnswers = false;
    private Boolean allowMultipleAttempts = false;
    private Integer maxAttempts = 2; // Default maximum attempts is 2
    private Boolean showQuizResponses = false;
    private Boolean showOneQuestionAtATime = false;

    @Column(nullable = false)
    private Boolean publish = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "course_id", insertable = false, updatable = false)
    private Integer courseId;

    // Thêm quan hệ với Module
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Modules module;

    @Column(name = "order_number")
    private Integer orderNumber;

    // Getters and Setters
    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
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

    public QuizType getQuizType() {
        return quizType;
    }

    public void setQuizType(QuizType quizType) {
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

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Modules getModule() {
        return module;
    }

    public void setModule(Modules module) {
        this.module = module;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }
}
