package com.stockmanagement.inventory.application.dto.command;

/**
 * ReceiveStockCommand - Command to receive stock into warehouse.
 * 
 * USE CASE: Supplier delivery, stock transfer receipt, initial stock setup
 * 
 * VALIDATION:
 * - SKU: Required, 3-20 chars, alphanumeric + hyphens
 * - LocationId: Required
 * - Quantity: Required, positive number
 * - UnitOfMeasure: Required, valid enum value
 * - PerformedBy: Required
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record ReceiveStockCommand(
        String sku,
        String locationId,
        String quantity,
        String unitOfMeasure,
        String reason,
        String performedBy) {
}
