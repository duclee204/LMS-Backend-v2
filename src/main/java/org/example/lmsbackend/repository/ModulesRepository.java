package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.Modules;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModulesRepository extends JpaRepository<Modules, Integer> {
    // ✅ Sắp xếp modules theo order_number
    List<Modules> findByCourse_CourseIdOrderByOrderNumber(Integer courseId);
    
    // ✅ STUDENT API: Lấy modules đã xuất bản theo courseId và sắp xếp theo order_number
    List<Modules> findByCourse_CourseIdAndPublishedTrueOrderByOrderNumber(Integer courseId);
}
