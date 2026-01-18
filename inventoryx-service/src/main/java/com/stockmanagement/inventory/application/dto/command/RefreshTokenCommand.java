package com.stockmanagement.inventory.application.dto.command;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenCommand(
        @NotBlank(message = "Refresh token is required") String refreshToken) {
}
