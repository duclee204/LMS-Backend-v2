package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DiscussionRepository extends JpaRepository<Discussion, Integer> {
    
    // Get public discussions for a course
    @Query("SELECT d FROM Discussion d WHERE d.course.courseId = :courseId AND d.type = 'PUBLIC' ORDER BY d.createdAt DESC")
    List<Discussion> findPublicDiscussionsByCourse(@Param("courseId") Integer courseId);
    
    // Get private discussions where user is either author or target
    @Query("SELECT d FROM Discussion d WHERE d.course.courseId = :courseId AND d.type = 'PRIVATE' AND (d.user.userId = :userId OR d.targetUser.userId = :userId) ORDER BY d.createdAt DESC")
    List<Discussion> findPrivateDiscussionsForUser(@Param("courseId") Integer courseId, @Param("userId") Integer userId);
    
    // Get all discussions for a course that user can see
    @Query("SELECT d FROM Discussion d WHERE d.course.courseId = :courseId AND (d.type = 'PUBLIC' OR (d.type = 'PRIVATE' AND (d.user.userId = :userId OR d.targetUser.userId = :userId))) ORDER BY d.createdAt DESC")
    List<Discussion> findDiscussionsForUser(@Param("courseId") Integer courseId, @Param("userId") Integer userId);
    
    // Get discussions by user
    List<Discussion> findByUser_UserId(Integer userId);
}
