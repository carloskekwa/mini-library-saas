package com.library.service;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role memberRole;

    @BeforeEach
    void setUp() {
        memberRole = Role.builder()
            .id(1L)
            .name("MEMBER")
            .description("Regular member")
            .build();

        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .passwordHash("hashed_password")
            .firstName("John")
            .lastName("Doe")
            .status(User.UserStatus.ACTIVE)
            .roles(new HashSet<>(Set.of(memberRole)))
            .build();
    }

    @Test
    void testGetUserByIdSuccess() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserByIdThrowsNotFoundException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void testGetUserByUsernameSuccess() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testCreateUserSuccess() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed_new_password");
        when(roleRepository.findByName("MEMBER")).thenReturn(Optional.of(memberRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser("newuser", "new@example.com", "password", "John", "Doe");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode("password");
        verify(roleRepository).findByName("MEMBER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserWithDuplicateUsernameThrowsException() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> userService.createUser("testuser", "new@example.com", "password", "John", "Doe"));
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void testCreateUserWithDuplicateEmailThrowsException() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> userService.createUser("newuser", "test@example.com", "password", "John", "Doe"));
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void testUpdateUserSuccess() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser(1L, "Jane", "Smith", "new@example.com");

        // Assert
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testAssignRoleToUserSuccess() {
        // Arrange
        Role adminRole = Role.builder()
            .id(2L)
            .name("ADMIN")
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.assignRoleToUser(1L, "ADMIN");

        // Assert
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(roleRepository).findByName("ADMIN");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeactivateUserSuccess() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.deactivateUser(1L);

        // Assert
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testExistsByUsernameReturnsTrue() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean result = userService.existsByUsername("testuser");

        // Assert
        assertTrue(result);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void testExistsByEmailReturnsFalse() {
        // Arrange
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act
        boolean result = userService.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

}
