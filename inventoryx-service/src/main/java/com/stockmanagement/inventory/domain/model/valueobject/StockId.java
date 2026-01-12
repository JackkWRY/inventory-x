package com.stockmanagement.inventory.domain.model.valueobject;

import java.util.UUID;

/**
 * StockId - Unique identifier for Stock aggregate.
 * 
 * DDD PATTERN: Value Object (Identity)
 * - Immutable
 * - Self-validating
 * - UUID-based for global uniqueness
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record StockId(String value) {

    /**
     * Validates that ID is not null or empty.
     */
    public StockId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "StockId cannot be null or empty");
        }
    }

    /**
     * Generates new UUID-based StockId.
     */
    public static StockId generate() {
        return new StockId(UUID.randomUUID().toString());
    }

    /**
     * Creates StockId from existing value (for persistence reconstitution).
     */
    public static StockId of(String value) {
        return new StockId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
