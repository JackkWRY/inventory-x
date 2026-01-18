package com.stockmanagement.inventory.domain.exception;

/**
 * UserNotFoundException - Thrown when user record is not found.
 * 
 * DDD PATTERN: Domain Exception
 * =============================
 * Represents entity not found scenario.
 * 
 * @author InventoryX Development Team
 * @since 2026-01-18
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
