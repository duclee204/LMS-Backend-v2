package org.example.lmsbackend.controller;

import org.example.lmsbackend.dto.DiscussionDTO;
import org.example.lmsbackend.service.DiscussionService;
import org.example.lmsbackend.service.CourseService;
import org.example.lmsbackend.service.EnrollmentsService;
import org.example.lmsbackend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/discussions")
public class DiscussionController {
    @Autowired
    private DiscussionService discussionService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private EnrollmentsService enrollmentsService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public List<DiscussionDTO> getAllDiscussions(@RequestParam Integer courseId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Chỉ trả về thảo luận của khóa học mà user có quyền
        if (userDetails.hasRole("instructor")) {
            if (!courseService.isInstructorOfCourse(userDetails.getUserId(), courseId)) {
                return List.of();
            }
        }
        if (userDetails.hasRole("student")) {
            if (!enrollmentsService.isStudentEnrolled(userDetails.getUserId(), courseId)) {
                return List.of();
            }
        }
        // Use new method that filters by user permissions
        return discussionService.getDiscussionsForUser(courseId, userDetails.getUserId());
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public List<DiscussionDTO> getDiscussionsByCourse(@PathVariable Integer courseId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails.hasRole("instructor")) {
            if (!courseService.isInstructorOfCourse(userDetails.getUserId(), courseId)) {
                return List.of();
            }
        }
        if (userDetails.hasRole("student")) {
            if (!enrollmentsService.isStudentEnrolled(userDetails.getUserId(), courseId)) {
                return List.of();
            }
        }
        // Use new method that filters by user permissions  
        return discussionService.getDiscussionsForUser(courseId, userDetails.getUserId());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('instructor', 'student')")
    public ResponseEntity<DiscussionDTO> createDiscussion(@RequestBody DiscussionDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Kiểm tra instructor có dạy khóa này không
        if (userDetails.hasRole("instructor") && !courseService.isInstructorOfCourse(userDetails.getUserId(), dto.getCourseId())) {
            return ResponseEntity.status(403).build();
        }
        
        // Kiểm tra student có đăng ký khóa này không
        if (userDetails.hasRole("student") && !enrollmentsService.isStudentEnrolled(userDetails.getUserId(), dto.getCourseId())) {
            return ResponseEntity.status(403).build();
        }
        
        dto.setUserId(userDetails.getUserId()); // Gán userId đúng
        DiscussionDTO created = discussionService.createDiscussion(dto);
        if (created == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('instructor')")
    public ResponseEntity<DiscussionDTO> updateDiscussion(@PathVariable Integer id, @RequestBody DiscussionDTO dto) {
        DiscussionDTO updated = discussionService.updateDiscussion(id, dto);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<String> deleteDiscussion(@PathVariable Integer id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boolean deleted = discussionService.deleteDiscussion(id, userDetails.getUserId(), userDetails.hasRole("admin"));
            if (!deleted) {
                return ResponseEntity.status(403).body("{\"success\": false, \"message\": \"Bạn không có quyền xóa thảo luận này\"}");
            }
            return ResponseEntity.ok("{\"success\": true, \"message\": \"Xóa thảo luận thành công\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"success\": false, \"message\": \"Lỗi khi xóa thảo luận: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Upload file attachment for discussion
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('instructor', 'student')")
    public ResponseEntity<?> uploadDiscussionFile(@RequestParam("file") MultipartFile file,
                                                @RequestParam("courseId") Integer courseId,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("=== Discussion File Upload Request ===");
            System.out.println("Course ID: " + courseId);
            System.out.println("User ID: " + userDetails.getUserId());
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize() + " bytes");

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "File không được để trống"
                ));
            }

            // Check file size (10MB limit)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "File quá lớn. Kích thước tối đa là 10MB"
                ));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (!isValidFileType(contentType)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Loại file không được hỗ trợ. Chỉ chấp nhận PDF, DOC, DOCX, TXT, JPG, PNG"
                ));
            }

            // Check user permission for the course
            if (userDetails.hasRole("instructor") && !courseService.isInstructorOfCourse(userDetails.getUserId(), courseId)) {
                return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền upload file cho khóa học này"
                ));
            }
            
            if (userDetails.hasRole("student") && !enrollmentsService.isStudentEnrolled(userDetails.getUserId(), courseId)) {
                return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền upload file cho khóa học này"
                ));
            }

            // Create directory structure: uploads/discussions/{courseId}/{userId}
            String discussionDir = uploadDir + File.separator + "discussions" + File.separator + 
                                 courseId + File.separator + userDetails.getUserId();
            
            Path uploadPath = Paths.get(discussionDir);
            
            // Create directories if they don't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("✅ Created directory: " + uploadPath.toAbsolutePath());
            }

            // Generate unique filename to avoid conflicts
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = "discussion_" + UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);
            
            System.out.println("✅ File saved successfully to: " + filePath.toAbsolutePath());

            // Return file information
            String fileUrl = "/api/discussions/download/" + courseId + "/" + userDetails.getUserId() + "/" + uniqueFilename;
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "File upload thành công",
                "fileName", originalFilename,
                "savedFileName", uniqueFilename,
                "fileUrl", fileUrl,
                "fileSize", file.getSize()
            ));

        } catch (IOException e) {
            System.err.println("❌ Error uploading file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Lỗi khi upload file: " + e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Lỗi không xác định: " + e.getMessage()
            ));
        }
    }

    /**
     * Download/view uploaded discussion file
     */
    @GetMapping("/download/{courseId}/{userId}/{filename}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> downloadDiscussionFile(@PathVariable Integer courseId,
                                                   @PathVariable Integer userId,
                                                   @PathVariable String filename,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("=== Download Discussion File Request ===");
            System.out.println("Course ID: " + courseId);
            System.out.println("User ID: " + userId);
            System.out.println("Filename: " + filename);
            System.out.println("Requesting User ID: " + userDetails.getUserId());

            // Check course access permission
            if (userDetails.hasRole("instructor") && !courseService.isInstructorOfCourse(userDetails.getUserId(), courseId)) {
                return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền truy cập file này"
                ));
            }
            
            if (userDetails.hasRole("student") && !enrollmentsService.isStudentEnrolled(userDetails.getUserId(), courseId)) {
                return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền truy cập file này"
                ));
            }

            // Build file path
            String discussionDir = uploadDir + File.separator + "discussions" + File.separator + 
                                 courseId + File.separator + userId;
            Path filePath = Paths.get(discussionDir, filename);

            // Check if file exists
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // Return file as byte array
            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                    .body(fileContent);

        } catch (IOException e) {
            System.err.println("❌ Error downloading file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Lỗi khi tải file: " + e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Lỗi không xác định: " + e.getMessage()
            ));
        }
    }

    /**
     * Validate file type
     */
    private boolean isValidFileType(String contentType) {
        if (contentType == null) return false;
        
        return contentType.equals("application/pdf") ||
               contentType.equals("application/msword") ||
               contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
               contentType.equals("text/plain") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
