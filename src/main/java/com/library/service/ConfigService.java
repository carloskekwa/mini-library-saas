package com.library.service;
import com.library.dto.ConfigPropertyDTO;
import com.library.entity.ConfigProperty;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.ConfigPropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for system configuration (Phase 18).
 */
@Service
@Transactional
public class ConfigService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);
    private final ConfigPropertyRepository configPropertyRepository;

    public ConfigService(ConfigPropertyRepository configPropertyRepository) {
        this.configPropertyRepository = configPropertyRepository;
    }

    public ConfigPropertyDTO getConfig(String key) {
        logger.info("Getting config: {}", key);
        ConfigProperty config = configPropertyRepository.findByKey(key)
            .orElseThrow(() -> new ResourceNotFoundException("Config not found: " + key));
        return ConfigPropertyDTO.from(config);
    }

    public String getConfigValue(String key, String defaultValue) {
        return configPropertyRepository.findByKey(key)
            .map(ConfigProperty::getValue)
            .orElse(defaultValue);
    }

    public void setConfig(String key, String value, String description, ConfigProperty.ConfigType type) {
        logger.info("Setting config: {}={}", key, value);
        
        ConfigProperty config = configPropertyRepository.findByKey(key)
            .orElse(new ConfigProperty(key, value, description, type));
        
        config.setValue(value);
        config.setDescription(description);
        config.setUpdatedAt(LocalDateTime.now());
        configPropertyRepository.save(config);
    }

    @Transactional(readOnly = true)
    public List<ConfigPropertyDTO> getAllConfigs() {
        return configPropertyRepository.findAll()
            .stream().map(ConfigPropertyDTO::from).collect(Collectors.toList());
    }

    public void deleteConfig(String key) {
        logger.info("Deleting config: {}", key);
        ConfigProperty config = configPropertyRepository.findByKey(key)
            .orElseThrow(() -> new ResourceNotFoundException("Config not found"));
        configPropertyRepository.delete(config);
    }
}
