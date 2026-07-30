package com.library.controller;
import com.library.dto.ConfigPropertyDTO;
import com.library.entity.ConfigProperty;
import com.library.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for configuration (Phase 18).
 */
@RestController
@RequestMapping("/api/config")
@Tag(name = "Configuration Management", description = "APIs for system configuration")
public class ConfigController {
    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get config property")
    public ResponseEntity<ConfigPropertyDTO> getConfig(@PathVariable String key) {
        ConfigPropertyDTO config = configService.getConfig(key);
        return ResponseEntity.ok(config);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all configs")
    public ResponseEntity<List<ConfigPropertyDTO>> getAllConfigs() {
        List<ConfigPropertyDTO> configs = configService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update config")
    public ResponseEntity<Map<String, String>> updateConfig(
        @RequestParam String key,
        @RequestParam String value,
        @RequestParam(required = false) String description,
        @RequestParam ConfigProperty.ConfigType type) {
        configService.setConfig(key, value, description, type);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Config updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete config")
    public ResponseEntity<Void> deleteConfig(@PathVariable String key) {
        configService.deleteConfig(key);
        return ResponseEntity.noContent().build();
    }
}
