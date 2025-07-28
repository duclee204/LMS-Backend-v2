package org.example.lmsbackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "user_answers")
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_answer_id", nullable = false)
    private Integer user_answerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "attempt_id", nullable = false)
    private org.example.lmsbackend.model.UserQuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "question_id", nullable = false)
    private Questions question;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @Lob
    @Column(name = "answer_text")
    private String answerText;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    // Essay submission fields
    @Column(name = "link_answer")
    private String linkAnswer;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "instructor_feedback")
    private String instructorFeedback;

    @Column(name = "manual_score")
    private Integer manualScore;

    public Integer getId() {
        return user_answerId;
    }

    public void setId(Integer user_answerId) {
        this.user_answerId = user_answerId;
    }

    public org.example.lmsbackend.model.UserQuizAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(org.example.lmsbackend.model.UserQuizAttempt attempt) {
        this.attempt = attempt;
    }

    public Questions getQuestion() {
        return question;
    }

    public void setQuestion(Questions question) {
        this.question = question;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
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

    public String getInstructorFeedback() {
        return instructorFeedback;
    }

    public void setInstructorFeedback(String instructorFeedback) {
        this.instructorFeedback = instructorFeedback;
    }

    public Integer getManualScore() {
        return manualScore;
    }

    public void setManualScore(Integer manualScore) {
        this.manualScore = manualScore;
    }

}