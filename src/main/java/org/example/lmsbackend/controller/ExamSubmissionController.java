package org.example.lmsbackend.controller;

import org.example.lmsbackend.service.ExamSubmissionService;
import org.example.lmsbackend.dto.ExamSubmissionDTO;
import org.example.lmsbackend.dto.QuizResultDTO;
import org.example.lmsbackend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exam")
public class ExamSubmissionController {

    @Autowired
    private ExamSubmissionService examSubmissionService;

    /**
     * Submit exam answers and get result immediately for multiple choice
     */
    @PostMapping("/submit")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> submitExam(@RequestBody ExamSubmissionDTO submissionDTO,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // Check if user has already submitted this quiz
            boolean hasSubmitted = examSubmissionService.hasUserSubmittedQuiz(
                userDetails.getUserId(), submissionDTO.getQuizId());
            
            if (hasSubmitted) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Bạn đã hoàn thành bài thi này rồi.",
                    "success", false
                ));
            }

            // Submit exam and get result
            QuizResultDTO result = examSubmissionService.submitExam(
                userDetails.getUserId(), submissionDTO);

            // Get attempt count after submission
            int attemptCount = examSubmissionService.getUserAttemptCount(
                userDetails.getUserId(), submissionDTO.getQuizId());

            return ResponseEntity.ok().body(Map.of(
                "message", "Nộp bài thành công!",
                "success", true,
                "result", result,
                "attemptCount", attemptCount
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Lỗi khi nộp bài: " + e.getMessage(),
                "success", false
            ));
        }
    }

    /**
     * Check if user has already submitted a quiz
     */
    @GetMapping("/check-submission/{quizId}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> checkSubmission(@PathVariable Integer quizId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("=== Check Submission Request ===");
            System.out.println("Quiz ID: " + quizId);
            System.out.println("User ID: " + (userDetails != null ? userDetails.getUserId() : "null"));
            System.out.println("User Roles: " + (userDetails != null ? userDetails.getAuthorities() : "null"));
            
            if (userDetails == null) {
                System.out.println("❌ User details is null - authentication failed");
                return ResponseEntity.status(401).body(Map.of(
                    "hasSubmitted", false,
                    "result", null,
                    "success", false,
                    "message", "Bạn cần đăng nhập để thực hiện thao tác này"
                ));
            }
            
            boolean hasSubmitted = examSubmissionService.hasUserSubmittedQuiz(
                userDetails.getUserId(), quizId);
            
            QuizResultDTO result = null;
            int attemptCount = 0;
            List<org.example.lmsbackend.model.UserQuizAttempt> attempts = new ArrayList<>();
            
            // Get attempt information
            attemptCount = examSubmissionService.getUserAttemptCount(userDetails.getUserId(), quizId);
            attempts = examSubmissionService.getUserAttempts(userDetails.getUserId(), quizId);
            
            if (hasSubmitted) {
                result = examSubmissionService.getUserQuizResult(userDetails.getUserId(), quizId);
                System.out.println("✅ User has submitted. Result found: " + (result != null));
            } else {
                System.out.println("📝 User hasn't submitted yet");
            }

            return ResponseEntity.ok().body(Map.of(
                "hasSubmitted", hasSubmitted,
                "result", result,
                "success", true,
                "attemptCount", attemptCount,
                "attempts", attempts
            ));

        } catch (Exception e) {
            System.out.println("❌ Error in checkSubmission: " + e.getMessage());
            e.printStackTrace();
            // Even if there's an error, assume user hasn't submitted yet
            // This is normal for users who haven't taken the exam
            return ResponseEntity.ok().body(Map.of(
                "hasSubmitted", false,
                "result", null,
                "success", true,
                "message", "Có thể làm bài thi"
            ));
        }
    }

    /**
     * Get user's exam result for review
     */
    @GetMapping("/result/{quizId}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> getExamResult(@PathVariable Integer quizId,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            QuizResultDTO result = examSubmissionService.getUserQuizResult(
                userDetails.getUserId(), quizId);
            
            if (result == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Lỗi khi lấy kết quả bài thi: " + e.getMessage(),
                "success", false
            ));
        }
    }
}
