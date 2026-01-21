package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.command.LoginCommand;
import com.stockmanagement.inventory.application.dto.response.AuthResponse;
import com.stockmanagement.inventory.domain.model.Permission;
import com.stockmanagement.inventory.domain.model.Role;
import com.stockmanagement.inventory.domain.model.User;
import com.stockmanagement.inventory.domain.model.valueobject.Username;
import com.stockmanagement.inventory.domain.repository.UserRepository;
import com.stockmanagement.inventory.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @com.stockmanagement.inventory.application.annotation.Auditable(action = "LOGIN", resource = "AUTH")
    public AuthResponse login(LoginCommand command) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(command.username(), command.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(new Username(command.username()))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found")); // Should not happen if auth
                                                                                         // success

            user.loginSuccess();
            userRepository.save(user);

            String accessToken = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .username(user.getUsername().value())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                    .permissions(user.getRoles().stream()
                            .flatMap(r -> r.getPermissions().stream())
                            .map(Permission::getName)
                            .collect(Collectors.toSet()))
                    .build();

        } catch (AuthenticationException e) {
            userRepository.findByUsername(new Username(command.username()))
                    .ifPresent(u -> {
                        u.loginFailed();
                        userRepository.save(u);
                    });
            throw e;
        }
    }

    public AuthResponse refreshToken(
            com.stockmanagement.inventory.application.dto.command.RefreshTokenCommand command) {
        String refreshToken = command.refreshToken();
        String username = jwtTokenProvider.extractUsername(refreshToken);

        if (username != null && jwtTokenProvider.isTokenValid(refreshToken, username)) {
            User user = userRepository.findByUsername(new Username(username))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String newAccessToken = jwtTokenProvider.generateToken(user);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .username(user.getUsername().value())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                    .permissions(user.getRoles().stream()
                            .flatMap(r -> r.getPermissions().stream())
                            .map(Permission::getName)
                            .collect(Collectors.toSet()))
                    .build();
        } else {
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid Refresh Token");
        }
    }
}
