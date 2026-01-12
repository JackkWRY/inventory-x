package com.stockmanagement.inventory.domain.exception;

/**
 * InvalidStockOperationException - Thrown when stock operation violates
 * business rules.
 * 
 * DDD PATTERN: Domain Exception
 * =============================
 * Represents invalid business operations.
 * 
 * WHEN THROWN:
 * - Attempting to release more than reserved
 * - Invalid quantity (negative, null)
 * - Operation not allowed in current state
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public class InvalidStockOperationException extends RuntimeException {

    public InvalidStockOperationException(String message) {
        super(message);
    }

    public InvalidStockOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
