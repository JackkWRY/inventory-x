package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.command.LoginCommand;
import com.stockmanagement.inventory.application.dto.command.RefreshTokenCommand;
import com.stockmanagement.inventory.application.dto.response.AuthResponse;
import com.stockmanagement.inventory.domain.model.Role;
import com.stockmanagement.inventory.domain.model.User;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.UserRepository;
import com.stockmanagement.inventory.infrastructure.security.JwtTokenProvider;
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

import java.util.Optional;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role(
                new RoleId("role-1"),
                "ROLE_USER",
                "User Role",
                Collections.emptySet());

        user = new User(
                new UserId(UUID.randomUUID()),
                new Username("testuser"),
                new Email("test@example.com"),
                new Password("password123"),
                "Test",
                "User");
        user.addRole(role);
    }

    @Test
    void shouldLoginSuccessfully() {
        // Arrange
        LoginCommand command = new LoginCommand("testuser", "password123");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername(any(Username.class))).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        // Act
        AuthResponse response = authenticationService.login(command);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(userRepository).save(user); // verify loginSuccess update
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() {
        // Arrange
        LoginCommand command = new LoginCommand("testuser", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        when(userRepository.findByUsername(any(Username.class))).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authenticationService.login(command));
        verify(userRepository).save(user); // verify loginFailed update
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        // Arrange
        RefreshTokenCommand command = new RefreshTokenCommand("valid-refresh-token");

        when(jwtTokenProvider.extractUsername("valid-refresh-token")).thenReturn("testuser");
        when(jwtTokenProvider.isTokenValid("valid-refresh-token", "testuser")).thenReturn(true);
        when(userRepository.findByUsername(any(Username.class))).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken(any(User.class))).thenReturn("new-refresh-token");

        // Act
        AuthResponse response = authenticationService.refreshToken(command);

        // Assert
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
    }

    @Test
    void shouldFailRefreshTokenIfInvalid() {
        // Arrange
        RefreshTokenCommand command = new RefreshTokenCommand("invalid-refresh-token");

        when(jwtTokenProvider.extractUsername("invalid-refresh-token")).thenReturn("testuser");
        when(jwtTokenProvider.isTokenValid("invalid-refresh-token", "testuser")).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authenticationService.refreshToken(command));
    }
}
