package com.stockmanagement.inventory.presentation.rest;

import com.stockmanagement.inventory.application.dto.command.LoginCommand;
import com.stockmanagement.inventory.application.dto.response.AuthResponse;
import com.stockmanagement.inventory.application.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginCommand command) {
        return ResponseEntity.ok(authenticationService.login(command));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody com.stockmanagement.inventory.application.dto.command.RefreshTokenCommand command) {
        return ResponseEntity.ok(authenticationService.refreshToken(command));
    }
}
