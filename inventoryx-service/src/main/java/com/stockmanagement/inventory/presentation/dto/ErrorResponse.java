package com.stockmanagement.inventory.presentation.dto;

import java.time.Instant;

/**
 * ErrorResponse - Standard error response format.
 * 
 * PURPOSE: Consistent error format across all API endpoints
 * 
 * FIELDS:
 * - code: Error code (e.g., "STOCK_NOT_FOUND", "INSUFFICIENT_STOCK")
 * - message: Human-readable error message
 * - timestamp: When error occurred
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record ErrorResponse(
        String code,
        String message,
        Instant timestamp) {
}
