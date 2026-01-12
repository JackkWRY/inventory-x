package com.stockmanagement.inventory.application.dto.command;

/**
 * AdjustStockCommand - Command to manually adjust stock quantity.
 * 
 * USE CASE: Physical inventory count, damaged goods, theft, data correction
 * 
 * VALIDATION:
 * - StockId: Required
 * - NewQuantity: Required, non-negative
 * - Reason: Required (audit trail)
 * - PerformedBy: Required
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record AdjustStockCommand(
        String stockId,
        String newQuantity,
        String reason,
        String performedBy) {
}
