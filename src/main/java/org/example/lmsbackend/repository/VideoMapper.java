package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.Video;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VideoMapper {

    @Insert("""
        INSERT INTO videos (title, description, file_url, duration, file_size, mime_type, course_id, instructor_id, uploaded_at)
        VALUES (#{title}, #{description}, #{fileUrl}, #{duration}, #{fileSize}, #{mimeType}, #{course.courseId}, #{instructor.userId}, NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "videoId")
    int insertVideo(Video video);

    @Select("""
    SELECT 
        video_id, title, description, file_url, duration, file_size, mime_type, uploaded_at
    FROM videos
    WHERE (#{title} IS NULL OR title LIKE CONCAT('%', #{title}, '%'))
""")
    @Results({
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "fileUrl", column = "file_url"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "fileSize", column = "file_size"),
            @Result(property = "mimeType", column = "mime_type"),
            @Result(property = "uploadedAt", column = "uploaded_at"),
    })
    List<Video> findVideos(@Param("title") String title);


    @Select("SELECT * FROM videos WHERE video_id = #{videoId}")
    @Results({
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "fileUrl", column = "file_url"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "fileSize", column = "file_size"),
            @Result(property = "mimeType", column = "mime_type"),
            @Result(property = "uploadedAt", column = "uploaded_at"),
            @Result(property = "course", column = "course_id", 
                    one = @One(select = "org.example.lmsbackend.repository.CourseMapper.findById")),
            @Result(property = "instructor", column = "instructor_id", 
                    one = @One(select = "org.example.lmsbackend.repository.UserMapper.findById"))
    })
    Video findById(@Param("videoId") Long videoId);

    @Update("""
        UPDATE videos
        SET 
            title = #{title},
            description = #{description},
            file_url = #{fileUrl},
            duration = #{duration},
            file_size = #{fileSize},
            mime_type = #{mimeType},
            uploaded_at = #{uploadedAt}
        WHERE video_id = #{videoId}
    """)
    int updateVideo(Video video);

    @Delete("DELETE FROM videos WHERE video_id = #{videoId}")
    int deleteVideo(@Param("videoId") Long videoId);

    @Select("SELECT COUNT(*) FROM videos v JOIN courses c ON v.course_id = c.course_id WHERE v.video_id = #{videoId} AND c.instructor_id = #{instructorId}")
    boolean isInstructorOfVideo(@Param("videoId") Long videoId, @Param("instructorId") Integer instructorId);

    @Select("SELECT COUNT(*) FROM videos v JOIN enrollments e ON v.course_id = e.course_id WHERE v.video_id = #{videoId} AND e.user_id = #{userId}")
    boolean canStudentAccessVideo(@Param("videoId") Long videoId, @Param("userId") Integer userId);

    @Select("""
        SELECT v.*, c.title as course_name, u.full_name as instructor_name
        FROM videos v 
        JOIN courses c ON v.course_id = c.course_id
        JOIN users u ON v.instructor_id = u.user_id
        WHERE v.course_id = #{courseId}
        """)
    @Results({
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "fileUrl", column = "file_url"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "fileSize", column = "file_size"),
            @Result(property = "mimeType", column = "mime_type"),
            @Result(property = "uploadedAt", column = "uploaded_at"),
            @Result(property = "course", column = "course_id", 
                    one = @One(select = "org.example.lmsbackend.repository.CourseMapper.findById")),
            @Result(property = "instructor", column = "instructor_id", 
                    one = @One(select = "org.example.lmsbackend.repository.UserMapper.findById"))
    })
    List<Video> findVideosByCourseId(@Param("courseId") Integer courseId);

}