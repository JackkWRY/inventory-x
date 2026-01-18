package com.stockmanagement.inventory.application.dto.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserCommand(
                @NotBlank(message = "First name is required") String firstName,

                @NotBlank(message = "Last name is required") String lastName,

                @Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email,

                String roleName // Optional: allows updating role
) {
}
