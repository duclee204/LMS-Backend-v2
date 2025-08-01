package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {
    
    @Query("SELECT vp FROM VideoProgress vp WHERE vp.user.userId = :userId AND vp.video.videoId = :videoId")
    Optional<VideoProgress> findByUserIdAndVideoId(@Param("userId") Integer userId, @Param("videoId") Integer videoId);
    
    @Query("SELECT vp FROM VideoProgress vp WHERE vp.user.userId = :userId")
    java.util.List<VideoProgress> findByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT vp FROM VideoProgress vp WHERE vp.user.userId = :userId AND vp.video.module.id = :moduleId")
    java.util.List<VideoProgress> findByUserIdAndModuleId(@Param("userId") Integer userId, @Param("moduleId") Integer moduleId);
}
