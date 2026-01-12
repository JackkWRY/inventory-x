package com.stockmanagement.inventory.application.dto.response;

/**
 * StockMovementResponse - Response DTO for stock movement history.
 * 
 * PURPOSE: Audit trail and movement tracking
 * 
 * FIELDS:
 * - id: Movement identifier
 * - stockId: Parent stock ID
 * - movementType: Type (RECEIPT, RESERVATION, etc.)
 * - quantity: Amount moved
 * - reason: Explanation
 * - referenceId: Related entity (order ID, transfer ID)
 * - performedBy: Who performed the movement
 * - performedAt: When it occurred
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record StockMovementResponse(
        String id,
        String stockId,
        String movementType,
        String quantity,
        String reason,
        String referenceId,
        String performedBy,
        String performedAt) {
}
