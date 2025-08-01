package org.example.lmsbackend.controller;

import org.example.lmsbackend.dto.VideoDTO;
import org.example.lmsbackend.service.VideoService;
import org.example.lmsbackend.service.CourseService;
import org.example.lmsbackend.service.EnrollmentsService;
import org.example.lmsbackend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private EnrollmentsService enrollmentsService;

    // Lấy danh sách video theo khóa học - có phân quyền
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<List<VideoDTO>> getVideosByCourse(@PathVariable Integer courseId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Kiểm tra quyền truy cập khóa học
        if (userDetails.hasRole("instructor")) {
            if (!courseService.isInstructorOfCourse(userDetails.getUserId(), courseId)) {
                return ResponseEntity.status(403).build();
            }
        }
        if (userDetails.hasRole("student")) {
            if (!enrollmentsService.isStudentEnrolled(userDetails.getUserId(), courseId)) {
                return ResponseEntity.status(403).build();
            }
        }
        
        List<VideoDTO> videos = videoService.getVideosByCourse(courseId);
        return ResponseEntity.ok(videos);
    }

    // Upload video - chỉ instructor của khóa học
    @PostMapping("/upload")
    @PreAuthorize("hasRole('instructor')")
    public ResponseEntity<VideoDTO> uploadVideo(@RequestParam("file") MultipartFile file,
                                              @RequestParam("title") String title,
                                              @RequestParam("description") String description,
                                              @RequestParam("courseId") Integer courseId,
                                              @RequestParam("moduleId") Integer moduleId, // Required now
                                              @RequestParam(value = "published", defaultValue = "false") Boolean published,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Kiểm tra giảng viên có dạy khóa này không
        if (!courseService.isInstructorOfCourse(userDetails.getUserId(), courseId)) {
            return ResponseEntity.status(403).build();
        }
        
        // Validate moduleId is provided
        if (moduleId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        VideoDTO videoDTO = videoService.uploadVideo(file, title, description, courseId, moduleId, published, userDetails.getUserId());
        if (videoDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(videoDTO);
    }

    // Stream video - có phân quyền xem
    @GetMapping("/stream/{videoId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long videoId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println("🎥 Stream request: videoId=" + videoId + ", userId=" + userDetails.getUserId() + ", role=" + userDetails.getAuthorities());
        
        // Admin có quyền xem tất cả video
        if (userDetails.hasRole("admin")) {
            System.out.println("✅ Admin access granted for video " + videoId);
        }
        // Kiểm tra quyền xem video cho instructor và student
        else if (userDetails.hasRole("instructor") && !videoService.isInstructorOfVideo(videoId, userDetails.getUserId())) {
            System.out.println("❌ Instructor access denied for video " + videoId);
            return ResponseEntity.status(403).build();
        }
        else if (userDetails.hasRole("student") && !videoService.canStudentAccessVideo(videoId, userDetails.getUserId())) {
            System.out.println("❌ Student access denied for video " + videoId);
            return ResponseEntity.status(403).build();
        }
        
        Resource videoResource = videoService.getVideoResource(videoId);
        if (videoResource == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(videoResource);
    }

    // Xem chi tiết video - có phân quyền
    @GetMapping("/{videoId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable Long videoId, 
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        VideoDTO video = videoService.getVideoById(videoId);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Admin có quyền xem tất cả video
        if (userDetails.hasRole("admin")) {
            System.out.println("✅ Admin access granted for video details " + videoId);
        }
        // Kiểm tra quyền xem video cho instructor và student
        else if (userDetails.hasRole("instructor") && !videoService.isInstructorOfVideo(videoId, userDetails.getUserId())) {
            return ResponseEntity.status(403).build();
        }
        else if (userDetails.hasRole("student") && !videoService.canStudentAccessVideo(videoId, userDetails.getUserId())) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(video);
    }

    // Xóa video - chỉ instructor của video
    @DeleteMapping("/{videoId}")
    @PreAuthorize("hasRole('instructor')")
    public ResponseEntity<String> deleteVideo(@PathVariable Long videoId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Chỉ giảng viên của video mới được xóa
        if (!videoService.isInstructorOfVideo(videoId, userDetails.getUserId())) {
            return ResponseEntity.status(403).body("Bạn không có quyền xóa video này");
        }
        
        int deleted = videoService.deleteVideo(videoId);
        return deleted > 0 ? ResponseEntity.ok("Video deleted successfully") : ResponseEntity.notFound().build();
    }

    // API tương thích với code cũ (không có phân quyền - chỉ dùng để test)
    @GetMapping
    public ResponseEntity<List<VideoDTO>> getAllVideos(@RequestParam(required = false) String title) {
        return ResponseEntity.ok(videoService.getAllVideos(title));
    }

    @GetMapping("/module/{moduleId}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<List<VideoDTO>> getVideosByModule(@PathVariable Integer moduleId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            System.out.println("🎥 Loading videos for moduleId: " + moduleId + " by user: " + userDetails.getUsername());
            
            // TODO: Add proper authorization check for module access
            // For now, allow all authenticated users to view videos in any module
            List<VideoDTO> videos = videoService.getVideosByModule(moduleId);
            System.out.println("✅ Found " + videos.size() + " videos for module " + moduleId);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            System.err.println("❌ Error in getVideosByModule: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // Video Management Endpoints
    @PutMapping("/{videoId}/status")
    @PreAuthorize("hasRole('instructor')")
    public ResponseEntity<Map<String, Object>> updateVideoStatus(@PathVariable Long videoId,
                                                   @RequestBody StatusUpdateRequest request,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Check if instructor owns this video
        if (!videoService.isInstructorOfVideo(videoId, userDetails.getUserId())) {
            return ResponseEntity.status(403).body(Map.of(
                "success", false,
                "message", "Bạn không có quyền chỉnh sửa video này"
            ));
        }
        
        boolean updated = videoService.updateVideoStatus(videoId, request.getPublished());
        if (updated) {
            String status = request.getPublished() ? "xuất bản" : "hủy xuất bản";
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Video đã được " + status + " thành công",
                "published", request.getPublished()
            ));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{videoId}")
    @PreAuthorize("hasRole('instructor')")
    public ResponseEntity<VideoDTO> updateVideo(@PathVariable Long videoId,
                                              @RequestBody VideoDTO videoDTO,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Check if instructor owns this video
        if (!videoService.isInstructorOfVideo(videoId, userDetails.getUserId())) {
            return ResponseEntity.status(403).build();
        }
        
        VideoDTO updated = videoService.updateVideo(videoId, videoDTO);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // Status update request class
    public static class StatusUpdateRequest {
        private Boolean published;
        
        public Boolean getPublished() {
            return published;
        }
        
        public void setPublished(Boolean published) {
            this.published = published;
        }
    }
}