package org.example.lmsbackend.controller;

import org.example.lmsbackend.dto.QuizzesDTO;
import org.example.lmsbackend.model.Quizzes;
import org.example.lmsbackend.service.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
public class QuizzesRestController {

    @Autowired
    private QuizzesService quizService;

    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody QuizzesDTO dto) {
        Quizzes createdQuiz = quizService.createQuiz(dto);
        return ResponseEntity.ok().body(Map.of(
            "message", "Quiz created successfully", 
            "success", true,
            "quizId", createdQuiz.getQuizId(),
            "title", createdQuiz.getTitle()
        ));
    }

    @GetMapping
    public ResponseEntity<List<QuizzesDTO>> getAllQuizzes(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Boolean publish,
            @RequestParam(required = false) Boolean withoutModule) {
        if (courseId != null) {
            return ResponseEntity.ok(quizService.getQuizzesByCourse(courseId, publish, withoutModule));
        } else {
            return ResponseEntity.ok(quizService.getAllQuizzes());
        }
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuizById(@PathVariable Integer quizId) {
        QuizzesDTO quiz = quizService.getQuizById(quizId);
        if (quiz != null) {
            return ResponseEntity.ok(quiz);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{quizId}/with-questions")
    public ResponseEntity<?> getQuizWithQuestions(@PathVariable Integer quizId) {
        Map<String, Object> result = quizService.getQuizWithQuestions(quizId);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('admin', 'instructor')")
    public ResponseEntity<?> updateQuiz(@RequestBody QuizzesDTO dto) {
        quizService.updateQuiz(dto);
        return ResponseEntity.ok().body(Map.of("message", "Quiz updated successfully", "success", true));
    }

    @PutMapping("/{quizId}/status")
    @PreAuthorize("hasAnyRole('admin', 'instructor')")
    public ResponseEntity<?> updateQuizStatus(@PathVariable Integer quizId, @RequestParam boolean publish) {
        quizService.updateQuizStatus(quizId, publish);
        return ResponseEntity.ok().body(Map.of(
            "message", "Quiz status updated successfully", 
            "success", true,
            "published", publish
        ));
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor')")
    public ResponseEntity<?> deleteQuiz(@PathVariable Integer quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.ok().body(Map.of("message", "Quiz deleted successfully", "success", true));
    }

    @GetMapping("/{quizId}/course")
    public ResponseEntity<Integer> getCourseIdByQuizId(@PathVariable Integer quizId) {
        Integer courseId = quizService.getCourseIdByQuizId(quizId);
        if (courseId != null) {
            return ResponseEntity.ok(courseId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<QuizzesDTO>> getQuizzesByModule(
            @PathVariable Integer moduleId,
            @RequestParam(required = false) Boolean publish) {
        return ResponseEntity.ok(quizService.getQuizzesByModule(moduleId, publish));
    }
}