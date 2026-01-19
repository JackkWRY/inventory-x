package com.stockmanagement.inventory.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank(message = "Name is required") String name,

        String description,
        String category,

        @DecimalMin(value = "0.0", message = "Price must be positive") BigDecimal price,

        String currency,
        String unitOfMeasure) {
}
