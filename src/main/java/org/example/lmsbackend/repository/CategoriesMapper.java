package org.example.lmsbackend.repository;

import org.apache.ibatis.annotations.*;
import org.example.lmsbackend.model.Categories;

import java.util.List;

@Mapper
public interface CategoriesMapper {

    @Insert("INSERT INTO categories (name, description) VALUES (#{name}, #{description})")
    void insertCategory(Categories category);

    @Select({
            "<script>",
            "SELECT category_id AS categoryId, name, description FROM categories",
            "WHERE 1=1",
            "<if test='name != null'>AND name LIKE CONCAT('%', #{name}, '%')</if>",
            "<if test='description != null'>AND description LIKE CONCAT('%', #{description}, '%')</if>",
            "</script>"
    })
    List<Categories> searchCategories(@Param("name") String name, @Param("description") String description);
    @Update("UPDATE categories SET name = #{name}, description = #{description} WHERE category_id = #{categoryId}")
    void updateCategory(Categories category);
    @Delete("DELETE FROM categories WHERE category_id = #{id}")
    void deleteCategory(Integer id);

}

