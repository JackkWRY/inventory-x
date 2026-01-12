package com.stockmanagement.inventory.domain.model.valueobject;

import java.util.regex.Pattern;

/**
 * ProductSKU - Stock Keeping Unit identifier.
 * 
 * DDD PATTERN: Value Object
 * - Immutable
 * - Format validation (3-20 chars, alphanumeric + hyphens)
 * - Case-insensitive (stored as uppercase)
 * 
 * EXAMPLES: "PROD-001", "SKU-12345", "LAPTOP-HP-001"
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record ProductSKU(String value) {

    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9-]{3,20}$");

    /**
     * Validates SKU format.
     * BUSINESS RULE: 3-20 chars, uppercase alphanumeric + hyphens only
     */
    public ProductSKU {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ProductSKU cannot be null or empty");
        }

        if (!SKU_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    String.format("Invalid SKU format: '%s'. " +
                            "Must be 3-20 characters (A-Z, 0-9, hyphen). " +
                            "Examples: 'PROD-001', 'SKU-12345'", value));
        }
    }

    /**
     * Creates ProductSKU from user input (normalizes to uppercase).
     */
    public static ProductSKU of(String value) {
        String normalized = value != null ? value.toUpperCase().trim() : null;
        return new ProductSKU(normalized);
    }

    /**
     * Case-insensitive comparison.
     */
    public boolean matches(String other) {
        return other != null && this.value.equalsIgnoreCase(other);
    }

    @Override
    public String toString() {
        return value;
    }
}
