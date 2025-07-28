package org.example.lmsbackend.controller;

import org.example.lmsbackend.dto.GradeDTO;
import org.example.lmsbackend.model.UserAnswer;
import org.example.lmsbackend.service.GradesService;
import org.example.lmsbackend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
public class GradesController {

    @Autowired
    private GradesService gradesService;

    /**
     * Get all grades for instructor/admin (both multiple choice and essay)
     */
    @GetMapping("/instructor/{courseId}")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> getInstructorGrades(@PathVariable Integer courseId,
                                                @RequestParam(defaultValue = "ALL") String type,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("=== Instructor Grades Request ===");
            System.out.println("Course ID: " + courseId);
            System.out.println("Type: " + type);
            System.out.println("User ID: " + userDetails.getUserId());

            List<GradeDTO> grades = gradesService.getInstructorGrades(courseId, type);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "grades", grades
            ));

        } catch (Exception e) {
            System.out.println("❌ Error getting instructor grades: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Lỗi khi lấy danh sách điểm: " + e.getMessage()
            ));
        }
    }

    /**
     * Get grades for a specific student
     */
    @GetMapping("/student")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> getStudentGrades(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("=== Student Grades Request ===");
            System.out.println("User ID: " + userDetails.getUserId());

            List<GradeDTO> grades = gradesService.getStudentGrades(userDetails.getUserId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "grades", grades
            ));

        } catch (Exception e) {
            System.out.println("❌ Error getting student grades: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Lỗi khi lấy điểm của sinh viên: " + e.getMessage()
            ));
        }
    }

    /**
     * Grade an essay question
     */
    @PostMapping("/grade-essay")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> gradeEssay(@RequestBody Map<String, Object> gradeData,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("=== Grade Essay Request ===");
            System.out.println("Grade Data: " + gradeData);

            Integer userAnswerId = (Integer) gradeData.get("userAnswerId");
            Integer score = (Integer) gradeData.get("score");
            String feedback = (String) gradeData.get("feedback");

            boolean success = gradesService.gradeEssayAnswer(userAnswerId, score, feedback);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Chấm điểm thành công"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không thể chấm điểm"
                ));
            }

        } catch (Exception e) {
            System.out.println("❌ Error grading essay: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Lỗi khi chấm điểm: " + e.getMessage()
            ));
        }
    }

    /**
     * Get detailed essay answer for grading
     */
    @GetMapping("/essay-details/{userAnswerId}")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> getEssayDetails(@PathVariable Integer userAnswerId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("=== Get Essay Details Request ===");
            System.out.println("User Answer ID: " + userAnswerId);

            UserAnswer answer = gradesService.getEssayAnswerDetails(userAnswerId);
            
            if (answer != null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "essayDetails", answer
                ));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.out.println("❌ Error getting essay details: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Lỗi khi tải chi tiết bài làm: " + e.getMessage()
            ));
        }
    }
}
