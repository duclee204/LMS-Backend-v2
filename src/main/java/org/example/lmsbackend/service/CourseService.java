package org.example.lmsbackend.service;

import org.example.lmsbackend.model.Course;
import org.example.lmsbackend.dto.CourseDTO;
import org.example.lmsbackend.repository.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private QuizzesService quizzesService;
    @Autowired
    private CourseMapper courseMapper;
    public boolean createCourse(CourseDTO dto, MultipartFile imageFile) {
        try {
            Course course = new Course();
            course.setTitle(dto.getTitle());
            course.setDescription(dto.getDescription());
            course.setCategoryId(dto.getCategoryId());
            course.setInstructorId(dto.getInstructorId());
            course.setStatus(Course.Status.valueOf(dto.getStatus()));
            course.setPrice(dto.getPrice());

            // ✅ Xử lý ảnh
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = saveImage(imageFile); // tên file hoặc URL
                course.setThumbnailUrl(fileName);
            }

            int result = courseMapper.insertCourse(course);
            System.out.println("🔧 Insert result: " + result);
            return result > 0;
        } catch (Exception e) {
            System.err.println("❌ Exception khi tạo khóa học:");
            e.printStackTrace();
            return false;
        }
    }

    public List<Course> getCourses(Integer categoryId, Integer instructorId, String status) {
        System.out.println("📦 getCourses with: categoryId=" + categoryId + ", instructorId=" + instructorId + ", status=" + status);
        return courseMapper.findCourses(categoryId, instructorId, status);
    }
    public boolean isInstructorOfCourse(int instructorId, int courseId) {
        return courseMapper.countByInstructorAndCourse(instructorId, courseId) > 0;
    }
    public boolean updateCourse(Course course, MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // 👉 Lưu ảnh vào thư mục uploads/imagescourse/
                String originalFilename = imageFile.getOriginalFilename();
                String filename = UUID.randomUUID() + "_" + originalFilename;
                Path filePath = Paths.get("uploads", "imagescourse", filename);
                Files.createDirectories(filePath.getParent());
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // 👉 Gán tên ảnh vào khóa học (chỉ lưu tên file, không lưu đường dẫn đầy đủ)
                course.setThumbnailUrl(filename);
            }

            return courseMapper.updateCourse(course) > 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteCourse(Integer courseId) {
        try {
            return courseMapper.deleteCourse(courseId) > 0;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 🛑 Constraint violation - không log error, chỉ return false
            System.out.println("ℹ️ Course " + courseId + " has related data, cannot delete - this is normal behavior");
            return false;
        } catch (Exception e) {
            // 🛑 Các lỗi khác - vẫn log để debug
            System.err.println("❌ Unexpected error deleting course " + courseId + ": " + e.getMessage());
            return false;
        }
    }
    /*
    public boolean isInstructorOwnerOfQuiz(Integer instructorId, Integer quizId) {
        Integer courseId = quizzesService.getCourseIdByQuizId(quizId);
        if (courseId == null) return false;

        return isInstructorOfCourse(instructorId, courseId);
    }
    */
    private String saveImage(MultipartFile file) {
        try {
            String uploadDir = "uploads/imagescourse";
            Path uploadPath = Paths.get(uploadDir);

            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Đặt tên file duy nhất (loại bỏ khoảng trắng)
            String originalFilename = file.getOriginalFilename();
            String cleanedFilename = originalFilename != null ? originalFilename.replaceAll("\\s+", "_") : "image.png";
            String filename = UUID.randomUUID() + "_" + cleanedFilename;

            Path filePath = uploadPath.resolve(filename);

            // Ghi file
            Files.copy(file.getInputStream(), filePath);

            return filename; // ✅ Trả về chỉ tên file để frontend dùng đúng URL
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file ảnh", e);
        }
    }

    // Get course by ID
    public Optional<Course> getCourseById(Integer courseId) {
        return courseMapper.findById(courseId);
    }
}
