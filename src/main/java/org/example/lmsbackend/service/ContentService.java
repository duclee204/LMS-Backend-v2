package org.example.lmsbackend.service;

import org.example.lmsbackend.model.Content;
import org.example.lmsbackend.model.Modules;
import org.example.lmsbackend.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ModulesService modulesService;

    // ✅ Tạo content mới
    public Content createContent(int moduleId, String title, String contentType, String description, 
                                String contentUrl, int orderNumber, boolean isPublished, MultipartFile file) throws IOException {
        Modules module = modulesService.getModuleById(moduleId);
        
        // Kiểm tra trùng orderNumber khi tạo mới (không loại trừ ai vì đây là tạo mới)
        if (isOrderNumberExistsForNewContent(moduleId, orderNumber)) {
            throw new RuntimeException("Số thứ tự " + orderNumber + " đã tồn tại trong module này. Vui lòng chọn số khác!");
        }
        
        Content content = new Content();
        content.setTitle(title);
        content.setType(contentType);
        content.setModule(module);
        content.setOrderNumber(orderNumber);
        content.setPublished(isPublished);
        
        // Set description if provided
        if (description != null && !description.trim().isEmpty()) {
            // Assuming Content model has description field
            // content.setDescription(description);
        }
        
        // Handle contentUrl for link type content
        if (contentUrl != null && !contentUrl.trim().isEmpty()) {
            content.setContentUrl(contentUrl);
        }
        
        // Handle file upload if provided
        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads/modules/" + moduleId;
            Files.createDirectories(Paths.get(uploadDir));
            
            String fileName = file.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            file.transferTo(path);
            
            content.setFileName(fileName);
            // Only set contentUrl from file if no URL was provided
            if (contentUrl == null || contentUrl.trim().isEmpty()) {
                content.setContentUrl("/" + path.toString().replace("\\", "/"));
            }
        }
        
        return contentRepository.save(content);
    }

    // ✅ Lấy content theo moduleId
    public List<Content> getContentsByModuleId(int moduleId) {
        return contentRepository.findByModuleIdOrderByOrderNumber(moduleId);
    }

    // ✅ STUDENT API: Lấy content đã xuất bản theo moduleId
    public List<Content> getPublishedContentsByModuleId(int moduleId) {
        return contentRepository.findByModuleIdAndPublishedTrueOrderByOrderNumber(moduleId);
    }

    // ✅ Lấy content theo courseId
    public List<Content> getContentsByCourseId(int courseId) {
        return contentRepository.findAllByModule_Course_CourseIdOrderByModule_OrderNumberAscOrderNumberAsc(courseId);
    }

    // ✅ Upload tài liệu
    public void uploadDocument(String username, int moduleId, MultipartFile file) throws IOException {
        Modules module = modulesService.getModuleById(moduleId);

        if (!module.getCourse().getInstructor().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to upload to this module");
        }

        // Tạo thư mục lưu file
        String uploadDir = "uploads/modules/" + moduleId;
        Files.createDirectories(Paths.get(uploadDir));

        // Lưu file
        Path path = Paths.get(uploadDir, file.getOriginalFilename());
        file.transferTo(path);

        // Tạo Content
        Content content = new Content();
        content.setTitle(file.getOriginalFilename());
        content.setType("document");
        content.setFileName(file.getOriginalFilename());
        content.setContentUrl("/" + path.toString().replace("\\", "/")); // chuẩn hóa URL
        content.setModule(module);
        content.setOrderNumber(getNextOrderNumber(moduleId));
        content.setDuration(null);
        content.setPublished(module.isPublished()); // nội dung thừa hưởng trạng thái của module

        contentRepository.save(content);
    }

    // ✅ Cập nhật trạng thái của content
    public Content updateContentStatus(int contentId, boolean published, String username) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        // Kiểm tra quyền instructor
        if (!content.getModule().getCourse().getInstructor().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to update this content");
        }

        content.setPublished(published);
        return contentRepository.save(content);
    }

    // ✅ Hàm hỗ trợ: lấy orderNumber tiếp theo cho content trong module
    private int getNextOrderNumber(int moduleId) {
        List<Content> contents = contentRepository.findByModuleIdOrderByOrderNumber(moduleId);
        return contents.isEmpty() ? 1 :
                contents.stream().mapToInt(Content::getOrderNumber).max().orElse(0) + 1;
    }

    // ✅ Kiểm tra trùng orderNumber trong module (trừ content hiện tại)
    private boolean isOrderNumberExists(int moduleId, int orderNumber, int excludeContentId) {
        List<Content> contents = contentRepository.findByModuleIdOrderByOrderNumber(moduleId);
        return contents.stream()
                .filter(c -> c.getId() != excludeContentId) // Loại trừ content hiện tại
                .anyMatch(c -> c.getOrderNumber() == orderNumber);
    }

    // ✅ Kiểm tra trùng orderNumber khi tạo content mới
    private boolean isOrderNumberExistsForNewContent(int moduleId, int orderNumber) {
        List<Content> contents = contentRepository.findByModuleIdOrderByOrderNumber(moduleId);
        return contents.stream()
                .anyMatch(c -> c.getOrderNumber() == orderNumber);
    }

    // ✅ Lấy content theo ID
    public Content getContentById(int contentId) {
        return contentRepository.findById(contentId)
                .orElse(null);
    }

    // ✅ Cập nhật content
    public Content updateContent(int contentId, String title, String contentType, String description, 
                               int orderNumber, boolean isPublished) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        // Kiểm tra trùng orderNumber
        if (isOrderNumberExists(content.getModule().getId(), orderNumber, contentId)) {
            throw new RuntimeException("Số thứ tự " + orderNumber + " đã tồn tại trong module này. Vui lòng chọn số khác!");
        }

        content.setTitle(title);
        content.setType(contentType);
        content.setOrderNumber(orderNumber);
        content.setPublished(isPublished);
        
        // Update description if Content model supports it
        // if (description != null) {
        //     content.setDescription(description);
        // }

        return contentRepository.save(content);
    }

    // ✅ Cập nhật content chỉ thông tin cơ bản (không lưu file mới)
    public Content updateContentInfo(int contentId, String title, String contentType, String description, 
                                   int orderNumber, boolean isPublished) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        // Kiểm tra trùng orderNumber
        if (isOrderNumberExists(content.getModule().getId(), orderNumber, contentId)) {
            throw new RuntimeException("Số thứ tự " + orderNumber + " đã tồn tại trong module này. Vui lòng chọn số khác!");
        }

        // Chỉ cập nhật thông tin cơ bản, không thay đổi file hoặc URL
        content.setTitle(title);
        content.setType(contentType);
        content.setOrderNumber(orderNumber);
        content.setPublished(isPublished);
        
        // Update description if Content model supports it
        // if (description != null) {
        //     content.setDescription(description);
        // }

        // Không thay đổi contentUrl, fileName - giữ nguyên file cũ
        return contentRepository.save(content);
    }

    // ✅ Cập nhật content với contentUrl
    public Content updateContent(int contentId, String title, String contentType, String description, 
                               int orderNumber, boolean isPublished, String contentUrl) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        // Kiểm tra trùng orderNumber
        if (isOrderNumberExists(content.getModule().getId(), orderNumber, contentId)) {
            throw new RuntimeException("Số thứ tự " + orderNumber + " đã tồn tại trong module này. Vui lòng chọn số khác!");
        }

        content.setTitle(title);
        content.setType(contentType);
        content.setOrderNumber(orderNumber);
        content.setPublished(isPublished);
        
        // Cập nhật contentUrl: chỉ cập nhật khi có URL mới được truyền vào
        if (contentUrl != null && !contentUrl.trim().isEmpty()) {
            content.setContentUrl(contentUrl);
        }
        // Nếu contentUrl là null hoặc empty, giữ nguyên URL cũ (không thay đổi)
        
        // Update description if Content model supports it
        // if (description != null) {
        //     content.setDescription(description);
        // }

        return contentRepository.save(content);
    }

    // ✅ Cập nhật content với file mới
    public Content updateContentWithFile(int contentId, String title, String contentType, String description, 
                                        int orderNumber, boolean isPublished, MultipartFile file) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        content.setTitle(title);
        content.setType(contentType);
        content.setOrderNumber(orderNumber);
        content.setPublished(isPublished);
        
        // Handle file upload
        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = "uploads/modules/" + content.getModule().getId();
                Files.createDirectories(Paths.get(uploadDir));
                
                String fileName = file.getOriginalFilename();
                Path path = Paths.get(uploadDir, fileName);
                file.transferTo(path);
                
                // Update content URL và file name
                content.setContentUrl("/" + uploadDir + "/" + fileName);
                content.setFileName(fileName);
                
                System.out.println("✅ File uploaded successfully: " + content.getContentUrl());
            } catch (IOException e) {
                throw new RuntimeException("Could not upload file: " + e.getMessage());
            }
        }
        
        return contentRepository.save(content);
    }

    // ✅ Xóa content
    public void deleteContent(int contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        
        // Delete physical file if exists
        if (content.getContentUrl() != null && !content.getContentUrl().isEmpty()) {
            try {
                Path filePath = Paths.get(content.getContentUrl().substring(1)); // Remove leading /
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but don't fail the deletion
                System.err.println("Could not delete file: " + e.getMessage());
            }
        }
        
        contentRepository.delete(content);
    }

    // ✅ Đồng bộ trạng thái của tất cả content trong module (dùng khi cập nhật module)
    public void syncContentStatusByModule(Modules module) {
        List<Content> contents = contentRepository.findByModuleIdOrderByOrderNumber(module.getId());
        boolean isPublished = module.isPublished();

        for (Content content : contents) {
            content.setPublished(isPublished);
        }
        contentRepository.saveAll(contents);
    }
}
