package com.stockmanagement.inventory.application.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        boolean isActive,
        boolean isLocked,
        Set<String> roles,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt) {
}
