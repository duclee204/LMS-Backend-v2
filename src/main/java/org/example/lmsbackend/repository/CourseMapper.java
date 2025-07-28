package org.example.lmsbackend.repository;

import org.apache.ibatis.annotations.*;
import org.example.lmsbackend.model.Course;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CourseMapper {
    @Insert("""
    INSERT INTO courses (title, description, category_id, instructor_id, status, price, thumbnail_url)
    VALUES (#{title}, #{description}, #{categoryId}, #{instructorId}, #{status}, #{price}, #{thumbnailUrl})
""")
    @Options(useGeneratedKeys = true, keyProperty = "courseId")
    int insertCourse(Course course);

    @Select("""
    <script>
    SELECT 
        c.course_id AS courseId,
        c.title,
        c.description,
        c.category_id AS categoryId,
        c.instructor_id AS instructorId,
        u.full_name AS instructorName,
        c.status,
        c.price,
        c.thumbnail_url AS thumbnailUrl  -- ✅ THÊM DÒNG NÀY
    FROM courses c
    LEFT JOIN users u ON c.instructor_id = u.user_id
    WHERE 1=1
        <if test="categoryId != null">AND c.category_id = #{categoryId}</if>
        <if test="instructorId != null">AND c.instructor_id = #{instructorId}</if>
        <if test="status != null">AND c.status = #{status}</if>
    </script>
""")
    List<Course> findCourses(
            @Param("categoryId") Integer categoryId,
            @Param("instructorId") Integer instructorId,
            @Param("status") String status
    );



    @Update("""
    UPDATE courses
    SET 
        title = #{title},
        description = #{description},
        category_id = #{categoryId},
        instructor_id = #{instructorId},
        status = #{status},
        price = #{price},
        thumbnail_url = #{thumbnailUrl},
        updated_at = CURRENT_TIMESTAMP
    WHERE course_id = #{courseId}
""")
    int updateCourse(Course course);

    @Delete("DELETE FROM courses WHERE course_id = #{courseId}")
    int deleteCourse(@Param("courseId") Integer courseId);
    @Select("SELECT COUNT(*) FROM courses WHERE course_id = #{courseId} AND instructor_id = #{instructorId}")
    int countByInstructorAndCourse(@Param("instructorId") int instructorId, @Param("courseId") int courseId);

    @Select("""
        SELECT 
            c.course_id AS courseId,
            c.title,
            c.description,
            c.category_id AS categoryId,
            c.instructor_id AS instructorId,
            u.full_name AS instructorName,
            c.status,
            c.price,
            c.thumbnail_url AS thumbnailUrl,
            c.created_at AS createdAt,
            c.updated_at AS updatedAt
        FROM courses c
        LEFT JOIN users u ON c.instructor_id = u.user_id
        WHERE c.course_id = #{courseId}
    """)
    Optional<Course> findById(@Param("courseId") Integer courseId);
}

