package com.library.service;

import com.library.dto.AuthResponse;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role memberRole;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        memberRole = Role.builder()
            .id(1L)
            .name("MEMBER")
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

        jwtToken = "eyJhbGciOiJIUzUxMiJ9...";
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .username("testuser")
            .password("password123")
            .build();

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(jwtToken);
        when(jwtTokenProvider.getExpirationDateFromJwt(jwtToken))
            .thenReturn(new Date(System.currentTimeMillis() + 86400000));

        // Act
        AuthResponse result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(jwtToken, result.getAccessToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Bearer", result.getTokenType());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authentication);
    }

    @Test
    void testLoginWithInvalidCredentialsThrowsException() {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .username("testuser")
            .password("wrongpassword")
            .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void testRegisterSuccess() {
        // Arrange
        RegisterRequest registerRequest = RegisterRequest.builder()
            .username("newuser")
            .email("new@example.com")
            .password("SecurePass123!")
            .passwordConfirm("SecurePass123!")
            .firstName("Jane")
            .lastName("Smith")
            .build();

        Authentication authentication = mock(Authentication.class);
        when(userService.createUser(anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(jwtToken);
        when(jwtTokenProvider.getExpirationDateFromJwt(jwtToken))
            .thenReturn(new Date(System.currentTimeMillis() + 86400000));

        // Act
        AuthResponse result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(jwtToken, result.getAccessToken());
        verify(userService).createUser(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testRegisterWithPasswordMismatchThrowsException() {
        // Arrange
        RegisterRequest registerRequest = RegisterRequest.builder()
            .username("newuser")
            .email("new@example.com")
            .password("SecurePass123!")
            .passwordConfirm("DifferentPass123!")
            .firstName("Jane")
            .lastName("Smith")
            .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }

    @Test
    void testRegisterWithWeakPasswordThrowsException() {
        // Arrange
        RegisterRequest registerRequest = RegisterRequest.builder()
            .username("newuser")
            .email("new@example.com")
            .password("weak")  // Too weak
            .passwordConfirm("weak")
            .firstName("Jane")
            .lastName("Smith")
            .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }

    @Test
    void testRefreshTokenSuccess() {
        // Arrange
        String bearerToken = "Bearer " + jwtToken;
        Authentication authentication = mock(Authentication.class);

        when(jwtTokenProvider.getTokenFromBearerString(bearerToken)).thenReturn(jwtToken);
        when(jwtTokenProvider.validateToken(jwtToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJwt(jwtToken)).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(jwtToken);
        when(jwtTokenProvider.getExpirationDateFromJwt(jwtToken))
            .thenReturn(new Date(System.currentTimeMillis() + 86400000));

        // Act
        AuthResponse result = authService.refreshToken(bearerToken);

        // Assert
        assertNotNull(result);
        assertEquals(jwtToken, result.getAccessToken());
        verify(jwtTokenProvider).validateToken(jwtToken);
        verify(jwtTokenProvider).getUsernameFromJwt(jwtToken);
    }

    @Test
    void testRefreshTokenWithInvalidTokenThrowsException() {
        // Arrange
        String bearerToken = "Bearer invalid_token";

        when(jwtTokenProvider.getTokenFromBearerString(bearerToken)).thenReturn("invalid_token");
        when(jwtTokenProvider.validateToken("invalid_token")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.refreshToken(bearerToken));
    }

}
