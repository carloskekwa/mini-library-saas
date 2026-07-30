package com.library.dto;
import com.library.entity.ConfigProperty;
import java.time.LocalDateTime;

public class ConfigPropertyDTO {
    private Long id;
    private String key;
    private String value;
    private String description;
    private String type;
    private Boolean isEditable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ConfigPropertyDTO() {}
    public ConfigPropertyDTO(Long id, String key, String value, String description, String type, Boolean isEditable, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.description = description;
        this.type = type;
        this.isEditable = isEditable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ConfigPropertyDTO from(ConfigProperty config) {
        return new ConfigPropertyDTO(config.getId(), config.getKey(), config.getValue(), config.getDescription(), config.getType().name(), config.getIsEditable(), config.getCreatedAt(), config.getUpdatedAt());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getIsEditable() { return isEditable; }
    public void setIsEditable(Boolean isEditable) { this.isEditable = isEditable; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
