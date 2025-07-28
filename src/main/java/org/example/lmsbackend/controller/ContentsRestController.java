package org.example.lmsbackend.controller;

import org.example.lmsbackend.dto.ContentResponseDTO;
import org.example.lmsbackend.dto.ContentRequestDTO;
import org.example.lmsbackend.model.Content;
import org.example.lmsbackend.service.ContentService;
import org.example.lmsbackend.service.CourseService;
import org.example.lmsbackend.service.ModulesService;
import org.example.lmsbackend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/contents")
public class ContentsRestController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ModulesService moduleService;

    // Helper method to check if user is admin
    private boolean isAdmin(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));
    }

    // ✅ Tạo content mới cho module
    @PostMapping
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> createContent(@RequestParam("moduleId") int moduleId,
                                          @RequestParam("title") String title,
                                          @RequestParam("contentType") String contentType,
                                          @RequestParam("orderNumber") int orderNumber,
                                          @RequestParam("isPublished") boolean isPublished,
                                          @RequestParam(value = "description", required = false) String description,
                                          @RequestParam(value = "contentUrl", required = false) String contentUrl,
                                          @RequestParam(value = "file", required = false) MultipartFile file,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // Only check ownership for instructors, admin can access all modules
            if (!isAdmin(userDetails)) {
                moduleService.ensureInstructorOwnsModule(moduleId, userDetails.getUsername());
            }
            
            Content content = contentService.createContent(
                moduleId, title, contentType, description, contentUrl, orderNumber, isPublished, file
            );
            
            // Convert to DTO for response
            ContentResponseDTO dto = new ContentResponseDTO();
            dto.setContentId(content.getId());
            dto.setModuleId(content.getModule().getId());
            dto.setTitle(content.getTitle());
            dto.setType(content.getType());
            dto.setContentUrl(content.getContentUrl());
            dto.setFileName(content.getFileName());
            dto.setOrderNumber(content.getOrderNumber());
            dto.setPublished(content.isPublished());
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Upload tài liệu cho module
    @PostMapping("/{moduleId}/documents")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> uploadDocument(@PathVariable int moduleId,
                                          @RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            contentService.uploadDocument(userDetails.getUsername(), moduleId, file);
            return ResponseEntity.ok("Upload successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Lấy danh sách content theo courseId
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<List<ContentResponseDTO>> getContentsByCourse(@PathVariable int courseId,
                                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Kiểm tra quyền instructor
        boolean isOwner = courseService.isInstructorOfCourse(userDetails.getId(), courseId);
        if (!isOwner) {
            return ResponseEntity.status(403).body(null);
        }

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

    // ✅ Cập nhật trạng thái content
    @PutMapping("/{contentId}/status")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> updateContentStatus(@PathVariable int contentId,
                                                @RequestParam boolean published,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            contentService.updateContentStatus(contentId, published, userDetails.getUsername());
            return ResponseEntity.ok("Cập nhật trạng thái tài liệu thành công: " + (published ? "Published" : "Not Published"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Cập nhật content (chỉ thông tin, không lưu file mới)
    @PutMapping("/{contentId}/info")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> updateContentInfo(@PathVariable int contentId,
                                              @RequestBody ContentRequestDTO requestDto,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // Lấy content hiện tại để kiểm tra quyền
            Content existingContent = contentService.getContentById(contentId);
            if (existingContent == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Only check ownership for instructors, admin can access all modules
            if (!isAdmin(userDetails)) {
                moduleService.ensureInstructorOwnsModule(existingContent.getModule().getId(), userDetails.getUsername());
            }
            
            // Cập nhật content (chỉ thông tin, không thay đổi file/URL)
            Content updatedContent = contentService.updateContentInfo(
                contentId, requestDto.getTitle(), requestDto.getContentType(), 
                requestDto.getDescription(), requestDto.getOrderNumber(), requestDto.isPublished()
            );
            
            // Convert to DTO for response
            ContentResponseDTO dto = new ContentResponseDTO();
            dto.setContentId(updatedContent.getId());
            dto.setModuleId(updatedContent.getModule().getId());
            dto.setTitle(updatedContent.getTitle());
            dto.setType(updatedContent.getType());
            dto.setContentUrl(updatedContent.getContentUrl());
            dto.setFileName(updatedContent.getFileName());
            dto.setOrderNumber(updatedContent.getOrderNumber());
            dto.setPublished(updatedContent.isPublished());
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Cập nhật content với URL mới (có thể thay đổi URL/file)
    @PutMapping("/{contentId}")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> updateContent(@PathVariable int contentId,
                                         @RequestBody ContentRequestDTO requestDto,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // Lấy content hiện tại để kiểm tra quyền
            Content existingContent = contentService.getContentById(contentId);
            if (existingContent == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Only check ownership for instructors, admin can access all modules
            if (!isAdmin(userDetails)) {
                moduleService.ensureInstructorOwnsModule(existingContent.getModule().getId(), userDetails.getUsername());
            }
            
            // Cập nhật content
            Content updatedContent = contentService.updateContent(
                contentId, requestDto.getTitle(), requestDto.getContentType(), 
                requestDto.getDescription(), requestDto.getOrderNumber(), requestDto.isPublished(),
                requestDto.getContentUrl() // Truyền contentUrl
            );
            
            // Convert to DTO for response
            ContentResponseDTO dto = new ContentResponseDTO();
            dto.setContentId(updatedContent.getId());
            dto.setModuleId(updatedContent.getModule().getId());
            dto.setTitle(updatedContent.getTitle());
            dto.setType(updatedContent.getType());
            dto.setContentUrl(updatedContent.getContentUrl());
            dto.setFileName(updatedContent.getFileName());
            dto.setOrderNumber(updatedContent.getOrderNumber());
            dto.setPublished(updatedContent.isPublished());
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Upload file mới cho content hiện có
    @PutMapping("/{contentId}/file")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> updateContentFile(@PathVariable int contentId,
                                              @RequestParam("title") String title,
                                              @RequestParam("contentType") String contentType,
                                              @RequestParam("orderNumber") int orderNumber,
                                              @RequestParam("isPublished") boolean isPublished,
                                              @RequestParam(value = "description", required = false) String description,
                                              @RequestParam("file") MultipartFile file,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // Lấy content hiện tại để kiểm tra quyền
            Content existingContent = contentService.getContentById(contentId);
            if (existingContent == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Only check ownership for instructors, admin can access all modules
            if (!isAdmin(userDetails)) {
                moduleService.ensureInstructorOwnsModule(existingContent.getModule().getId(), userDetails.getUsername());
            }
            
            // Cập nhật content với file mới
            Content updatedContent = contentService.updateContentWithFile(
                contentId, title, contentType, description, orderNumber, isPublished, file
            );
            
            // Convert to DTO for response
            ContentResponseDTO dto = new ContentResponseDTO();
            dto.setContentId(updatedContent.getId());
            dto.setModuleId(updatedContent.getModule().getId());
            dto.setTitle(updatedContent.getTitle());
            dto.setType(updatedContent.getType());
            dto.setContentUrl(updatedContent.getContentUrl());
            dto.setFileName(updatedContent.getFileName());
            dto.setOrderNumber(updatedContent.getOrderNumber());
            dto.setPublished(updatedContent.isPublished());
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Xóa content
    @DeleteMapping("/{contentId}")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<?> deleteContent(@PathVariable int contentId,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // Lấy content để kiểm tra quyền
            Content content = contentService.getContentById(contentId);
            if (content == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Only check ownership for instructors, admin can access all modules
            if (!isAdmin(userDetails)) {
                moduleService.ensureInstructorOwnsModule(content.getModule().getId(), userDetails.getUsername());
            }
            
            // Xóa content
            contentService.deleteContent(contentId);
            
            return ResponseEntity.ok("Xóa content thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Lấy danh sách content theo moduleId
    @GetMapping("/module/{moduleId}")
    @PreAuthorize("hasAnyRole('instructor', 'admin')")
    public ResponseEntity<List<ContentResponseDTO>> getContentsByModule(@PathVariable int moduleId,
                                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Only check ownership for instructors, admin can access all modules
        if (!isAdmin(userDetails)) {
            moduleService.ensureInstructorOwnsModule(moduleId, userDetails.getUsername());
        }
        
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

    // ✅ STUDENT API: Lấy danh sách content đã xuất bản theo moduleId
    @GetMapping("/module/{moduleId}/published")
    @PreAuthorize("hasRole('student') or hasRole('instructor') or hasRole('admin')")
    public ResponseEntity<List<ContentResponseDTO>> getPublishedContentsByModule(@PathVariable int moduleId) {
        List<Content> contents = contentService.getPublishedContentsByModuleId(moduleId);
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
}
