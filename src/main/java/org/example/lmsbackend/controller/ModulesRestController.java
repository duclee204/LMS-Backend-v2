package org.example.lmsbackend.controller;

import org.example.lmsbackend.dto.ModulesDTO;
import org.example.lmsbackend.dto.ContentResponseDTO;
import org.example.lmsbackend.dto.ModuleResponseDTO;
import org.example.lmsbackend.dto.VideoDTO;
import org.example.lmsbackend.model.Modules;
import org.example.lmsbackend.model.Content;
import org.example.lmsbackend.model.ModuleProgress;
import org.example.lmsbackend.model.Quizzes;
import org.example.lmsbackend.service.ContentService;
import org.example.lmsbackend.service.ModulesService;
import org.example.lmsbackend.service.ModuleProgressService;
import org.example.lmsbackend.service.VideoService;
import org.example.lmsbackend.service.QuizzesService;
import org.example.lmsbackend.repository.ContentRepository;
import org.example.lmsbackend.repository.VideoRepository;
import org.example.lmsbackend.repository.QuizzesRepository;
import org.example.lmsbackend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/modules")
public class ModulesRestController {

    @Autowired
    private ModulesService moduleService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ModuleProgressService moduleProgressService;
    
    @Autowired
    private VideoService videoService;
    
    @Autowired
    private QuizzesService quizzesService;
    
    @Autowired
    private ContentRepository contentRepository;
    
    @Autowired
    private VideoRepository videoRepository;
    
    @Autowired
    private QuizzesRepository quizzesRepository;

    // ✅ Tạo module mới
    @PostMapping
    @PreAuthorize("hasRole('instructor') or hasRole('admin')")
    public ResponseEntity<?> createModule(@RequestBody ModulesDTO dto, Principal principal) {
        Modules module = moduleService.createModule(principal.getName(), dto);

        ModuleResponseDTO response = new ModuleResponseDTO();
        response.setModuleId(module.getId());
        response.setTitle(module.getTitle());
        response.setDescription(module.getDescription());
        response.setOrderNumber(module.getOrderNumber());
        response.setCourseId(module.getCourse().getCourseId());
        response.setCourseTitle(module.getCourse().getTitle());
        response.setPublished(module.isPublished());

        return ResponseEntity.ok(response);
    }

    // ✅ Tạo module mới cho course cụ thể
    @PostMapping("/{courseId}")
    @PreAuthorize("hasRole('instructor') or hasRole('admin')")
    public ResponseEntity<?> createModuleForCourse(@PathVariable int courseId, @RequestBody ModulesDTO dto, Principal principal) {
        // Set courseId in DTO
        dto.setCourseId(courseId);
        Modules module = moduleService.createModule(principal.getName(), dto);

        ModuleResponseDTO response = new ModuleResponseDTO();
        response.setModuleId(module.getId());
        response.setTitle(module.getTitle());
        response.setDescription(module.getDescription());
        response.setOrderNumber(module.getOrderNumber());
        response.setCourseId(module.getCourse().getCourseId());
        response.setCourseTitle(module.getCourse().getTitle());
        response.setPublished(module.isPublished());

        return ResponseEntity.ok(response);
    }

    // ✅ Upload tài liệu cho module → sử dụng Content
    @PostMapping("/{moduleId}/documents")
    @PreAuthorize("hasRole('instructor')")
    public ResponseEntity<?> upload(@PathVariable int moduleId,
                                    @RequestParam("file") MultipartFile file,
                                    Principal principal) {
        try {
            contentService.uploadDocument(principal.getName(), moduleId, file);
            return ResponseEntity.ok().body(Map.of(
                "message", "Upload successful",
                "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Error: " + e.getMessage(),
                "success", false
            ));
        }
    }

