// QuizRepository.java
// ------------------------
package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizzesRepository extends JpaRepository<Quizzes, Integer> {
    List<Quizzes> findByCourseId(Integer courseId);
    List<Quizzes> findByCourseIdAndPublishTrue(Integer courseId);
}