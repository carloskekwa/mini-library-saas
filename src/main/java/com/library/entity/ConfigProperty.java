package com.library.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ConfigProperty entity for system configuration (Phase 18).
 */
@Entity
@Table(name = "config_properties", indexes = {
    @Index(name = "idx_config_key", columnList = "property_key", unique = true)
})
public class ConfigProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_key", nullable = false, unique = true, length = 255)
    private String key;

    @Column(columnDefinition = "TEXT")
    private String value;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ConfigType type;

    @Column(name = "is_editable")
    private Boolean isEditable = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ConfigType { STRING, INTEGER, BOOLEAN, DECIMAL }

    public ConfigProperty() {}
    public ConfigProperty(String key, String value, String description, ConfigType type) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.type = type;
        this.isEditable = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ConfigType getType() { return type; }
    public void setType(ConfigType type) { this.type = type; }
    public Boolean getIsEditable() { return isEditable; }
    public void setIsEditable(Boolean isEditable) { this.isEditable = isEditable; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
