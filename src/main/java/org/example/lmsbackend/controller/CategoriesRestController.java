package org.example.lmsbackend.controller;

import org.example.lmsbackend.model.Categories;
import org.example.lmsbackend.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoriesRestController {

    @Autowired
    private CategoriesService categoriesService;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> createCategory(@RequestBody Categories category) {
        categoriesService.createCategory(category);
        return new ResponseEntity<>("Category created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<List<Categories>> getCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description) {
        
        List<Categories> list = categoriesService.searchCategories(name, description);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> updateCategory(@PathVariable("id") Integer id, @RequestBody Categories category) {
        category.setCategoryId(id);
        categoriesService.updateCategory(category);
        return new ResponseEntity<>("Category updated successfully", HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") Integer id) {
        categoriesService.deleteCategory(id);
        return new ResponseEntity<>("Category deleted successfully", HttpStatus.OK);
    }

}
