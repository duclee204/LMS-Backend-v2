package org.example.lmsbackend.repository;

import org.apache.ibatis.annotations.*;
import org.example.lmsbackend.model.UserQuizAttempt;
import java.util.List;

@Mapper
public interface UserQuizAttemptMapper {

    @Insert("INSERT INTO user_quiz_attempts (user_id, quiz_id, score, attempted_at) " +
            "VALUES (#{user.userId}, #{quiz.quizId}, #{score}, #{attemptedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "attemptId", keyColumn = "attempt_id")
    void insertAttempt(UserQuizAttempt attempt);

    @Update("UPDATE user_quiz_attempts SET score = #{score} WHERE attempt_id = #{attemptId}")
    void updateAttemptScore(@Param("attemptId") Integer attemptId, @Param("score") Integer score);

    @Select("SELECT a.*, u.user_id, u.username, u.full_name, u.email, " +
            "q.quiz_id, q.title, q.description " +
            "FROM user_quiz_attempts a " +
            "JOIN users u ON a.user_id = u.user_id " +
            "JOIN quizzes q ON a.quiz_id = q.quiz_id " +
            "WHERE a.user_id = #{userId} AND a.quiz_id = #{quizId} " +
            "ORDER BY a.score DESC, a.attempted_at DESC LIMIT 1")
    @Results({
        @Result(property = "attemptId", column = "attempt_id"),
        @Result(property = "score", column = "score"),
        @Result(property = "attemptedAt", column = "attempted_at"),
        @Result(property = "user.userId", column = "user_id"),
        @Result(property = "user.username", column = "username"),
        @Result(property = "user.fullName", column = "full_name"),
        @Result(property = "user.email", column = "email"),
        @Result(property = "quiz.quizId", column = "quiz_id"),
        @Result(property = "quiz.title", column = "title"),
        @Result(property = "quiz.description", column = "description")
    })
    UserQuizAttempt findByUserAndQuiz(@Param("userId") Integer userId, @Param("quizId") Integer quizId);

    @Select("SELECT a.*, u.user_id, u.username, u.full_name, u.email, " +
            "q.quiz_id, q.title, q.description " +
            "FROM user_quiz_attempts a " +
            "JOIN users u ON a.user_id = u.user_id " +
            "JOIN quizzes q ON a.quiz_id = q.quiz_id " +
            "WHERE a.user_id = #{userId} AND a.quiz_id = #{quizId} " +
            "ORDER BY a.attempted_at DESC")
    @Results({
        @Result(property = "attemptId", column = "attempt_id"),
        @Result(property = "score", column = "score"),
        @Result(property = "attemptedAt", column = "attempted_at"),
        @Result(property = "user.userId", column = "user_id"),
        @Result(property = "user.username", column = "username"),
        @Result(property = "user.fullName", column = "full_name"),
        @Result(property = "user.email", column = "email"),
        @Result(property = "quiz.quizId", column = "quiz_id"),
        @Result(property = "quiz.title", column = "title"),
        @Result(property = "quiz.description", column = "description")
    })
    List<UserQuizAttempt> findAllAttemptsByUserAndQuiz(@Param("userId") Integer userId, @Param("quizId") Integer quizId);

    @Select("SELECT COUNT(*) FROM user_quiz_attempts WHERE user_id = #{userId} AND quiz_id = #{quizId}")
    int countAttemptsByUserAndQuiz(@Param("userId") Integer userId, @Param("quizId") Integer quizId);

    @Select("SELECT a.*, u.user_id, u.username, u.full_name, u.email, " +
            "q.quiz_id, q.title, q.description, q.quiz_type, q.course_id " +
            "FROM user_quiz_attempts a " +
            "JOIN users u ON a.user_id = u.user_id " +
            "JOIN quizzes q ON a.quiz_id = q.quiz_id " +
            "WHERE a.attempt_id = #{attemptId}")
    @Results({
        @Result(property = "attemptId", column = "attempt_id"),
        @Result(property = "score", column = "score"),
        @Result(property = "attemptedAt", column = "attempted_at"),
        @Result(property = "user.userId", column = "user_id"),
        @Result(property = "user.username", column = "username"),
        @Result(property = "user.fullName", column = "full_name"),
        @Result(property = "user.email", column = "email"),
        @Result(property = "quiz.quizId", column = "quiz_id"),
        @Result(property = "quiz.title", column = "title"),
        @Result(property = "quiz.description", column = "description"),
        @Result(property = "quiz.quizType", column = "quiz_type"),
        @Result(property = "quiz.courseId", column = "course_id")
    })
    UserQuizAttempt findById(@Param("attemptId") Integer attemptId);

    @Select("SELECT a.*, u.user_id, u.username, u.full_name, u.email, " +
            "q.quiz_id, q.title, q.description, q.quiz_type, q.course_id " +
            "FROM user_quiz_attempts a " +
            "JOIN users u ON a.user_id = u.user_id " +
            "JOIN quizzes q ON a.quiz_id = q.quiz_id " +
            "WHERE q.course_id = #{courseId} " +
            "ORDER BY a.attempted_at DESC")
    @Results({
        @Result(property = "attemptId", column = "attempt_id"),
        @Result(property = "score", column = "score"),
        @Result(property = "attemptedAt", column = "attempted_at"),
        @Result(property = "user.userId", column = "user_id"),
        @Result(property = "user.username", column = "username"),
        @Result(property = "user.fullName", column = "full_name"),
        @Result(property = "user.email", column = "email"),
        @Result(property = "quiz.quizId", column = "quiz_id"),
        @Result(property = "quiz.title", column = "title"),
        @Result(property = "quiz.description", column = "description"),
        @Result(property = "quiz.quizType", column = "quiz_type"),
        @Result(property = "quiz.courseId", column = "course_id")
    })
    List<UserQuizAttempt> findByCourseId(@Param("courseId") Integer courseId);

    @Select("SELECT a.*, u.user_id, u.username, u.full_name, u.email, " +
            "q.quiz_id, q.title, q.description, q.quiz_type, q.course_id " +
            "FROM user_quiz_attempts a " +
            "JOIN users u ON a.user_id = u.user_id " +
            "JOIN quizzes q ON a.quiz_id = q.quiz_id " +
            "WHERE a.user_id = #{userId} " +
            "ORDER BY a.attempted_at DESC")
    @Results({
        @Result(property = "attemptId", column = "attempt_id"),
        @Result(property = "score", column = "score"),
        @Result(property = "attemptedAt", column = "attempted_at"),
        @Result(property = "user.userId", column = "user_id"),
        @Result(property = "user.username", column = "username"),
        @Result(property = "user.fullName", column = "full_name"),
        @Result(property = "user.email", column = "email"),
        @Result(property = "quiz.quizId", column = "quiz_id"),
        @Result(property = "quiz.title", column = "title"),
        @Result(property = "quiz.description", column = "description"),
        @Result(property = "quiz.quizType", column = "quiz_type"),
        @Result(property = "quiz.courseId", column = "course_id")
    })
    List<UserQuizAttempt> findByUserId(@Param("userId") Integer userId);
}
