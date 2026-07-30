package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.AuthResponse;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController.
 * Tests endpoints for login, registration, and token refresh.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
            .username("testuser")
            .password("password123")
            .build();

        registerRequest = RegisterRequest.builder()
            .username("newuser")
            .email("new@example.com")
            .password("SecurePass123!")
            .passwordConfirm("SecurePass123!")
            .firstName("John")
            .lastName("Doe")
            .build();

        authResponse = AuthResponse.builder()
            .accessToken("eyJhbGciOiJIUzUxMiJ9...")
            .tokenType("Bearer")
            .userId(1L)
            .username("testuser")
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of("MEMBER"))
            .expiresAt(new Date(System.currentTimeMillis() + 86400000))
            .build();
    }

    @Test
    void testLoginSuccessReturns200() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testLoginWithEmptyUsernameReturnsBadRequest() throws Exception {
        // Arrange
        LoginRequest invalidRequest = LoginRequest.builder()
            .username("")
            .password("password123")
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterSuccessReturns201() throws Exception {
        // Arrange
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void testRegisterWithMissingFieldsReturnsBadRequest() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = RegisterRequest.builder()
            .username("newuser")
            .email("new@example.com")
            // Missing password
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWithShortPasswordReturnsBadRequest() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = RegisterRequest.builder()
            .username("newuser")
            .email("new@example.com")
            .password("short")  // Too short
            .passwordConfirm("short")
            .firstName("John")
            .lastName("Doe")
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "MEMBER")
    void testRefreshTokenSuccessReturns200() throws Exception {
        // Arrange
        when(authService.refreshToken(any(String.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
            .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9...")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void testRefreshTokenWithoutAuthorizationHeaderReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

}
