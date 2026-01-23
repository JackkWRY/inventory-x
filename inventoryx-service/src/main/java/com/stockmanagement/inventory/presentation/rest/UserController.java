package com.stockmanagement.inventory.presentation.rest;

import com.stockmanagement.inventory.application.dto.command.RegisterUserCommand;
import com.stockmanagement.inventory.application.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManagementService userManagementService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RegisterUserCommand command) {
        userManagementService.registerUser(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @org.springframework.web.bind.annotation.GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<org.springframework.data.domain.Page<com.stockmanagement.inventory.application.dto.response.UserResponse>> getUsers(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String search,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(userManagementService.getUsersPaged(search, pageable));
    }

    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUser(
            @org.springframework.web.bind.annotation.PathVariable java.util.UUID id,
            @Valid @RequestBody com.stockmanagement.inventory.application.dto.command.UpdateUserCommand command) {
        userManagementService.updateUser(id, command);
        return ResponseEntity.ok().build();
    }

    @org.springframework.web.bind.annotation.PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleUserStatus(
            @org.springframework.web.bind.annotation.PathVariable java.util.UUID id) {
        userManagementService.toggleUserStatus(id);
        return ResponseEntity.ok().build();
    }
}
