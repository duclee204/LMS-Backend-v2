package org.example.lmsbackend.service;

import org.example.lmsbackend.model.Categories;
import org.example.lmsbackend.repository.CategoriesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {

    @Autowired
    private CategoriesMapper categoriesMapper;

    public void createCategory(Categories category) {
        categoriesMapper.insertCategory(category);
    }

    public List<Categories> searchCategories(String name, String description) {
        return categoriesMapper.searchCategories(name, description);
    }
    public void updateCategory(Categories category) {
        categoriesMapper.updateCategory(category);
    }
    public void deleteCategory(Integer id) {
        categoriesMapper.deleteCategory(id);
    }

}
