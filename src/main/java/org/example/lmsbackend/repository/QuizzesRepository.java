// QuizRepository.java
// ------------------------
package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizzesRepository extends JpaRepository<Quizzes, Integer> {
    List<Quizzes> findByCourseId(Integer courseId);
    List<Quizzes> findByCourseIdAndPublishTrue(Integer courseId);
    
    // Methods for quizzes without module (moduleId is null)
    List<Quizzes> findByCourseIdAndModuleIsNull(Integer courseId);
    List<Quizzes> findByCourseIdAndPublishTrueAndModuleIsNull(Integer courseId);
    
    // Thêm methods cho module
    List<Quizzes> findByModuleId(Integer moduleId);
    List<Quizzes> findByModuleIdAndPublishTrue(Integer moduleId);
    List<Quizzes> findByModuleIdOrderByOrderNumber(Integer moduleId);
}