package com.library.service;

import com.library.entity.Category;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService.
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Fiction", "Fiction books");
        testCategory.setId(1L);
    }

    @Test
    void testGetCategoryByIdSuccess() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        Category result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Fiction", result.getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void testGetCategoryByIdThrowsNotFoundException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(999L));
    }

    @Test
    void testCreateCategorySuccess() {
        // Arrange
        when(categoryRepository.existsByName("Fiction")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category result = categoryService.createCategory("Fiction", "Fiction books");

        // Assert
        assertNotNull(result);
        assertEquals("Fiction", result.getName());
        verify(categoryRepository).existsByName("Fiction");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreateCategoryWithDuplicateNameThrowsException() {
        // Arrange
        when(categoryRepository.existsByName("Fiction")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> categoryService.createCategory("Fiction", "Fiction books"));
    }

    @Test
    void testUpdateCategorySuccess() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Science")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category result = categoryService.updateCategory(1L, "Science", "Science books");

        // Assert
        assertNotNull(result);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testDeleteCategorySuccess() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(testCategory);
    }

}
