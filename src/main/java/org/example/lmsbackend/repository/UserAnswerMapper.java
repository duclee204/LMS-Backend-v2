package org.example.lmsbackend.repository;

import org.apache.ibatis.annotations.*;
import org.example.lmsbackend.model.UserAnswer;

import java.util.List;

@Mapper
public interface UserAnswerMapper {

    @Insert("INSERT INTO user_answers (attempt_id, question_id, answer_id, answer_text, is_correct, link_answer, file_name, file_path) " +
            "VALUES (#{attempt.id}, #{question.questionId}, #{answer.answerId}, #{answerText}, #{isCorrect}, #{linkAnswer}, #{fileName}, #{filePath})")
    @Options(useGeneratedKeys = true, keyProperty = "user_answerId", keyColumn = "user_answer_id")
    void insertUserAnswer(UserAnswer userAnswer);

    @Select("SELECT ua.*, " +
            "a.attempt_id, " +
            "q.question_id, q.question_text, q.type, q.points, " +
            "ans.answer_id, ans.answer_text as selected_answer_text, ans.is_correct as selected_is_correct " +
            "FROM user_answers ua " +
            "JOIN user_quiz_attempts a ON ua.attempt_id = a.attempt_id " +
            "JOIN questions q ON ua.question_id = q.question_id " +
            "LEFT JOIN answers ans ON ua.answer_id = ans.answer_id " +
            "WHERE ua.attempt_id = #{attemptId}")
    @Results({
        @Result(property = "user_answerId", column = "user_answer_id"),
        @Result(property = "answerText", column = "answer_text"),
        @Result(property = "isCorrect", column = "is_correct"),
        @Result(property = "linkAnswer", column = "link_answer"),
        @Result(property = "fileName", column = "file_name"),
        @Result(property = "filePath", column = "file_path"),
        @Result(property = "instructorFeedback", column = "instructor_feedback"),
        @Result(property = "manualScore", column = "manual_score"),
        @Result(property = "attempt.attemptId", column = "attempt_id"),
        @Result(property = "question.questionId", column = "question_id"),
        @Result(property = "question.questionText", column = "question_text"),
        @Result(property = "question.type", column = "type"),
        @Result(property = "question.points", column = "points"),
        @Result(property = "answer.answerId", column = "answer_id"),
        @Result(property = "answer.answerText", column = "selected_answer_text"),
        @Result(property = "answer.isCorrect", column = "selected_is_correct")
    })
    List<UserAnswer> findByAttemptId(@Param("attemptId") Integer attemptId);

    @Update("UPDATE user_answers SET is_correct = #{isCorrect} WHERE user_answer_id = #{userAnswerId}")
    void updateCorrectness(@Param("userAnswerId") Integer userAnswerId, @Param("isCorrect") Boolean isCorrect);

    @Select("SELECT ua.*, " +
            "a.attempt_id, " +
            "q.question_id, q.question_text, q.type, q.points, " +
            "ans.answer_id, ans.answer_text as selected_answer_text, ans.is_correct as selected_is_correct " +
            "FROM user_answers ua " +
            "JOIN user_quiz_attempts a ON ua.attempt_id = a.attempt_id " +
            "JOIN questions q ON ua.question_id = q.question_id " +
            "LEFT JOIN answers ans ON ua.answer_id = ans.answer_id " +
            "WHERE ua.user_answer_id = #{userAnswerId}")
    @Results({
        @Result(property = "user_answerId", column = "user_answer_id"),
        @Result(property = "answerText", column = "answer_text"),
        @Result(property = "isCorrect", column = "is_correct"),
        @Result(property = "linkAnswer", column = "link_answer"),
        @Result(property = "fileName", column = "file_name"),
        @Result(property = "filePath", column = "file_path"),
        @Result(property = "instructorFeedback", column = "instructor_feedback"),
        @Result(property = "manualScore", column = "manual_score"),
        @Result(property = "attempt.attemptId", column = "attempt_id"),
        @Result(property = "question.questionId", column = "question_id"),
        @Result(property = "question.questionText", column = "question_text"),
        @Result(property = "question.type", column = "type"),
        @Result(property = "question.points", column = "points"),
        @Result(property = "answer.answerId", column = "answer_id"),
        @Result(property = "answer.answerText", column = "selected_answer_text"),
        @Result(property = "answer.isCorrect", column = "selected_is_correct")
    })
    UserAnswer findById(@Param("userAnswerId") Integer userAnswerId);

    @Update("UPDATE user_answers SET " +
            "answer_text = #{answerText}, " +
            "is_correct = #{isCorrect}, " +
            "link_answer = #{linkAnswer}, " +
            "file_name = #{fileName}, " +
            "file_path = #{filePath}, " +
            "instructor_feedback = #{instructorFeedback}, " +
            "manual_score = #{manualScore} " +
            "WHERE user_answer_id = #{user_answerId}")
    void updateUserAnswer(UserAnswer userAnswer);
}
