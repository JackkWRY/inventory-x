package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.command.RegisterUserCommand;
import com.stockmanagement.inventory.application.dto.command.UpdateUserCommand;
import com.stockmanagement.inventory.application.dto.response.UserResponse;
import com.stockmanagement.inventory.domain.model.Role;
import com.stockmanagement.inventory.domain.model.User;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.RoleRepository;
import com.stockmanagement.inventory.domain.repository.UserRepository;
import com.stockmanagement.inventory.domain.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementService userManagementService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role(
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
    void shouldRegisterUserSuccessfully() {
        // Arrange
        RegisterUserCommand command = new RegisterUserCommand(
                "newuser", "new@example.com", "password123", "New", "User", "ROLE_USER");

        when(userRepository.existsByUsername(any(Username.class))).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        userManagementService.registerUser(command);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldFailRegisterIfUsernameExists() {
        // Arrange
        RegisterUserCommand command = new RegisterUserCommand(
                "testuser", "new@example.com", "password123", "New", "User", "ROLE_USER");

        when(userRepository.existsByUsername(any(Username.class))).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userManagementService.registerUser(command));
    }

    @Test
    void shouldGetUsersPaged() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        Page<UserResponse> result = userManagementService.getUsersPaged(null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).username());
    }

    @Test
    void shouldUpdateUser() {
        // Arrange
        UUID userId = user.getId().value();
        UpdateUserCommand command = new UpdateUserCommand(
                "Updated", "Name", "updated@example.com", "ROLE_USER");

        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        // Act
        userManagementService.updateUser(userId, command);

        // Assert
        assertEquals("Updated", user.getFirstName());
        assertEquals("updated@example.com", user.getEmail().value());
        verify(userRepository).save(user);
    }

    @Test
    void shouldToggleUserStatus() {
        // Arrange
        UUID userId = user.getId().value();
        boolean initialStatus = user.isActive();
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(user));

        // Act
        userManagementService.toggleUserStatus(userId);

        // Assert
        assertNotEquals(initialStatus, user.isActive());
        verify(userRepository).save(user);
    }
}
