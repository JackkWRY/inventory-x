package com.stockmanagement.inventory.presentation.dto;

import com.stockmanagement.inventory.domain.model.Location.LocationType;
import com.stockmanagement.inventory.domain.model.Location.LocationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLocationRequest {
    @NotBlank(message = "Location name is required")
    private String name;

    private LocationType type;
    private String description;
    private String address;
    private LocationStatus status;
}
