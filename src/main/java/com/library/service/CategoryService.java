package com.library.service;

import com.library.entity.Category;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for category management operations.
 * Handles category CRUD operations and validations.
 */
@Service
@Transactional
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Get all categories.
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Get category by ID.
     */
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    /**
     * Get category by name.
     */
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
    }

    /**
     * Create a new category.
     */
    public Category createCategory(String name, String description) {
        // Check if category already exists
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category already exists: " + name);
        }

        Category category = new Category(name, description);
        Category savedCategory = categoryRepository.save(category);
        logger.info("Category created successfully: {}", name);
        return savedCategory;
    }

    /**
     * Update category information.
     */
    public Category updateCategory(Long categoryId, String name, String description) {
        Category category = getCategoryById(categoryId);

        // Check if new name is unique
        if (name != null && !name.equals(category.getName()) && categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category name already exists: " + name);
        }

        if (name != null) {
            category.setName(name);
        }
        if (description != null) {
            category.setDescription(description);
        }

        Category updatedCategory = categoryRepository.save(category);
        logger.info("Category updated successfully: {}", category.getName());
        return updatedCategory;
    }

    /**
     * Delete category.
     */
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        categoryRepository.delete(category);
        logger.info("Category deleted: {}", category.getName());
    }

}
