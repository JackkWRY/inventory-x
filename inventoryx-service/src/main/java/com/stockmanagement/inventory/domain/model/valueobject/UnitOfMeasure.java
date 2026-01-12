package com.stockmanagement.inventory.domain.model.valueobject;

/**
 * UnitOfMeasure - Units for measuring stock quantities.
 * 
 * DDD PATTERN: Value Object (Enum)
 * - Fixed set of valid values
 * - Type-safe
 * - Domain-specific behavior
 * 
 * DESIGN DECISION: Enum vs Database Table
 * ✅ Enum: Type-safe, no DB roundtrip, better performance
 * ❌ Enum: Requires deployment to add new units
 * 
 * Trade-off acceptable because units rarely change.
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public enum UnitOfMeasure {

    /** Individual discrete items (laptops, phones) */
    PIECE("Piece", "pcs"),

    /** Packaged in boxes (bulk items) */
    BOX("Box", "box"),

    /** Larger packaging unit (wholesale) */
    CARTON("Carton", "ctn"),

    /** Weight-based (raw materials, food) */
    KILOGRAM("Kilogram", "kg"),

    /** Volume-based (liquids, beverages) */
    LITER("Liter", "L");

    private final String displayName;
    private final String abbreviation;

    UnitOfMeasure(String displayName, String abbreviation) {
        this.displayName = displayName;
        this.abbreviation = abbreviation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Checks if fractional quantities are allowed.
     * PIECE/BOX/CARTON: Whole numbers only
     * KILOGRAM/LITER: Fractional values allowed
     */
    public boolean allowsFractional() {
        return this == KILOGRAM || this == LITER;
    }

    /**
     * Checks if this is a discrete (countable) unit.
     */
    public boolean isDiscrete() {
        return this == PIECE || this == BOX || this == CARTON;
    }

    /**
     * Formats quantity with unit abbreviation.
     * EXAMPLE: formatQuantity("10.5") with KILOGRAM returns "10.5 kg"
     */
    public String formatQuantity(String quantity) {
        return quantity + " " + abbreviation;
    }
}
