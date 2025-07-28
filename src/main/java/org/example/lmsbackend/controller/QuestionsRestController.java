package org.example.lmsbackend.controller;

import org.example.lmsbackend.service.CourseService;
import org.example.lmsbackend.dto.QuestionsDTO;
import org.example.lmsbackend.service.QuestionsService;
import org.example.lmsbackend.service.QuizzesService;
import org.example.lmsbackend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionsRestController {

    @Autowired
    private QuestionsService     questionsService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private QuizzesService quizzesService;

    @PostMapping
    @PreAuthorize("hasAnyRole('admin', 'instructor')") 
    public ResponseEntity<?> createQuestion(@RequestBody QuestionsDTO dto,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        // ✅ Enhanced debug logging để kiểm tra dữ liệu từ frontend
        System.out.println("=== DEBUG: Main Create Question Request ===");
        System.out.println("User: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("User Role: " + (userDetails != null ? userDetails.getAuthorities() : "null"));
        System.out.println("Quiz ID: " + dto.getQuizId());
        System.out.println("Question Text: '" + dto.getQuestionText() + "'");
        System.out.println("Question Text Length: " + (dto.getQuestionText() != null ? dto.getQuestionText().length() : "null"));
        System.out.println("Type: " + dto.getType());
        System.out.println("Points: " + dto.getPoints());
        System.out.println("Answers count: " + (dto.getAnswers() != null ? dto.getAnswers().size() : "null"));
        System.out.println("=========================================");

        /*
        // ✅ Nếu là instructor, chỉ cho phép tạo câu hỏi nếu là người dạy course chứa quiz đó
        if (userDetails.hasRole("instructor")) {
            boolean isOwner = courseService.isInstructorOwnerOfQuiz(userDetails.getUserId(), dto.getQuizId());
            if (!isOwner) {
                return ResponseEntity.status(403).body("🚫 Bạn không có quyền tạo câu hỏi cho quiz này.");
            }
        }
        */

        // ✅ Tạo câu hỏi
        QuestionsDTO createdQuestion = questionsService.createQuestionWithReturn(dto);
        if (createdQuestion != null) {
            return ResponseEntity.ok().body(createdQuestion);
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "❌ Tạo câu hỏi thất bại.",
                "success", false
            ));
        }
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor')")
    public ResponseEntity<?> updateQuestion(@PathVariable Integer questionId,
                                           @RequestBody QuestionsDTO dto) {
        boolean updated = questionsService.updateQuestion(questionId, dto);
        if (updated) {
            return ResponseEntity.ok().body(Map.of(
                "message", "✅ Cập nhật câu hỏi thành công.",
                "success", true
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "❌ Cập nhật câu hỏi thất bại.",
                "success", false
            ));
        }
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Integer questionId) {
        boolean deleted = questionsService.deleteQuestion(questionId);
        if (deleted) {
            return ResponseEntity.ok().body(Map.of(
                "message", "✅ Xóa câu hỏi thành công.",
                "success", true
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "❌ Xóa câu hỏi thất bại.",
                "success", false
            ));
        }
    }

    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor')")
    public ResponseEntity<List<QuestionsDTO>> getQuestionsByQuizId(@PathVariable Integer quizId) {
        List<QuestionsDTO> questions = questionsService.getQuestionsWithAnswersByQuizId(quizId);
        return ResponseEntity.ok(questions);
    }

    // ✅ New endpoint for students to get question types only (for exam type detection)
    @GetMapping("/quiz/{quizId}/types")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<Map<String, Object>> getQuestionTypesByQuizId(@PathVariable Integer quizId) {
        System.out.println("🔍 Getting question types for quiz " + quizId);
        List<QuestionsDTO> questions = questionsService.getQuestionsWithAnswersByQuizId(quizId);
        
        List<String> questionTypes = questions.stream()
            .map(QuestionsDTO::getType)
            .distinct()
            .toList();
            
        System.out.println("📝 Found question types: " + questionTypes);
        
        return ResponseEntity.ok(Map.of(
            "questionTypes", questionTypes,
            "totalQuestions", questions.size()
        ));
    }

    @GetMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor')")
    public ResponseEntity<?> getQuestionById(@PathVariable Integer questionId) {
        QuestionsDTO question = questionsService.getQuestionWithAnswersById(questionId);
        if (question != null) {
            return ResponseEntity.ok(question);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Temporary endpoint for testing without authentication
    @PostMapping("/test")
    public ResponseEntity<?> createQuestionTest(@RequestBody QuestionsDTO dto) {
        
        // ✅ Debug logging để kiểm tra dữ liệu từ frontend
        System.out.println("=== DEBUG: Test Question Creation ===");
        System.out.println("Quiz ID: " + dto.getQuizId());
        System.out.println("Question Text: '" + dto.getQuestionText() + "'");
        System.out.println("Question Text Length: " + (dto.getQuestionText() != null ? dto.getQuestionText().length() : "null"));
        System.out.println("Type: " + dto.getType());
        System.out.println("Points: " + dto.getPoints());
        System.out.println("Answers count: " + (dto.getAnswers() != null ? dto.getAnswers().size() : "null"));
        System.out.println("=====================================");

        // ✅ Tạo câu hỏi
        boolean created = questionsService.createQuestion(dto);
        if (created) {
            return ResponseEntity.ok().body(Map.of(
                "message", "✅ Tạo câu hỏi thành công.",
                "success", true
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "❌ Tạo câu hỏi thất bại.",
                "success", false
            ));
        }
    }
}
