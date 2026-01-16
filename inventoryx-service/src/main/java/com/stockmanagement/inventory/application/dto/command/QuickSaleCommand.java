package com.stockmanagement.inventory.application.dto.command;

/**
 * QuickSaleCommand - Command for immediate POS/Walk-in sales.
 * 
 * USE CASE: Point of Sale, retail counter, walk-in customers
 * 
 * BUSINESS LOGIC:
 * Combines Reserve + Confirm into single operation for efficiency.
 * Stock is immediately deducted from available quantity.
 * 
 * VALIDATION:
 * - stockId: Required, must exist
 * - quantity: Required, must be positive, cannot exceed available
 * - orderId: Required, POS receipt/invoice number
 * - performedBy: Required, who made the sale
 * 
 * @author InventoryX Development Team
 * @since 2026-01-16
 */
public record QuickSaleCommand(
        String stockId,
        String quantity,
        String orderId,
        String performedBy) {
    // Compact constructor for validation
    public QuickSaleCommand {
        if (stockId == null || stockId.isBlank()) {
            throw new IllegalArgumentException("Stock ID is required");
        }
        if (quantity == null || quantity.isBlank()) {
            throw new IllegalArgumentException("Quantity is required");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }
        if (performedBy == null || performedBy.isBlank()) {
            throw new IllegalArgumentException("Performed by is required");
        }
    }
}
