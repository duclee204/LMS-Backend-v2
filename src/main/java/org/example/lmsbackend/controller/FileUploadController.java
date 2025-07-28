package org.example.lmsbackend.controller;

import org.example.lmsbackend.security.CustomUserDetails;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Upload essay file for student submission
     */
    @PostMapping("/essay")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> uploadEssayFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam("courseId") Integer courseId,
                                           @RequestParam("quizId") Integer quizId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("=== Essay File Upload Request ===");
            System.out.println("Course ID: " + courseId);
            System.out.println("Quiz ID: " + quizId);
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

            // Create directory structure: uploads/test/{courseId}/{userId}
            String testDir = uploadDir + File.separator + "test" + File.separator + 
                           courseId + File.separator + userDetails.getUserId();
            
            Path uploadPath = Paths.get(testDir);
            
            // Create directories if they don't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("✅ Created directory: " + uploadPath.toAbsolutePath());
            }

            // Generate unique filename to avoid conflicts
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = "quiz_" + quizId + "_" + UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);
            
            System.out.println("✅ File saved successfully to: " + filePath.toAbsolutePath());

            // Return file information
            String fileUrl = "/api/upload/test/" + courseId + "/" + userDetails.getUserId() + "/" + uniqueFilename;
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "File upload thành công",
                "fileName", originalFilename,
                "savedFileName", uniqueFilename,
                "filePath", fileUrl,
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
     * Download/view uploaded essay file
     */
    @GetMapping("/test/{courseId}/{userId}/{filename}")
    @PreAuthorize("hasAnyRole('student', 'instructor', 'admin')")
    public ResponseEntity<?> downloadEssayFile(@PathVariable Integer courseId,
                                             @PathVariable Integer userId,
                                             @PathVariable String filename,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("=== Download Essay File Request ===");
            System.out.println("Course ID: " + courseId);
            System.out.println("User ID: " + userId);
            System.out.println("Filename: " + filename);
            System.out.println("Requesting User ID: " + userDetails.getUserId());

            // Security check: only allow users to download their own files, or instructors/admins
            if (!userDetails.getUserId().equals(userId) && 
                !userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_instructor") || 
                                    auth.getAuthority().equals("ROLE_admin"))) {
                return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền truy cập file này"
                ));
            }

            // Build file path
            String testDir = uploadDir + File.separator + "test" + File.separator + 
                           courseId + File.separator + userId;
            Path filePath = Paths.get(testDir, filename);

            // Check if file exists
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // Read file content
            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            System.out.println("✅ File found and ready for download: " + filePath.toAbsolutePath());

            return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
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
               contentType.equals("image/png");
    }

    /**
     * Serve question files for download
     */
    @GetMapping("/questions/{filename}")
    public ResponseEntity<?> downloadQuestionFile(@PathVariable String filename) {
        try {
            System.out.println("=== Download Question File Request ===");
            System.out.println("Filename: " + filename);

            // Build file path
            String questionDir = uploadDir + File.separator + "questions";
            Path filePath = Paths.get(questionDir, filename);

            // Check if file exists
            if (!Files.exists(filePath)) {
                System.err.println("❌ File not found: " + filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            // Read file content
            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            System.out.println("✅ Question file found and ready for download: " + filePath.toAbsolutePath());

            return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(fileContent);

        } catch (IOException e) {
            System.err.println("❌ Error downloading question file: " + e.getMessage());
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
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
