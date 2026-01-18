package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.annotation.Auditable;
import com.stockmanagement.inventory.application.dto.command.RegisterUserCommand;
import com.stockmanagement.inventory.application.dto.command.UpdateUserCommand;
import com.stockmanagement.inventory.application.dto.response.UserResponse;
import com.stockmanagement.inventory.domain.model.Role;
import com.stockmanagement.inventory.domain.model.User;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.RoleRepository;
import com.stockmanagement.inventory.domain.repository.UserRepository;
import com.stockmanagement.inventory.domain.exception.UserNotFoundException;
import com.stockmanagement.inventory.domain.exception.RoleNotFoundException;
import com.stockmanagement.inventory.domain.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Auditable(action = "REGISTER_USER", resource = "USER")
    public void registerUser(RegisterUserCommand command) {
        if (userRepository.existsByUsername(new Username(command.username()))) {
            throw new UserAlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        Role role = roleRepository.findByName(command.roleName())
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + command.roleName()));

        User newUser = new User(
                new UserId(UUID.randomUUID()),
                new Username(command.username()),
                new Email(command.email()),
                new Password(passwordEncoder.encode(command.password())),
                command.firstName(),
                command.lastName());

        newUser.addRole(role);

        userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersPaged(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    @Auditable(action = "UPDATE_USER", resource = "USER")
    public void updateUser(UUID userId, UpdateUserCommand command) {
        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update fields
        user.updateProfile(command.firstName(), command.lastName(), new Email(command.email()));

        // Update Role if provided
        if (command.roleName() != null && !command.roleName().isBlank()) {
            Role role = roleRepository.findByName(command.roleName())
                    .orElseThrow(() -> new RoleNotFoundException("Role not found: " + command.roleName()));

            // Clear existing and add new (assuming single role for now for simplicity, or
            // add logic to manage set)
            user.getRoles().clear();
            user.addRole(role);
        }

        userRepository.save(user);
    }

    @Transactional
    @Auditable(action = "TOGGLE_USER_STATUS", resource = "USER")
    public void toggleUserStatus(UUID userId) {
        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isActive()) {
            user.deactivate();
        } else {
            user.activate();
        }
        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId().value())
                .username(user.getUsername().value())
                .email(user.getEmail().value())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(user.isActive())
                .isLocked(user.isLocked())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()))
                .build();
    }
}
