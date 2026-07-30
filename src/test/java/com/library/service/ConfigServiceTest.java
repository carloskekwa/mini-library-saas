package com.library.service;
import com.library.dto.ConfigPropertyDTO;
import com.library.entity.ConfigProperty;
import com.library.repository.ConfigPropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {
    @Mock
    private ConfigPropertyRepository configPropertyRepository;
    
    private ConfigService configService;

    @BeforeEach
    void setUp() {
        configService = new ConfigService(configPropertyRepository);
    }

    @Test
    void testGetConfig() {
        ConfigProperty config = new ConfigProperty("library.name", "My Library", "Library name", ConfigProperty.ConfigType.STRING);
        config.setId(1L);

        when(configPropertyRepository.findByKey("library.name")).thenReturn(Optional.of(config));

        ConfigPropertyDTO result = configService.getConfig("library.name");

        assertNotNull(result);
        assertEquals("My Library", result.getValue());
    }

    @Test
    void testGetConfigValue() {
        ConfigProperty config = new ConfigProperty("max.books", "100", "Max books", ConfigProperty.ConfigType.INTEGER);
        when(configPropertyRepository.findByKey("max.books")).thenReturn(Optional.of(config));

        String result = configService.getConfigValue("max.books", "50");

        assertEquals("100", result);
    }

    @Test
    void testSetConfig() {
        ConfigProperty config = new ConfigProperty("library.active", "true", "Library active", ConfigProperty.ConfigType.BOOLEAN);
        config.setId(1L);

        when(configPropertyRepository.findByKey("library.active")).thenReturn(Optional.of(config));
        when(configPropertyRepository.save(any(ConfigProperty.class))).thenReturn(config);

        configService.setConfig("library.active", "false", "Library active", ConfigProperty.ConfigType.BOOLEAN);

        verify(configPropertyRepository, times(1)).save(any(ConfigProperty.class));
    }

    @Test
    void testGetAllConfigs() {
        ConfigProperty config1 = new ConfigProperty("key1", "value1", "Config 1", ConfigProperty.ConfigType.STRING);
        config1.setId(1L);
        when(configPropertyRepository.findAll()).thenReturn(Arrays.asList(config1));

        List<ConfigPropertyDTO> result = configService.getAllConfigs();

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteConfig() {
        ConfigProperty config = new ConfigProperty("library.name", "My Library", "Library name", ConfigProperty.ConfigType.STRING);
        config.setId(1L);

        when(configPropertyRepository.findByKey("library.name")).thenReturn(Optional.of(config));

        configService.deleteConfig("library.name");

        verify(configPropertyRepository, times(1)).delete(config);
    }
}
