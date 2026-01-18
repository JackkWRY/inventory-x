package com.stockmanagement.inventory.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private Set<String> permissions;
}
