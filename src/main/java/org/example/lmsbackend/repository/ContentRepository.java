package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Integer> {
    // ✅ Sắp xếp content theo order_number
    List<Content> findByModuleIdOrderByOrderNumber(Integer moduleId);
    
    // ✅ Sắp xếp content theo module order_number, rồi content order_number
    List<Content> findAllByModule_Course_CourseIdOrderByModule_OrderNumberAscOrderNumberAsc(Integer courseId);
    
    // ✅ STUDENT API: Lấy content đã xuất bản theo moduleId và sắp xếp theo order_number
    List<Content> findByModuleIdAndPublishedTrueOrderByOrderNumber(Integer moduleId);
}
