package com.example.commerce.controllers;

import com.example.commerce.dtos.CategoryRequestDTO;
import com.example.commerce.dtos.CategoryResponseDTO;
import com.example.commerce.services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sort
    ) {
        logger.info("GET /categories - Fetching all categories");
        List<CategoryResponseDTO> categories = categoryService.getAllCategories(sort);
        logger.info("GET /categories - Returning {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Byte id) {
        logger.info("GET /categories/{} - Fetching category by id", id);
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        logger.info("GET /categories/{} - Category found", id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        logger.info("POST /categories - Creating new category with name: {}", categoryRequestDTO.getName());
        CategoryResponseDTO createdCategory = categoryService.createCategory(categoryRequestDTO);
        logger.info("POST /categories - Category created with id: {}", createdCategory.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Byte id, @RequestBody CategoryRequestDTO categoryRequestDTO) {
        logger.info("PUT /categories/{} - Updating category", id);
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, categoryRequestDTO);
        logger.info("PUT /categories/{} - Category updated successfully", id);
        return ResponseEntity.ok(updatedCategory);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> partialUpdateCategory(@PathVariable Byte id, @RequestBody CategoryRequestDTO categoryRequestDTO) {
        logger.info("PATCH /categories/{} - Partially updating category", id);
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, categoryRequestDTO);
        logger.info("PATCH /categories/{} - Category partially updated successfully", id);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Byte id) {
        logger.info("DELETE /categories/{} - Deleting category", id);
        categoryService.deleteCategory(id);
        logger.info("DELETE /categories/{} - Category deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
