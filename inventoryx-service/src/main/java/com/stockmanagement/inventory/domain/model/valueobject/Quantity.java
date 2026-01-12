package com.stockmanagement.inventory.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Quantity - Numeric quantity with business rules.
 * 
 * DDD PATTERN: Value Object
 * - Immutable
 * - Non-negative (>= 0)
 * - BigDecimal for precision (4 decimal places)
 * - Domain operations (add, subtract, compare)
 * 
 * WHY NOT PRIMITIVE?
 * - Prevents precision errors (0.1 + 0.2 != 0.3 in double)
 * - Enforces business rules (no negative quantities)
 * - Type-safe and expressive
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record Quantity(BigDecimal value) {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Validates and normalizes quantity.
     * BUSINESS RULE: Must be >= 0
     */
    public Quantity {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Quantity cannot be null. Use Quantity.zero() for zero.");
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    String.format("Quantity cannot be negative: %s", value));
        }

        // Normalize scale for consistent comparisons
        value = value.setScale(SCALE, ROUNDING_MODE);
    }

    // Factory Methods

    public static Quantity of(String value) {
        return new Quantity(new BigDecimal(value));
    }

    public static Quantity of(long value) {
        return new Quantity(BigDecimal.valueOf(value));
    }

    public static Quantity zero() {
        return new Quantity(BigDecimal.ZERO);
    }

    // Arithmetic Operations

    /**
     * Adds quantities (immutable).
     * USE CASE: Receiving stock, releasing reservation
     */
    public Quantity add(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add null quantity");
        }
        return new Quantity(this.value.add(other.value));
    }

    /**
     * Subtracts quantities (immutable).
     * USE CASE: Reserving stock, confirming sale
     * BUSINESS RULE: Result must be >= 0
     */
    public Quantity subtract(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot subtract null quantity");
        }

        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    String.format("Cannot subtract %s from %s: result would be negative",
                            other.value, this.value));
        }

        return new Quantity(result);
    }

    /**
     * Multiplies by factor.
     * USE CASE: Unit conversion, price calculation
     */
    public Quantity multiply(BigDecimal factor) {
        if (factor == null) {
            throw new IllegalArgumentException("Cannot multiply by null factor");
        }
        return new Quantity(this.value.multiply(factor));
    }

    // Comparison Operations

    public boolean isGreaterThan(Quantity other) {
        return other != null && this.value.compareTo(other.value) > 0;
    }

    public boolean isGreaterThanOrEqual(Quantity other) {
        return other != null && this.value.compareTo(other.value) >= 0;
    }

    public boolean isZero() {
        return this.value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.value.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
