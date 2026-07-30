package com.library.controller;

import com.library.dto.AuthResponse;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST Controller.
 * Handles user login, registration, and token operations.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for login, registration, and token management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * User login endpoint.
     * Returns JWT token on successful authentication.
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with credentials")
    @ApiResponse(responseCode = "200", description = "Login successful",
        content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * User registration endpoint.
     * Creates new user and returns JWT token.
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register new user account")
    @ApiResponse(responseCode = "201", description = "Registration successful",
        content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    @ApiResponse(responseCode = "400", description = "Validation error or password mismatch")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    /**
     * Token refresh endpoint.
     * Validates existing token and returns new one.
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token", description = "Get new JWT token using existing token")
    @ApiResponse(responseCode = "200", description = "Token refresh successful",
        content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestHeader("Authorization") String bearerToken) {
        AuthResponse authResponse = authService.refreshToken(bearerToken);
        return ResponseEntity.ok(authResponse);
    }

}
