package com.library.service;

import com.library.dto.UserDTO;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Service for user management operations.
 * Handles user CRUD, role assignment, and user-related business logic.
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get user by ID.
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    /**
     * Get user by username.
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Get user by email.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Search users for admin operations.
     */
    public Page<User> searchUsers(String search, String status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        User.UserStatus parsedStatus = null;

        if (status != null && !status.isBlank()) {
            parsedStatus = User.UserStatus.valueOf(status.toUpperCase());
        }

        return userRepository.searchUsers(search, parsedStatus, pageable);
    }

    /**
     * Create a new user.
     * Assigns default MEMBER role if no roles provided.
     */
    public User createUser(String username, String email, String password, String firstName, String lastName) {
        // Validate username and email uniqueness
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // Create user with encoded password
        User user = new User(username, email, passwordEncoder.encode(password), firstName, lastName);
        user.setStatus(User.UserStatus.ACTIVE);
        user.setRoles(new HashSet<>());

        // Assign MEMBER role by default
        Role memberRole = roleRepository.findByName("MEMBER")
            .orElseThrow(() -> new ResourceNotFoundException("Default MEMBER role not found"));
        user.getRoles().add(memberRole);

        User savedUser = userRepository.save(user);
        logger.info("User created successfully: {}", username);
        return savedUser;
    }

    /**
     * Update user information.
     */
    public User updateUser(Long userId, String firstName, String lastName, String email) {
        User user = getUserById(userId);

        // Check if new email is unique
        if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (email != null) {
            user.setEmail(email);
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", user.getUsername());
        return updatedUser;
    }

    /**
     * Update profile information for an existing user with optional username updates.
     */
    public User updateUserProfile(Long userId, String username, String firstName, String lastName, String email) {
        User user = getUserById(userId);

        if (username != null) {
            String normalizedUsername = username.trim();
            if (!normalizedUsername.isEmpty() && !normalizedUsername.equals(user.getUsername())) {
                if (userRepository.existsByUsername(normalizedUsername)) {
                    throw new IllegalArgumentException("Username already exists: " + normalizedUsername);
                }
                user.setUsername(normalizedUsername);
            }
        }

        if (email != null) {
            String normalizedEmail = email.trim();
            if (!normalizedEmail.isEmpty() && !normalizedEmail.equals(user.getEmail()) && userRepository.existsByEmail(normalizedEmail)) {
                throw new IllegalArgumentException("Email already exists: " + normalizedEmail);
            }
            if (!normalizedEmail.isEmpty()) {
                user.setEmail(normalizedEmail);
            }
        }

        if (firstName != null) {
            user.setFirstName(firstName.trim());
        }
        if (lastName != null) {
            user.setLastName(lastName.trim());
        }

        User updatedUser = userRepository.save(user);
        logger.info("Profile updated successfully: {}", updatedUser.getUsername());
        return updatedUser;
    }

    /**
     * Assign role to user.
     */
    public User assignRoleToUser(Long userId, String roleName) {
        User user = getUserById(userId);
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        logger.info("Role {} assigned to user: {}", roleName, user.getUsername());
        return updatedUser;
    }

    /**
     * Remove role from user.
     */
    public User removeRoleFromUser(Long userId, String roleName) {
        User user = getUserById(userId);
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        user.getRoles().remove(role);
        User updatedUser = userRepository.save(user);
        logger.info("Role {} removed from user: {}", roleName, user.getUsername());
        return updatedUser;
    }

    /**
     * Get user roles.
     */
    public Set<String> getUserRoles(Long userId) {
        User user = getUserById(userId);
        return user.getRoles().stream()
            .map(Role::getName)
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Change user password.
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Set new password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Password changed for user: {}", user.getUsername());
    }

    /**
     * Deactivate user.
     */
    public User deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setStatus(User.UserStatus.INACTIVE);
        User updatedUser = userRepository.save(user);
        logger.info("User deactivated: {}", user.getUsername());
        return updatedUser;
    }

    /**
     * Activate user.
     */
    public User activateUser(Long userId) {
        User user = getUserById(userId);
        user.setStatus(User.UserStatus.ACTIVE);
        User updatedUser = userRepository.save(user);
        logger.info("User activated: {}", user.getUsername());
        return updatedUser;
    }

    /**
     * Set user status directly.
     */
    public User updateUserStatus(Long userId, String status) {
        User user = getUserById(userId);
        User.UserStatus userStatus = User.UserStatus.valueOf(status.toUpperCase());
        user.setStatus(userStatus);
        User updatedUser = userRepository.save(user);
        logger.info("User status updated: {} -> {}", user.getUsername(), userStatus);
        return updatedUser;
    }

    /**
     * Delete user.
     */
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
        logger.info("User deleted: {}", user.getUsername());
    }

    /**
     * Check if user exists by username.
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if user exists by email.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
