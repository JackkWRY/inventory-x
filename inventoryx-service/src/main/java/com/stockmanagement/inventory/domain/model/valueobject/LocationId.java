package com.stockmanagement.inventory.domain.model.valueobject;

/**
 * LocationId - Warehouse location identifier.
 * 
 * DDD PATTERN: Value Object (Foreign Reference)
 * - References Warehouse Management bounded context
 * - Anti-Corruption Layer: Prevents coupling to warehouse internals
 * 
 * EXAMPLES: "WH-01", "WH-01-ZONE-A", "STORE-BKK-001"
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record LocationId(String value) {

    /**
     * Validates that location ID exists.
     * NOTE: Format not validated here (different location types have different
     * formats)
     * Referential integrity validated at application service layer.
     */
    public LocationId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "LocationId cannot be null or empty");
        }
    }

    /**
     * Creates LocationId from string.
     */
    public static LocationId of(String value) {
        return new LocationId(value);
    }

    /**
     * Case-sensitive comparison (WH-01 != wh-01).
     */
    public boolean matches(String other) {
        return other != null && this.value.equals(other);
    }

    @Override
    public String toString() {
        return value;
    }
}
