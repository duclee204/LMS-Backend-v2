package org.example.lmsbackend.controller;

import org.example.lmsbackend.model.Course;
import org.example.lmsbackend.dto.CourseDTO;
import org.example.lmsbackend.service.CourseService;
import org.example.lmsbackend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseRestController {
    @Autowired
    private org.example.lmsbackend.service.EnrollmentsService enrollmentsService;

    // API mới: trả về tất cả khóa học kèm trạng thái đã đăng ký
    @GetMapping("/all-with-status")
    @PreAuthorize("hasRole('student') or hasRole('admin') or hasRole('instructor')")
    public ResponseEntity<List<Map<String, Object>>> getAllCoursesWithStatus(@RequestParam int userId) {
        List<Course> allCourses = courseService.getCourses(null, null, null);
        // Lấy danh sách ID khóa học đã đăng ký
        List<org.example.lmsbackend.dto.EnrollmentsDTO> enrolled = enrollmentsService.getEnrolledCourses(userId);
        List<Integer> enrolledCourseIds = new java.util.ArrayList<>();
        for (org.example.lmsbackend.dto.EnrollmentsDTO dto : enrolled) {
            enrolledCourseIds.add(dto.getCourseId());
        }

        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Course course : allCourses) {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("courseId", course.getCourseId());
            item.put("title", course.getTitle());
            item.put("description", course.getDescription());
            item.put("price", course.getPrice());
            item.put("thumbnailUrl", course.getThumbnailUrl());
            item.put("enrolled", enrolledCourseIds.contains(course.getCourseId()));
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    @Autowired
    private CourseService courseService;
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createCourse(
            @RequestPart("course") CourseDTO courseDTO,
            @RequestPart("image") MultipartFile imageFile) {

        System.out.println("📥 Received courseDTO: " + courseDTO);
        System.out.println("📷 Received file: " + imageFile.getOriginalFilename());

        try {
            boolean created = courseService.createCourse(courseDTO, imageFile);
            if (!created) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Tạo khóa học thất bại"));
            }
            return ResponseEntity.ok(Map.of("message", "Tạo khóa học thành công"));
        } catch (Exception e) {
            e.printStackTrace(); // xem lỗi cụ thể ở terminal
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('admin') or hasRole('instructor')")
    public ResponseEntity<List<Course>> listCourses(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String status
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer instructorId = null;

        if (principal instanceof CustomUserDetails customUser) {
            boolean isInstructor = customUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_instructor"));
            if (isInstructor) {
                instructorId = customUser.getId(); // ✅ lấy đúng userId
            }
            System.out.println("🔍 User ID: " + customUser.getId());
        }

        System.out.printf("getCourses with: categoryId=%s, instructorId=%s, status=%s%n",
                categoryId, instructorId, status);

        List<Course> courses = courseService.getCourses(categoryId, instructorId, status);
        return ResponseEntity.ok(courses);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateCourse(
            @PathVariable("id") Integer courseId,
            @RequestPart("course") Course course,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        course.setCourseId(courseId);

        boolean updated = courseService.updateCourse(course, imageFile);

        if (updated) {
            return ResponseEntity.ok("Course updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteCourse(@PathVariable("id") Integer courseId) {
        try {
            boolean deleted = courseService.deleteCourse(courseId);
            if (deleted) {
                return ResponseEntity.ok("Course deleted successfully");
            } else {
                // 🔸 Course tồn tại nhưng không thể xóa do có dữ liệu liên quan
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot delete course. Please remove all related videos and enrollments first.");
            }
        } catch (Exception e) {
            // 🔸 Lỗi không mong muốn khác
            System.err.println("❌ Unexpected error in deleteCourse controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred while deleting the course.");
        }
    }

    // Get course by ID - accessible to enrolled students, instructors, and admins
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('student') or hasRole('instructor') or hasRole('admin')")
    public ResponseEntity<?> getCourseById(@PathVariable("id") Integer courseId) {
        try {
            Optional<Course> courseOpt = courseService.getCourseById(courseId);
            
            if (courseOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Khóa học không tồn tại"));
            }
            
            Course course = courseOpt.get();
            
            // Check if user has access to this course
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails customUser) {
                boolean isAdmin = customUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
                boolean isInstructor = customUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_instructor"));
                boolean isStudent = customUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_student"));
                
                // Admin can access any course
                if (isAdmin) {
                    return ResponseEntity.ok(course);
                }
                
                // Instructor can access their own courses
                if (isInstructor && course.getInstructorId() != null && course.getInstructorId().equals(customUser.getId())) {
                    return ResponseEntity.ok(course);
                }
                
                // If instructor_id is null, allow instructor to access (temporary workaround)
                if (isInstructor && course.getInstructorId() == null) {
                    return ResponseEntity.ok(course);
                }
                
                // Student needs to be enrolled (we'll implement enrollment check later)
                // For now, allow students to access any course
                if (isStudent) {
                    // TODO: Check if student is enrolled in this course
                    return ResponseEntity.ok(course);
                }
            }
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Bạn không có quyền truy cập khóa học này"));
                
        } catch (Exception e) {
            System.err.println("❌ Error getting course: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Lỗi server khi lấy thông tin khóa học"));
        }
    }

}
