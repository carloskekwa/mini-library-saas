package com.library.controller;

import com.library.dto.UserDTO;
import com.library.entity.User;
import com.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
}
