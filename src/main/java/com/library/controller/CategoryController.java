package com.library.controller;

import com.library.dto.CategoryDTO;
import com.library.dto.CreateCategoryRequest;
import com.library.entity.Category;
import com.library.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Category REST Controller.
 * Handles category CRUD operations.
 */
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Get all categories.
     */
    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve all book categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> dtos = categories.stream()
            .map(CategoryDTO::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get category by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve a specific category")
    @ApiResponse(responseCode = "200", description = "Category retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(CategoryDTO.from(category));
    }

    /**
     * Create new category (Admin/Librarian only).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Create new category", description = "Create a new book category")
    @ApiResponse(responseCode = "201", description = "Category created successfully",
        content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        Category category = categoryService.createCategory(request.getName(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryDTO.from(category));
    }

    /**
     * Update category (Admin/Librarian only).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Update category", description = "Update category information")
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CreateCategoryRequest request) {
        Category category = categoryService.updateCategory(id, request.getName(), request.getDescription());
        return ResponseEntity.ok(CategoryDTO.from(category));
    }

    /**
     * Delete category (Admin only).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete category", description = "Delete a category")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
