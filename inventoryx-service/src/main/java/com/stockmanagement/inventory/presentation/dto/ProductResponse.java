package com.stockmanagement.inventory.presentation.dto;

import com.stockmanagement.inventory.domain.model.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        String category,
        BigDecimal price,
        String currency,
        String unitOfMeasure,
        Instant createdAt,
        Instant updatedAt) {
    public static ProductResponse fromDomain(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getCurrency(),
                product.getUnitOfMeasure(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
}
