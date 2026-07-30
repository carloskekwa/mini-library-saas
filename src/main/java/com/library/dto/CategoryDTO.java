package com.library.dto;

import com.library.entity.Category;

/**
 * DTO for category response.
 */
public class CategoryDTO {

    private Long id;
    private String name;
    private String description;

    /**
     * Constructors
     */
    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Convert Category entity to CategoryDTO.
     */
    public static CategoryDTO from(Category category) {
        return new CategoryDTO(category.getId(), category.getName(), category.getDescription());
    }

    /**
     * Getters and Setters
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
