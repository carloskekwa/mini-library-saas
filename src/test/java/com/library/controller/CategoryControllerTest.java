package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.CategoryDTO;
import com.library.dto.CreateCategoryRequest;
import com.library.service.CategoryService;
import com.library.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CategoryController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private Category testCategory;
    private CreateCategoryRequest createRequest;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Fiction", "Fiction books");
        testCategory.setId(1L);

        createRequest = new CreateCategoryRequest("Fiction", "Fiction books");
    }

    @Test
    void testGetAllCategoriesReturns200() throws Exception {
        // Arrange
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/categories")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Fiction"));
    }

    @Test
    void testGetCategoryByIdReturns200() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);

        // Act & Assert
        mockMvc.perform(get("/api/categories/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Fiction"));
    }

    @Test
    @WithMockUser(username = "librarian", roles = "LIBRARIAN")
    void testCreateCategoryReturns201() throws Exception {
        // Arrange
        when(categoryService.createCategory("Fiction", "Fiction books")).thenReturn(testCategory);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Fiction"));
    }

    @Test
    void testCreateCategoryWithoutAuthenticationReturns403() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateCategoryReturns200() throws Exception {
        // Arrange
        when(categoryService.updateCategory(anyLong(), any(), any())).thenReturn(testCategory);

        // Act & Assert
        mockMvc.perform(put("/api/categories/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Fiction"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testDeleteCategoryReturns204() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/categories/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

}
