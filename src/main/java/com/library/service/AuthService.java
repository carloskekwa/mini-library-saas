package com.library.service;

import com.library.dto.AuthResponse;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Service for authentication operations.
 * Handles user login, registration, and token refresh.
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Helper method to convert java.util.Date to java.time.LocalDateTime
     */
    private LocalDateTime convertDateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

    /**
     * Authenticate user and return JWT token.
     */
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Get authenticated user
            User user = userService.getUserByUsername(loginRequest.getUsername());

            // Generate JWT token
            String jwtToken = jwtTokenProvider.generateToken(authentication);

            logger.info("User logged in successfully: {}", user.getUsername());

            // Build auth response
            AuthResponse authResponse = new AuthResponse();
            authResponse.setAccessToken(jwtToken);
            authResponse.setTokenType("Bearer");
            authResponse.setUserId(user.getId());
            authResponse.setUsername(user.getUsername());
            authResponse.setEmail(user.getEmail());
            authResponse.setFirstName(user.getFirstName());
            authResponse.setLastName(user.getLastName());
            authResponse.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()));
            authResponse.setExpiresAt(convertDateToLocalDateTime(jwtTokenProvider.getExpirationDateFromJwt(jwtToken)));
            return authResponse;
        } catch (Exception ex) {
            logger.error("Authentication failed for user: {}", loginRequest.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    /**
     * Register a new user.
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        // Validate passwords match
        if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirm())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Validate password strength
        validatePasswordStrength(registerRequest.getPassword());

        // Create user
        User user = userService.createUser(
            registerRequest.getUsername(),
            registerRequest.getEmail(),
            registerRequest.getPassword(),
            registerRequest.getFirstName(),
            registerRequest.getLastName()
        );

        logger.info("User registered successfully: {}", user.getUsername());

        // Authenticate and return token
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                registerRequest.getUsername(),
                registerRequest.getPassword()
            )
        );

        String jwtToken = jwtTokenProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(jwtToken);
        authResponse.setTokenType("Bearer");
        authResponse.setUserId(user.getId());
        authResponse.setUsername(user.getUsername());
        authResponse.setEmail(user.getEmail());
        authResponse.setFirstName(user.getFirstName());
        authResponse.setLastName(user.getLastName());
        authResponse.setRoles(user.getRoles().stream()
            .map(role -> role.getName())
            .collect(Collectors.toSet()));
        authResponse.setExpiresAt(convertDateToLocalDateTime(jwtTokenProvider.getExpirationDateFromJwt(jwtToken)));
        return authResponse;
    }

    /**
     * Refresh JWT token.
     * Validates existing token and returns new one.
     */
    public AuthResponse refreshToken(String token) {
        // Remove "Bearer " prefix if present
        String jwt = jwtTokenProvider.getTokenFromBearerString(token);

        if (!jwtTokenProvider.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        String username = jwtTokenProvider.getUsernameFromJwt(jwt);
        User user = userService.getUserByUsername(username);

        // Generate new token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities()
        );
        String newToken = jwtTokenProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(newToken);
        authResponse.setTokenType("Bearer");
        authResponse.setUserId(user.getId());
        authResponse.setUsername(user.getUsername());
        authResponse.setEmail(user.getEmail());
        authResponse.setFirstName(user.getFirstName());
        authResponse.setLastName(user.getLastName());
        authResponse.setRoles(user.getRoles().stream()
            .map(role -> role.getName())
            .collect(Collectors.toSet()));
        authResponse.setExpiresAt(convertDateToLocalDateTime(jwtTokenProvider.getExpirationDateFromJwt(newToken)));
        return authResponse;
    }

    /**
     * Validate password strength.
     * Requirements: min 8 chars, uppercase, lowercase, number, special char
     */
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }

}
