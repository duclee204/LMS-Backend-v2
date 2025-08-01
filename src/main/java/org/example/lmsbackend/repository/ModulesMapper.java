package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.Modules;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ModulesMapper {

    @Select("SELECT * FROM modules WHERE module_id = #{id}")
    @Results({
            @Result(property = "id", column = "module_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "orderNumber", column = "order_number"),
            @Result(property = "published", column = "published"),
            @Result(property = "courseId", column = "course_id")
    })
    Modules findById(@Param("id") Integer id);
}
