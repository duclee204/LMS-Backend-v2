package org.example.lmsbackend.controller;

import org.example.lmsbackend.dto.DiscussionReplyDTO;
import org.example.lmsbackend.service.DiscussionReplyService;
import org.example.lmsbackend.service.EnrollmentsService;
import org.example.lmsbackend.service.DiscussionService;
import org.example.lmsbackend.service.CourseService;
import org.example.lmsbackend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/discussion-replies")
public class DiscussionReplyController {

    @Autowired
    private DiscussionReplyService discussionReplyService;

    @Autowired
    private EnrollmentsService enrollmentsService;

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private CourseService courseService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Tạo reply mới
    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('instructor') or hasRole('student')")
    public ResponseEntity<?> createReply(@RequestBody DiscussionReplyDTO dto, Authentication auth) {
        try {
            System.out.println("? Creating reply - User: " + dto.getUserId() + ", DiscussionId: " + dto.getDiscussionId());
            System.out.println("? Parent Reply ID: " + dto.getParentReplyId() + " (null = root reply, not null = nested reply)");
            
            // Kiểm tra quyền: user phải được enroll vào course chứa discussion
            var discussionDTO = discussionService.getDiscussionById(dto.getDiscussionId());
            if (discussionDTO == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Discussion not found"));
            }

            Integer courseId = discussionDTO.getCourseId();
            
            // Kiểm tra enrollment cho student, instructor có thể reply vào course của mình
            String role = auth.getAuthorities().iterator().next().getAuthority();
            if ("ROLE_student".equals(role)) {
                boolean isEnrolled = enrollmentsService.isStudentEnrolled(dto.getUserId(), courseId);
                if (!isEnrolled) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("message", "You are not enrolled in this course"));
                }
            }

            DiscussionReplyDTO created = discussionReplyService.createReply(dto);
            if (created != null) {
                return ResponseEntity.ok(created);
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Failed to create reply"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating reply: " + e.getMessage()));
        }
    }

    // Lấy danh sách replies cho một discussion
    @GetMapping("/discussion/{discussionId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<List<DiscussionReplyDTO>> getRepliesByDiscussion(@PathVariable Integer discussionId) {
        try {
            List<DiscussionReplyDTO> replies = discussionReplyService.getRepliesByDiscussionId(discussionId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Đếm số lượng replies
    @GetMapping("/count/{discussionId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<Map<String, Long>> getReplyCount(@PathVariable Integer discussionId) {
        try {
            Long count = discussionReplyService.getReplyCount(discussionId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Xóa reply (chỉ admin hoặc tác giả)
    @DeleteMapping("/{replyId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<?> deleteReply(@PathVariable Integer replyId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boolean deleted = discussionReplyService.deleteReply(replyId, userDetails.getUserId(), userDetails.hasRole("admin"));
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Reply deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Bạn không có quyền xóa trả lời này"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting reply: " + e.getMessage()));
        }
    }

    /**
     * Upload file attachment for discussion reply
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('instructor', 'student')")
    public ResponseEntity<?> uploadReplyFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam("discussionId") Integer discussionId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("=== Reply File Upload Request ===");
            System.out.println("Discussion ID: " + discussionId);
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

            // Check user permission for the discussion
            var discussionDTO = discussionService.getDiscussionById(discussionId);
            if (discussionDTO == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Discussion not found"
                ));
            }

            Integer courseId = discussionDTO.getCourseId();

            // Check enrollment/teaching permission
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

            // Create directory structure: uploads/discussions/{courseId}/{discussionId}/replies/
            String dirPath = uploadDir + File.separator + "discussions" + File.separator + 
                           courseId + File.separator + discussionId + File.separator + "replies";
            
            Path uploadPath = Paths.get(dirPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = "reply_" + UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);

            // Create file URL
            String fileUrl = "/api/discussion-replies/download/" + courseId + "/" + discussionId + "/" + uniqueFilename;

            System.out.println("File saved to: " + filePath.toString());
            System.out.println("File URL: " + fileUrl);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "File uploaded successfully",
                "fileName", originalFilename,
                "fileUrl", fileUrl
            ));

        } catch (IOException e) {
            System.err.println("File upload error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Lỗi khi lưu file: " + e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Unexpected error during file upload: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Lỗi không mong muốn: " + e.getMessage()
            ));
        }
    }

    /**
     * Download reply file attachment
     */
    @GetMapping("/download/{courseId}/{discussionId}/{filename}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<?> downloadReplyFile(@PathVariable Integer courseId,
                                             @PathVariable Integer discussionId,
                                             @PathVariable String filename,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // Check user permission for the course
            if (userDetails.hasRole("instructor") && !courseService.isInstructorOfCourse(userDetails.getUserId(), courseId)) {
                return ResponseEntity.status(403).body("Bạn không có quyền truy cập file này");
            }

            if (userDetails.hasRole("student") && !enrollmentsService.isStudentEnrolled(userDetails.getUserId(), courseId)) {
                return ResponseEntity.status(403).body("Bạn không có quyền truy cập file này");
            }

            // Build file path
            String filePath = uploadDir + File.separator + "discussions" + File.separator + 
                            courseId + File.separator + discussionId + File.separator + "replies" + File.separator + filename;
            
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            // Get file content and type
            byte[] content = Files.readAllBytes(path);
            String contentType = Files.probeContentType(path);
            
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(content);

        } catch (IOException e) {
            System.err.println("File download error: " + e.getMessage());
            return ResponseEntity.status(500).body("Lỗi khi tải file");
        } catch (Exception e) {
            System.err.println("Unexpected error during file download: " + e.getMessage());
            return ResponseEntity.status(500).body("Lỗi không mong muốn");
        }
    }

    /**
     * Validate file type
     */
    private boolean isValidFileType(String contentType) {
        return contentType != null && (
               contentType.equals("application/pdf") ||
               contentType.equals("application/msword") ||
               contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
               contentType.equals("text/plain") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp"));
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
