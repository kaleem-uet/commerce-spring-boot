package com.example.commerce.services;

import com.example.commerce.dtos.CategoryRequestDTO;
import com.example.commerce.dtos.CategoryResponseDTO;
import com.example.commerce.entities.Category;
import com.example.commerce.exceptions.ResourceNotFoundException;
import com.example.commerce.mappers.CategoryMapper;
import com.example.commerce.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryResponseDTO> getAllCategories(String sort) {
        if (!List.of("name", "id").contains(sort)) {
            sort = "name";
        }
        logger.debug("Fetching all categories from database");
        List<CategoryResponseDTO> categories = categoryRepository.findAll(Sort.by(sort))
                .stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.debug("Found {} categories", categories.size());
        return categories;
    }

    public CategoryResponseDTO getCategoryById(Byte id) {
        logger.debug("Fetching category with id: {}", id);
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        logger.debug("Creating new category with name: {}", categoryRequestDTO.getName());

        // Validation
        if (categoryRequestDTO.getName() == null || categoryRequestDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        // Map DTO to entity
        Category category = categoryMapper.toEntity(categoryRequestDTO);

        Category savedCategory = categoryRepository.save(category);
        logger.info("Category created successfully with id: {}", savedCategory.getId());
        return categoryMapper.toResponseDTO(savedCategory);
    }

    public CategoryResponseDTO updateCategory(Byte id, CategoryRequestDTO categoryRequestDTO) {
        logger.debug("Updating category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Partial update - only update non-null, non-empty fields
        if (categoryRequestDTO.getName() != null && !categoryRequestDTO.getName().isEmpty()) {
            category.setName(categoryRequestDTO.getName());
        }

        Category updatedCategory = categoryRepository.save(category);
        logger.info("Category updated successfully with id: {}", id);
        return categoryMapper.toResponseDTO(updatedCategory);
    }

    public void deleteCategory(Byte id) {
        logger.debug("Deleting category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        categoryRepository.delete(category);
        logger.info("Category deleted successfully with id: {}", id);
    }
}
