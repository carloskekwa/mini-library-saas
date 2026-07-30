package com.library.controller;

import com.library.dto.UpdateProfileRequest;
import com.library.dto.UserDTO;
import com.library.entity.User;
import com.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for admin user management.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN', 'MEMBER')")
    @Operation(summary = "Get current profile", description = "Retrieve profile for the authenticated user")
    public ResponseEntity<UserDTO> getCurrentProfile(Authentication authentication) {
        User authUser = (User) authentication.getPrincipal();
        User user = userService.getUserById(authUser.getId());
        return ResponseEntity.ok(UserDTO.from(user));
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN', 'MEMBER')")
    @Operation(summary = "Update current profile", description = "Update profile fields based on role permissions")
    public ResponseEntity<UserDTO> updateCurrentProfile(
        Authentication authentication,
        @Valid @RequestBody UpdateProfileRequest request) {

        User authUser = (User) authentication.getPrincipal();
        boolean isAdmin = hasRole(authUser, "ADMIN");
        boolean isLibrarian = hasRole(authUser, "LIBRARIAN");

        if (!isAdmin && request.getUsername() != null && !request.getUsername().isBlank()) {
            throw new IllegalStateException("Only admins can update username");
        }

        if (!isAdmin && !isLibrarian && request.getEmail() != null && !request.getEmail().isBlank()) {
            throw new IllegalStateException("Only librarians and admins can update email");
        }

        User updatedUser = userService.updateUserProfile(
            authUser.getId(),
            isAdmin ? request.getUsername() : null,
            request.getFirstName(),
            request.getLastName(),
            (isAdmin || isLibrarian) ? request.getEmail() : null
        );

        return ResponseEntity.ok(UserDTO.from(updatedUser));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Search users", description = "Paginated user search by username/email and optional status")
    public ResponseEntity<Page<UserDTO>> searchUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String status) {

        Page<User> users = userService.searchUsers(search, status, page, pageSize);
        return ResponseEntity.ok(users.map(UserDTO::from));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get user by id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserDTO.from(user));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user status")
    public ResponseEntity<UserDTO> updateUserStatus(
        @PathVariable Long id,
        @RequestParam String status) {

        User updatedUser = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(UserDTO.from(updatedUser));
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream().anyMatch(role -> roleName.equals(role.getName()));
    }
}
