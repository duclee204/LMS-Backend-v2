package org.example.lmsbackend.repository;

import org.apache.ibatis.annotations.*;
import org.example.lmsbackend.model.Answer;

import java.util.List;

@Mapper
public interface AnswerMapper {

    // Thêm answer cho câu hỏi trắc nghiệm
    @Insert("""
        INSERT INTO answers (question_id, answer_text, is_correct, order_number)
        VALUES (#{question.questionId}, #{answerText}, #{isCorrect}, #{orderNumber})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "answerId")
    void insertAnswer(Answer answer);

    // Lấy danh sách answers theo question_id
    @Select("""
        SELECT answer_id, question_id, answer_text, is_correct, order_number
        FROM answers
        WHERE question_id = #{questionId}
        ORDER BY order_number ASC
    """)
    @Results({
            @Result(property = "answerId", column = "answer_id"),
            @Result(property = "answerText", column = "answer_text"),
            @Result(property = "isCorrect", column = "is_correct"),
            @Result(property = "orderNumber", column = "order_number")
    })
    List<Answer> findByQuestionId(@Param("questionId") Integer questionId);

    // Cập nhật answer
    @Update("""
        UPDATE answers
        SET answer_text = #{answerText},
            is_correct = #{isCorrect},
            order_number = #{orderNumber}
        WHERE answer_id = #{answerId}
    """)
    void updateAnswer(Answer answer);

    // Xóa all answers của một câu hỏi (khi delete question)
    @Delete("DELETE FROM answers WHERE question_id = #{questionId}")
    void deleteByQuestionId(@Param("questionId") Integer questionId);

    // Xóa answer theo ID
    @Delete("DELETE FROM answers WHERE answer_id = #{answerId}")
    void deleteAnswer(@Param("answerId") Integer answerId);

    // Lấy answer theo ID
    @Select("SELECT answer_id, question_id, answer_text, is_correct, order_number " +
            "FROM answers WHERE answer_id = #{answerId}")
    @Results({
            @Result(property = "answerId", column = "answer_id"),
            @Result(property = "answerText", column = "answer_text"),
            @Result(property = "isCorrect", column = "is_correct"),
            @Result(property = "orderNumber", column = "order_number")
    })
    Answer findById(@Param("answerId") Integer answerId);
}
