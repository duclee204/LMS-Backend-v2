package org.example.lmsbackend.repository;

import org.apache.ibatis.annotations.*;
import org.example.lmsbackend.model.Quizzes;

@Mapper
public interface QuizzesMapper {

    @Select("SELECT * FROM quizzes WHERE quiz_id = #{quizId}")
    @Results({
        @Result(property = "quizId", column = "quiz_id"),
        @Result(property = "courseId", column = "course_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "timeLimit", column = "time_limit"),
        @Result(property = "publish", column = "publish"),
        @Result(property = "allowMultipleAttempts", column = "allow_multiple_attempts"),
        @Result(property = "maxAttempts", column = "max_attempts")
    })
    Quizzes findById(@Param("quizId") Integer quizId);

    @Insert("INSERT INTO quizzes (course_id, title, description, time_limit, publish, allow_multiple_attempts, max_attempts) " +
            "VALUES (#{courseId}, #{title}, #{description}, #{timeLimit}, #{publish}, #{allowMultipleAttempts}, #{maxAttempts})")
    @Options(useGeneratedKeys = true, keyProperty = "quizId", keyColumn = "quiz_id")
    void insertQuiz(Quizzes quiz);

    @Update("UPDATE quizzes SET title = #{title}, description = #{description}, " +
            "time_limit = #{timeLimit}, publish = #{publish}, allow_multiple_attempts = #{allowMultipleAttempts}, " +
            "max_attempts = #{maxAttempts} WHERE quiz_id = #{quizId}")
    void updateQuiz(Quizzes quiz);

    @Delete("DELETE FROM quizzes WHERE quiz_id = #{quizId}")
    void deleteQuiz(@Param("quizId") Integer quizId);
}
