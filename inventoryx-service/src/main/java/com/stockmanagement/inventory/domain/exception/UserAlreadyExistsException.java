package com.stockmanagement.inventory.domain.exception;

/**
 * UserAlreadyExistsException - Thrown when attempting to create a user that
 * already exists.
 * 
 * DDD PATTERN: Domain Exception
 * =============================
 * Represents business rule violation (Unique Constraint).
 * 
 * @author InventoryX Development Team
 * @since 2026-01-18
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
