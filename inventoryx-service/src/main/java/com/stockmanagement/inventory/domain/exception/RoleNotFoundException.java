package com.stockmanagement.inventory.domain.exception;

/**
 * RoleNotFoundException - Thrown when role is not found.
 * 
 * DDD PATTERN: Domain Exception
 * =============================
 * Represents value object/entity not found scenario.
 * 
 * @author InventoryX Development Team
 * @since 2026-01-18
 */
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String message) {
        super(message);
    }
}
