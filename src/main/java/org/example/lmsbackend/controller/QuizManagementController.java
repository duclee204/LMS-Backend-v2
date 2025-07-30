package org.example.lmsbackend.controller;

import org.example.lmsbackend.service.QuizResultService;
import org.example.lmsbackend.service.QuestionsService;
import org.example.lmsbackend.dto.QuestionsDTO;
import org.example.lmsbackend.dto.QuizResultDTO;
import org.example.lmsbackend.dto.QuizEditDTO;
import org.example.lmsbackend.dto.QuizUpdateDTO;
import org.example.lmsbackend.model.Quizzes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller để xử lý chỉnh sửa bài thi và xem kết quả
 */
@RestController
@RequestMapping("/api/quiz-management")
public class QuizManagementController {

    @Autowired
    private QuestionsService questionsService;

    @Autowired
    private QuizResultService quizResultService;

    /**
     * Lấy toàn bộ câu hỏi và đáp án của một quiz để chỉnh sửa
     */
    @GetMapping("/quiz/{quizId}/edit")
    @PreAuthorize("hasAnyRole('admin', 'instructor')")
    public ResponseEntity<QuizEditDTO> getQuizForEdit(@PathVariable Integer quizId) {
        try {
            System.out.println("=== Quiz Edit Request ===");
            System.out.println("Quiz ID: " + quizId);
            System.out.println("Authentication: " + SecurityContextHolder.getContext().getAuthentication());
            
            // Lấy thông tin quiz
            Quizzes quiz = questionsService.getQuizById(quizId);
            if (quiz == null) {
                System.out.println("❌ Quiz not found with ID: " + quizId);
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("✅ Quiz found: " + quiz.getTitle());
            
            // Lấy tất cả câu hỏi với đáp án
            List<QuestionsDTO> questions = questionsService.getQuestionsWithAnswersByQuizId(quizId);
            System.out.println("✅ Found " + questions.size() + " questions");
            
            QuizEditDTO editData = new QuizEditDTO();
            editData.setQuizId(quizId);
            editData.setTitle(quiz.getTitle());
            editData.setDescription(quiz.getDescription());
            editData.setTimeLimit(quiz.getTimeLimit());
            editData.setAllowMultipleAttempts(quiz.getAllowMultipleAttempts());
            // Tạm thời bỏ qua totalMarks và maxAttempts vì không có trong model
            editData.setQuestions(questions);
            
            System.out.println("✅ Quiz edit data prepared successfully");
            return ResponseEntity.ok(editData);
            
        } catch (Exception e) {
            System.out.println("❌ Error in getQuizForEdit: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Cập nhật toàn bộ quiz (thông tin cơ bản + câu hỏi + đáp án)
     */
    @PutMapping("/quiz/{quizId}/update")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> updateQuizComplete(
            @PathVariable Integer quizId,
            @RequestBody QuizUpdateDTO updateData) {
        try {
            System.out.println("=== Quiz Update Request ===");
            System.out.println("Quiz ID: " + quizId);
            System.out.println("Update Data: " + updateData.getTitle());
            
            // Cập nhật thông tin cơ bản của quiz
            boolean quizUpdated = questionsService.updateQuizBasicInfo(quizId, updateData);
            
            if (!quizUpdated) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không thể cập nhật thông tin quiz"
                ));
            }
            
            // Cập nhật từng câu hỏi
            for (QuestionsDTO question : updateData.getQuestions()) {
                if (question.getQuestionId() != null) {
                    // Cập nhật câu hỏi có sẵn
                    questionsService.updateQuestion(question.getQuestionId(), question);
                } else {
                    // Thêm câu hỏi mới
                    question.setQuizId(quizId);
                    questionsService.createQuestion(question);
                }
            }
            
            // Xóa các câu hỏi không còn trong danh sách (nếu có)
            // TODO: Implement logic để xóa câu hỏi đã bị xóa
            
            System.out.println("✅ Quiz updated successfully");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật bài thi thành công"
            ));
            
        } catch (Exception e) {
            System.out.println("❌ Error updating quiz: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Lỗi khi cập nhật bài thi: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Xóa một câu hỏi khỏi quiz
     */
    @DeleteMapping("/question/{questionId}")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Integer questionId) {
        try {
            System.out.println("=== Delete Question Request ===");
            System.out.println("Question ID: " + questionId);
            
            questionsService.deleteQuestion(questionId);
            
            System.out.println("✅ Question deleted successfully");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Xóa câu hỏi thành công"
            ));
        } catch (Exception e) {
            System.out.println("❌ Error deleting question: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Lỗi khi xóa câu hỏi: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Lấy kết quả chi tiết của một attempt
     */
    @GetMapping("/attempt/{attemptId}/result")
    @PreAuthorize("hasAnyRole('instructor', 'admin') or @userService.isOwnerOfAttempt(authentication.name, #attemptId)")
    public ResponseEntity<QuizResultDTO> getAttemptResult(@PathVariable Integer attemptId) {
        try {
            QuizResultDTO result = quizResultService.getAttemptResult(attemptId);
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
