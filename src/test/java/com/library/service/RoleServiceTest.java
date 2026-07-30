package com.library.service;

import com.library.entity.Role;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.RoleRepository;
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
 * Unit tests for RoleService.
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
            .id(1L)
            .name("ADMIN")
            .description("Administrator role")
            .build();
    }

    @Test
    void testGetRoleByIdSuccess() {
        // Arrange
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));

        // Act
        Role result = roleService.getRoleById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        verify(roleRepository).findById(1L);
    }

    @Test
    void testGetRoleByIdThrowsNotFoundException() {
        // Arrange
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(999L));
        verify(roleRepository).findById(999L);
    }

    @Test
    void testGetRoleByNameSuccess() {
        // Arrange
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(testRole));

        // Act
        Role result = roleService.getRoleByName("ADMIN");

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        verify(roleRepository).findByName("ADMIN");
    }

    @Test
    void testCreateRoleSuccess() {
        // Arrange
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        Role result = roleService.createRole("ADMIN", "Administrator role");

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        verify(roleRepository).findByName("ADMIN");
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testCreateRoleWithDuplicateNameThrowsException() {
        // Arrange
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(testRole));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> roleService.createRole("ADMIN", "Administrator role"));
        verify(roleRepository).findByName("ADMIN");
    }

    @Test
    void testUpdateRoleSuccess() {
        // Arrange
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        Role result = roleService.updateRole(1L, "Updated description");

        // Assert
        assertNotNull(result);
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testDeleteRoleSuccess() {
        // Arrange
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));

        // Act
        roleService.deleteRole(1L);

        // Assert
        verify(roleRepository).findById(1L);
        verify(roleRepository).delete(testRole);
    }

}
