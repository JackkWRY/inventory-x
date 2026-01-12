package com.stockmanagement.inventory.domain.exception;

/**
 * StockNotFoundException - Thrown when stock record is not found.
 * 
 * DDD PATTERN: Domain Exception
 * =============================
 * Represents entity not found scenario.
 * 
 * WHEN THROWN:
 * - Repository.findById() returns empty
 * - Attempting to operate on non-existent stock
 * - Invalid stock reference
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public class StockNotFoundException extends RuntimeException {

    public StockNotFoundException(String message) {
        super(message);
    }

    public StockNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