    // ✅ Lấy danh sách module theo courseId
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('instructor') or hasRole('admin')")
    public ResponseEntity<List<ModuleResponseDTO>> getModulesByCourseId(@PathVariable int courseId,
                                                                        Principal principal) {
        // Only check instructor ownership if user is instructor, admin can access all
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_admin"));
        
        if (!isAdmin) {
            moduleService.ensureInstructorOwnsCourse(courseId, principal.getName());
        }

        List<Modules> modules = moduleService.getModulesByCourseId(courseId);
        List<ModuleResponseDTO> dtos = modules.stream().map(module -> {
            ModuleResponseDTO dto = new ModuleResponseDTO();
            dto.setModuleId(module.getId());
            dto.setTitle(module.getTitle());
            dto.setDescription(module.getDescription());
            dto.setOrderNumber(module.getOrderNumber());
            dto.setPublished(module.isPublished());
            dto.setCourseId(module.getCourse().getCourseId());
            dto.setCourseTitle(module.getCourse().getTitle());
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    // ✅ Cập nhật module
    @PutMapping("/{moduleId}")
    @PreAuthorize("hasRole('instructor') or hasRole('admin')")
    public ResponseEntity<?> updateModule(@PathVariable int moduleId,
                                        @RequestBody ModulesDTO dto,
                                        Principal principal) {
        try {
            // Kiểm tra quyền
            moduleService.ensureInstructorOwnsModule(moduleId, principal.getName());
            
            // Cập nhật module
            Modules updatedModule = moduleService.updateModule(moduleId, dto, principal.getName());
            
            // Convert to response DTO
            ModuleResponseDTO response = new ModuleResponseDTO();
            response.setModuleId(updatedModule.getId());
            response.setTitle(updatedModule.getTitle());
            response.setDescription(updatedModule.getDescription());
            response.setOrderNumber(updatedModule.getOrderNumber());
            response.setPublished(updatedModule.isPublished());
            response.setCourseId(updatedModule.getCourse().getCourseId());
            response.setCourseTitle(updatedModule.getCourse().getTitle());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Xóa module
    @DeleteMapping("/{moduleId}")
    @PreAuthorize("hasRole('instructor') or hasRole('admin')")
    public ResponseEntity<?> deleteModule(@PathVariable int moduleId,
                                        Principal principal) {
        try {
            // Kiểm tra quyền
            moduleService.ensureInstructorOwnsModule(moduleId, principal.getName());
            
            // Xóa module
            moduleService.deleteModule(moduleId);
            
            return ResponseEntity.ok().body(Map.of(
                "message", "Xóa module thành công",
                "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Error: " + e.getMessage(),
                "success", false
            ));
        }
    }

    // ✅ Cập nhật trạng thái module
    @PutMapping("/{moduleId}/status")
    @PreAuthorize("hasRole('instructor') or hasRole('admin')")
    public ResponseEntity<?> updateStatus(@PathVariable int moduleId,
                                          @RequestParam boolean published,
                                          Principal principal) {
        try {
            Modules updatedModule = moduleService.updateModuleStatus(moduleId, published, principal.getName());
            
            // Đếm số content đã được cập nhật
            int totalContentCount = updatedModule.getContents().size();
            int publishedContentCount = 0;
            for (Content content : updatedModule.getContents()) {
                if (content.isPublished()) {
                    publishedContentCount++;
                }
            }
            
            // TODO: Đếm video và quiz (cần thêm method để lấy từ service)
            int totalVideoCount = 0; // Placeholder - cần implement
            int totalQuizCount = 0;  // Placeholder - cần implement
            
            return ResponseEntity.ok().body(Map.of(
                "message", "Cập nhật trạng thái thành công: " + (published ? "Published" : "Not Published"),
                "success", true,
                "published", published,
                "moduleId", moduleId,
                "totalContentCount", totalContentCount,
                "publishedContentCount", publishedContentCount,
                "totalVideoCount", totalVideoCount,
                "totalQuizCount", totalQuizCount,
                "details", published ? 
                    "Module và tất cả nội dung bên trong (content, video, quiz) đã được xuất bản" :
                    "Module và tất cả nội dung bên trong (content, video, quiz) đã được ẩn"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi khi cập nhật trạng thái: " + e.getMessage()
            ));
        }
    }

    // ✅ Cập nhật trạng thái content
    @PutMapping("/contents/{contentId}/status")
    @PreAuthorize("hasRole('instructor')")
    public ResponseEntity<?> updateContentStatus(@PathVariable int contentId,
                                                 @RequestParam boolean published,
                                                 Principal principal) {
        try {
            contentService.updateContentStatus(contentId, published, principal.getName());
            return ResponseEntity.ok().body(Map.of(
                "message", "Cập nhật trạng thái tài liệu thành công: " + (published ? "Published" : "Not Published"),
                "success", true,
                "published", published
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Error: " + e.getMessage(),
                "success", false
            ));
        }
    }

    // ✅ Lấy danh sách content theo module
    @GetMapping("/{moduleId}/contents")
    @PreAuthorize("hasRole('instructor')")
    public ResponseEntity<List<ContentResponseDTO>> getContentsByModule(@PathVariable int moduleId,
                                                                        Principal principal) {
        moduleService.ensureInstructorOwnsModule(moduleId, principal.getName());
        List<Content> contents = contentService.getContentsByModuleId(moduleId);

        List<ContentResponseDTO> response = contents.stream().map(content -> {
            ContentResponseDTO dto = new ContentResponseDTO();
            dto.setContentId(content.getId());
            dto.setModuleId(content.getModule().getId());
            dto.setTitle(content.getTitle());
            dto.setType(content.getType());
            dto.setContentUrl(content.getContentUrl());
            dto.setFileName(content.getFileName());
            dto.setDuration(content.getDuration());
            dto.setOrderNumber(content.getOrderNumber());
            dto.setPublished(content.isPublished());
            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // ✅ Lấy danh sách content theo course
    @GetMapping("/course/{courseId}/contents")
    @PreAuthorize("hasRole('instructor')")
    public ResponseEntity<List<ContentResponseDTO>> getContentsByCourse(@PathVariable int courseId,
                                                                        Principal principal) {
        moduleService.ensureInstructorOwnsCourse(courseId, principal.getName());
        List<Content> contents = contentService.getContentsByCourseId(courseId);

        List<ContentResponseDTO> response = contents.stream().map(content -> {
            ContentResponseDTO dto = new ContentResponseDTO();
            dto.setContentId(content.getId());
            dto.setModuleId(content.getModule().getId());
            dto.setTitle(content.getTitle());
            dto.setType(content.getType());
            dto.setContentUrl(content.getContentUrl());
            dto.setFileName(content.getFileName());
            dto.setDuration(content.getDuration());
            dto.setOrderNumber(content.getOrderNumber());
            dto.setPublished(content.isPublished());
            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // ✅ STUDENT API: Lấy danh sách module đã xuất bản theo courseId
    @GetMapping("/course/{courseId}/published")
    @PreAuthorize("hasRole('student') or hasRole('instructor') or hasRole('admin')")
    public ResponseEntity<List<ModuleResponseDTO>> getPublishedModulesByCourse(@PathVariable int courseId) {
        List<Modules> modules = moduleService.getPublishedModulesByCourseId(courseId);
        List<ModuleResponseDTO> dtos = modules.stream().map(module -> {
            ModuleResponseDTO dto = new ModuleResponseDTO();
            dto.setModuleId(module.getId());
            dto.setTitle(module.getTitle());
            dto.setDescription(module.getDescription());
            dto.setOrderNumber(module.getOrderNumber());
            dto.setPublished(module.isPublished());
            dto.setCourseId(module.getCourse().getCourseId());
            dto.setCourseTitle(module.getCourse().getTitle());
            
            // Add progress information for authenticated users
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
                try {
                    // Get userId from CustomUserDetails
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof CustomUserDetails userDetails) {
                        Integer userId = userDetails.getUserId();
                        System.out.println("🔍 Getting progress for userId: " + userId + ", moduleId: " + module.getId());
                        ModuleProgress progress = moduleProgressService.getModuleProgress(userId, module.getId());
                        if (progress != null) {
                            System.out.println("✅ Found progress: " + progress.getContentCompleted() + "/" + progress.getVideoCompleted() + "/" + progress.getTestCompleted());
                            
                            // Calculate completion percentage based on actual item counts
                            int totalItems = 0;
                            int completedItems = 0;
                            
                            // Count content items
                            int totalContentItems = getTotalContentItems(module);
                            int completedContentItems = getCompletedContentItems(module, userId);
                            totalItems += totalContentItems;
                            completedItems += completedContentItems;
                            
                            // Count video items  
                            int totalVideoItems = getTotalVideoItems(module);
                            int completedVideoItems = getCompletedVideoItems(module, userId);
                            totalItems += totalVideoItems;
                            completedItems += completedVideoItems;
                            
                            // Count quiz/test items
                            int totalQuizItems = getTotalQuizItems(module);
                            int completedQuizItems = getCompletedQuizItems(module, userId);
                            totalItems += totalQuizItems;
                            completedItems += completedQuizItems;
                            
                            // Set completion flags based on individual item completion
                            dto.setContentCompleted(totalContentItems > 0 && completedContentItems == totalContentItems);
                            dto.setVideoCompleted(totalVideoItems > 0 && completedVideoItems == totalVideoItems);
                            dto.setTestCompleted(totalQuizItems > 0 && completedQuizItems == totalQuizItems);
                            dto.setModuleCompleted(totalItems > 0 && completedItems == totalItems);
                            
                            // Calculate percentage based on actual completion ratio
                            dto.setCompletionPercentage(totalItems > 0 ? (double) completedItems * 100.0 / totalItems : 0.0);
                            
                            System.out.println("📊 Module " + module.getId() + " progress: " + completedItems + "/" + totalItems + 
                                " (" + String.format("%.1f", dto.getCompletionPercentage()) + "%)");
                            System.out.println("📝 Details - Content: " + completedContentItems + "/" + totalContentItems + 
                                ", Video: " + completedVideoItems + "/" + totalVideoItems + 
                                ", Quiz: " + completedQuizItems + "/" + totalQuizItems);
                            
                            dto.setCompletionPercentage(totalItems > 0 ? (double) completedItems * 100.0 / totalItems : 0.0);
                        } else {
                            dto.setContentCompleted(false);
                            dto.setVideoCompleted(false);
                            dto.setTestCompleted(false);
                            dto.setModuleCompleted(false);
                            dto.setCompletionPercentage(0.0);
                        }
                    } else {
                        dto.setContentCompleted(false);
                        dto.setVideoCompleted(false);
                        dto.setTestCompleted(false);
                        dto.setModuleCompleted(false);
                        dto.setCompletionPercentage(0.0);
                    }
                } catch (Exception e) {
                    // If error getting progress, set defaults
                    System.out.println("❌ Error getting progress for module " + module.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                    dto.setContentCompleted(false);
                    dto.setVideoCompleted(false);
                    dto.setTestCompleted(false);
                    dto.setModuleCompleted(false);
                    dto.setCompletionPercentage(0.0);
                }
            } else {
                dto.setContentCompleted(false);
                dto.setVideoCompleted(false);
                dto.setTestCompleted(false);
                dto.setModuleCompleted(false);
                dto.setCompletionPercentage(0.0);
            }
            
            return dto;
        }).toList();
        
        return ResponseEntity.ok(dtos);
    }
    
    // Helper methods to check what content types a module has
    private boolean hasContentItems(Modules module) {
        try {
            List<Content> contents = contentRepository.findByModuleIdAndPublishedTrueOrderByOrderNumber(module.getId());
            return contents != null && !contents.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean hasVideoItems(Modules module) {
        try {
            List<org.example.lmsbackend.model.Video> videos = videoRepository.findByModuleIdAndPublished(module.getId());
            return videos != null && !videos.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean hasQuizItems(Modules module) {
        try {
            List<Quizzes> quizzes = quizzesRepository.findByModuleIdAndPublishTrue(module.getId());
            return quizzes != null && !quizzes.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    // Methods to count total items in module
    private int getTotalContentItems(Modules module) {
        try {
            List<Content> contents = contentRepository.findByModuleIdAndPublishedTrueOrderByOrderNumber(module.getId());
            return contents != null ? contents.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int getTotalVideoItems(Modules module) {
        try {
            List<org.example.lmsbackend.model.Video> videos = videoRepository.findByModuleIdAndPublished(module.getId());
            return videos != null ? videos.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int getTotalQuizItems(Modules module) {
        try {
            List<Quizzes> quizzes = quizzesRepository.findByModuleIdAndPublishTrue(module.getId());
            return quizzes != null ? quizzes.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    // Methods to count completed items for a user
    private int getCompletedContentItems(Modules module, Integer userId) {
        try {
            List<Content> allContents = contentRepository.findByModuleIdAndPublishedTrueOrderByOrderNumber(module.getId());
            if (allContents == null || allContents.isEmpty()) return 0;
            
            int completedCount = 0;
            for (Content content : allContents) {
                // Check if user has completed this specific content
                boolean isCompleted = moduleProgressService.hasCompletedSpecificContent(userId, content.getId());
                if (isCompleted) {
                    completedCount++;
                }
            }
            return completedCount;
        } catch (Exception e) {
            System.err.println("❌ Error counting completed content items: " + e.getMessage());
            return 0;
        }
    }
    
    private int getCompletedVideoItems(Modules module, Integer userId) {
        try {
            List<org.example.lmsbackend.model.Video> allVideos = videoRepository.findByModuleIdAndPublished(module.getId());
            if (allVideos == null || allVideos.isEmpty()) return 0;
            
            int completedCount = 0;
            for (org.example.lmsbackend.model.Video video : allVideos) {
                // Check if user has completed this specific video
                boolean isCompleted = moduleProgressService.hasCompletedSpecificVideo(userId, video.getVideoId().intValue());
                if (isCompleted) {
                    completedCount++;
                }
            }
            return completedCount;
        } catch (Exception e) {
            System.err.println("❌ Error counting completed video items: " + e.getMessage());
            return 0;
        }
    }
    
    private int getCompletedQuizItems(Modules module, Integer userId) {
        try {
            List<Quizzes> allQuizzes = quizzesRepository.findByModuleIdAndPublishTrue(module.getId());
            if (allQuizzes == null || allQuizzes.isEmpty()) return 0;
            
            int completedCount = 0;
            for (Quizzes quiz : allQuizzes) {
                // Check if user has completed this specific quiz
                boolean isCompleted = moduleProgressService.hasCompletedSpecificQuiz(userId, quiz.getQuizId());
                if (isCompleted) {
                    completedCount++;
                }
            }
            return completedCount;
        } catch (Exception e) {
            System.err.println("❌ Error counting completed quiz items: " + e.getMessage());
            return 0;
        }
    }
}
