package org.example.lmsbackend.controller;

import org.example.lmsbackend.model.ModuleProgress;
import org.example.lmsbackend.service.ModuleProgressService;
import org.example.lmsbackend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/module-progress")
public class ModuleProgressController {

    @Autowired
    private ModuleProgressService moduleProgressService;

    @PostMapping("/content/{moduleId}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> updateContentProgress(
            @PathVariable Integer moduleId,
            @RequestBody Map<String, Boolean> request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            Integer userId = userDetails.getUserId();
            Boolean completed = request.get("completed");
            
            moduleProgressService.updateContentProgress(userId, moduleId, completed);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Content progress updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error updating content progress: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/video/{moduleId}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> updateVideoProgress(
            @PathVariable Integer moduleId,
            @RequestBody Map<String, Boolean> request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            Integer userId = userDetails.getUserId();
            Boolean completed = request.get("completed");
            
            moduleProgressService.updateVideoProgress(userId, moduleId, completed);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Video progress updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error updating video progress: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/test/{moduleId}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> updateTestProgress(
            @PathVariable Integer moduleId,
            @RequestBody Map<String, Boolean> request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            Integer userId = userDetails.getUserId();
            Boolean completed = request.get("completed");
            
            moduleProgressService.updateTestProgress(userId, moduleId, completed);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test progress updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error updating test progress: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/test-unlock/{moduleId}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> checkTestUnlock(
            @PathVariable Integer moduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            Integer userId = userDetails.getUserId();
            boolean unlocked = moduleProgressService.isTestUnlocked(userId, moduleId);
            
            return ResponseEntity.ok(Map.of(
                "unlocked", unlocked
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error checking test unlock status: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> getCourseProgress(
            @PathVariable Integer courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            Integer userId = userDetails.getUserId();
            List<ModuleProgress> progressList = moduleProgressService.getUserProgressInCourse(userId, courseId);
            
            return ResponseEntity.ok(progressList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error getting course progress: " + e.getMessage()
            ));
        }
    }
}
