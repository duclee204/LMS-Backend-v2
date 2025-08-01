package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.ContentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentProgressRepository extends JpaRepository<ContentProgress, Integer> {
    
    @Query("SELECT cp FROM ContentProgress cp WHERE cp.user.userId = :userId AND cp.content.contentId = :contentId")
    Optional<ContentProgress> findByUserIdAndContentId(@Param("userId") Integer userId, @Param("contentId") Integer contentId);
    
    @Query("SELECT cp FROM ContentProgress cp WHERE cp.user.userId = :userId")
    java.util.List<ContentProgress> findByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT cp FROM ContentProgress cp WHERE cp.user.userId = :userId AND cp.content.module.id = :moduleId")
    java.util.List<ContentProgress> findByUserIdAndModuleId(@Param("userId") Integer userId, @Param("moduleId") Integer moduleId);
}
