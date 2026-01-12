package com.stockmanagement.inventory.domain.exception;

/**
 * InsufficientStockException - Thrown when attempting to reserve more stock
 * than available.
 * 
 * DDD PATTERN: Domain Exception
 * =============================
 * Represents a business rule violation in the domain.
 * 
 * BUSINESS RULE:
 * Cannot reserve more than available quantity
 * 
 * WHEN THROWN:
 * - Stock.reserve() when requested > available
 * - Order placement with insufficient stock
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
