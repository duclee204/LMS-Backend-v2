package org.example.lmsbackend.repository;

import org.apache.ibatis.annotations.*;
import org.example.lmsbackend.model.Questions;

import java.util.List;

@Mapper
public interface QuestionsMapper {

    // Thêm câu hỏi
    @Insert("""
        INSERT INTO questions (quiz_id, question_text, type, points, question_file_url, question_file_name)
        VALUES (#{quiz.quizId}, #{questionText}, #{type}, #{points}, #{questionFileUrl}, #{questionFileName})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    void insertQuestion(Questions question);

    // Lấy danh sách câu hỏi theo quiz_id
    @Select("""
        SELECT q.question_id, q.quiz_id, q.question_text, q.type, q.points, q.question_file_url, q.question_file_name,
               qz.title as quiz_title, qz.description as quiz_description
        FROM questions q
        LEFT JOIN quizzes qz ON q.quiz_id = qz.quiz_id
        WHERE q.quiz_id = #{quizId}
        ORDER BY q.question_id ASC
    """)
    @Results({
        @Result(property = "questionId", column = "question_id"),
        @Result(property = "quiz", column = "quiz_id", 
                one = @One(select = "findQuizById")),
        @Result(property = "questionText", column = "question_text"),
        @Result(property = "type", column = "type"),
        @Result(property = "points", column = "points"),
        @Result(property = "questionFileUrl", column = "question_file_url"),
        @Result(property = "questionFileName", column = "question_file_name"),
        @Result(property = "answers", column = "question_id",
                javaType = List.class,
                many = @Many(select = "org.example.lmsbackend.repository.AnswerMapper.findByQuestionId"))
    })
    List<Questions> findByQuizId(@Param("quizId") int quizId);

    // Lấy câu hỏi theo ID
    @Select("""
        SELECT q.question_id, q.quiz_id, q.question_text, q.type, q.points, q.question_file_url, q.question_file_name,
               qz.title as quiz_title, qz.description as quiz_description
        FROM questions q
        LEFT JOIN quizzes qz ON q.quiz_id = qz.quiz_id
        WHERE q.question_id = #{questionId}
    """)
    @Results({
        @Result(property = "questionId", column = "question_id"),
        @Result(property = "quiz", column = "quiz_id", 
                one = @One(select = "findQuizById")),
        @Result(property = "questionText", column = "question_text"),
        @Result(property = "type", column = "type"),
        @Result(property = "points", column = "points"),
        @Result(property = "questionFileUrl", column = "question_file_url"),
        @Result(property = "questionFileName", column = "question_file_name"),
        @Result(property = "answers", column = "question_id",
                javaType = List.class,
                many = @Many(select = "org.example.lmsbackend.repository.AnswerMapper.findByQuestionId"))
    })
    Questions findById(@Param("questionId") Integer questionId);

    // Cập nhật câu hỏi
    @Update("""
        UPDATE questions
        SET question_text = #{questionText},
            type = #{type},
            points = #{points},
            question_file_url = #{questionFileUrl},
            question_file_name = #{questionFileName}
        WHERE question_id = #{questionId}
    """)
    void updateQuestion(Questions question);

    // Xóa câu hỏi theo ID
    @Delete("DELETE FROM questions WHERE question_id = #{questionId}")
    void deleteQuestion(@Param("questionId") int questionId);

    // Lấy quiz theo ID
    @Select("""
        SELECT quiz_id, title, description, time_limit, course_id, quiz_type, publish
        FROM quizzes
        WHERE quiz_id = #{quizId}
    """)
    @Results({
        @Result(property = "quizId", column = "quiz_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"), 
        @Result(property = "timeLimit", column = "time_limit"),
        @Result(property = "courseId", column = "course_id"),
        @Result(property = "quizType", column = "quiz_type"),
        @Result(property = "publish", column = "publish")
    })
    org.example.lmsbackend.model.Quizzes findQuizById(@Param("quizId") Integer quizId);

    // Cập nhật thông tin cơ bản của quiz
    @Update("""
        UPDATE quizzes
        SET title = #{title}, 
            description = #{description}, 
            time_limit = #{timeLimit},
            allow_multiple_attempts = #{allowMultipleAttempts}
        WHERE quiz_id = #{quizId}
    """)
    int updateQuizBasicInfo(@Param("quizId") Integer quizId,
                           @Param("title") String title,
                           @Param("description") String description,
                           @Param("timeLimit") Integer timeLimit,
                           @Param("allowMultipleAttempts") Boolean allowMultipleAttempts);
}
