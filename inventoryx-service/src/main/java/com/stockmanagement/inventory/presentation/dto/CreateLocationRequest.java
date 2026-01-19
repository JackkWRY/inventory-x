package com.stockmanagement.inventory.presentation.dto;

import com.stockmanagement.inventory.domain.model.Location.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLocationRequest {
    @NotBlank(message = "Location name is required")
    private String name;

    @NotNull(message = "Location type is required")
    private LocationType type;

    private String description;
    private String address;
}
