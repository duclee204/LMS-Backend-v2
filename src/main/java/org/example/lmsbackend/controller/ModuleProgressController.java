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

    @GetMapping("/module/{moduleId}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> getModuleProgress(
            @PathVariable Integer moduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            Integer userId = userDetails.getUserId();
            ModuleProgress progress = moduleProgressService.getModuleProgress(userId, moduleId);
            
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error getting module progress: " + e.getMessage()
            ));
        }
    }

    // Individual Content Progress Tracking
    @PostMapping("/content-progress/{contentId}/viewed")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> markContentAsViewed(
            @PathVariable Integer contentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            Integer userId = userDetails.getUserId();
            moduleProgressService.markContentAsViewed(userId, contentId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Content marked as viewed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error marking content as viewed: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/content-progress/{contentId}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> getContentProgress(
            @PathVariable Integer contentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            Integer userId = userDetails.getUserId();
            Map<String, Object> progress = moduleProgressService.getContentProgress(userId, contentId);
            
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error getting content progress: " + e.getMessage()
            ));
        }
    }

    // Individual Video Progress Tracking
    @PostMapping("/video-progress/{videoId}/watch")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> updateVideoWatchProgress(
            @PathVariable Integer videoId,
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            Integer userId = userDetails.getUserId();
            Double watchedDuration = ((Number) request.get("watchedDuration")).doubleValue();
            Double totalDuration = ((Number) request.get("totalDuration")).doubleValue();
            Double watchedPercentage = ((Number) request.get("watchedPercentage")).doubleValue();
            Boolean completed = (Boolean) request.get("completed");
            
            moduleProgressService.updateVideoWatchProgress(userId, videoId, watchedDuration, totalDuration, watchedPercentage, completed);
            
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

    @GetMapping("/video-progress/{videoId}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> getVideoProgress(
            @PathVariable Integer videoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            Integer userId = userDetails.getUserId();
            Map<String, Object> progress = moduleProgressService.getVideoProgress(userId, videoId);
            
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error getting video progress: " + e.getMessage()
            ));
        }
    }
}
