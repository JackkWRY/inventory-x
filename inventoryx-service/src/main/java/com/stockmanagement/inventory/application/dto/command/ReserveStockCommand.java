package com.stockmanagement.inventory.application.dto.command;

/**
 * ReserveStockCommand - Command to reserve stock for an order.
 * 
 * USE CASE: Customer places order, stock allocation
 * 
 * VALIDATION:
 * - SKU: Required
 * - LocationId: Required
 * - Quantity: Required, positive
 * - OrderId: Required
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record ReserveStockCommand(
        String sku,
        String locationId,
        String quantity,
        String orderId) {
}
