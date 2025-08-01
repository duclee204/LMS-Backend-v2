package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    @Query("SELECT v FROM Video v WHERE v.module.id = :moduleId")
    List<Video> findByModuleId(@Param("moduleId") Integer moduleId);
    
    @Query("SELECT v FROM Video v WHERE v.module.id = :moduleId AND v.published = true")
    List<Video> findByModuleIdAndPublished(@Param("moduleId") Integer moduleId);
}
