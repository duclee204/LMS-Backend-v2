package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.ModuleProgress;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ModuleProgressMapper {

    @Select("SELECT * FROM module_progress WHERE user_id = #{userId} AND module_id = #{moduleId}")
    @Results({
        @Result(property = "progressId", column = "progress_id"),
        @Result(property = "contentCompleted", column = "content_completed"),
        @Result(property = "videoCompleted", column = "video_completed"),
        @Result(property = "testCompleted", column = "test_completed"),
        @Result(property = "testUnlocked", column = "test_unlocked"),
        @Result(property = "moduleCompleted", column = "module_completed"),
        @Result(property = "completedAt", column = "completed_at"),
        @Result(property = "lastUpdated", column = "last_updated"),
        @Result(property = "user", column = "user_id", 
                one = @One(select = "org.example.lmsbackend.repository.UserMapper.findById")),
        @Result(property = "module", column = "module_id", 
                one = @One(select = "org.example.lmsbackend.repository.ModulesMapper.findById"))
    })
    Optional<ModuleProgress> findByUserAndModule(@Param("userId") Integer userId, @Param("moduleId") Integer moduleId);

    @Select("SELECT * FROM module_progress WHERE user_id = #{userId}")
    @Results({
        @Result(property = "progressId", column = "progress_id"),
        @Result(property = "contentCompleted", column = "content_completed"),
        @Result(property = "videoCompleted", column = "video_completed"),
        @Result(property = "testCompleted", column = "test_completed"),
        @Result(property = "testUnlocked", column = "test_unlocked"),
        @Result(property = "moduleCompleted", column = "module_completed"),
        @Result(property = "completedAt", column = "completed_at"),
        @Result(property = "lastUpdated", column = "last_updated"),
        @Result(property = "user", column = "user_id", 
                one = @One(select = "org.example.lmsbackend.repository.UserMapper.findById")),
        @Result(property = "module", column = "module_id", 
                one = @One(select = "org.example.lmsbackend.repository.ModulesMapper.findById"))
    })
    List<ModuleProgress> findByUserId(@Param("userId") Integer userId);

    @Insert("""
        INSERT INTO module_progress (user_id, module_id, content_completed, video_completed, 
                                   test_completed, test_unlocked, module_completed, last_updated)
        VALUES (#{user.userId}, #{module.id}, #{contentCompleted}, #{videoCompleted}, 
                #{testCompleted}, #{testUnlocked}, #{moduleCompleted}, NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "progressId")
    int insert(ModuleProgress moduleProgress);

    @Update("""
        UPDATE module_progress SET 
            content_completed = #{contentCompleted},
            video_completed = #{videoCompleted},
            test_completed = #{testCompleted},
            test_unlocked = #{testUnlocked},
            module_completed = #{moduleCompleted},
            completed_at = #{completedAt},
            last_updated = NOW()
        WHERE progress_id = #{progressId}
    """)
    int update(ModuleProgress moduleProgress);

    @Select("""
        SELECT COUNT(*) FROM module_progress mp
        JOIN modules m ON mp.module_id = m.module_id
        WHERE mp.user_id = #{userId} AND m.course_id = #{courseId} AND mp.module_completed = true
    """)
    int countCompletedModulesByCourse(@Param("userId") Integer userId, @Param("courseId") Integer courseId);

    @Select("""
        SELECT mp.* FROM module_progress mp
        JOIN modules m ON mp.module_id = m.module_id
        WHERE mp.user_id = #{userId} AND m.course_id = #{courseId}
        ORDER BY m.order_number
    """)
    @Results({
        @Result(property = "progressId", column = "progress_id"),
        @Result(property = "contentCompleted", column = "content_completed"),
        @Result(property = "videoCompleted", column = "video_completed"),
        @Result(property = "testCompleted", column = "test_completed"),
        @Result(property = "testUnlocked", column = "test_unlocked"),
        @Result(property = "moduleCompleted", column = "module_completed"),
        @Result(property = "completedAt", column = "completed_at"),
        @Result(property = "lastUpdated", column = "last_updated")
    })
    List<ModuleProgress> findByCourseAndUser(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
}
