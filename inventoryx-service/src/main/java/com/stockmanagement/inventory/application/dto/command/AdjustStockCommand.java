package com.stockmanagement.inventory.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
                @NotBlank(message = "Stock ID is required") String stockId,

                @NotBlank(message = "New quantity is required") @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Quantity must be a non-negative number") String newQuantity,

                @NotBlank(message = "Reason is required for audit trail") String reason,

                @NotBlank(message = "Performed by is required") String performedBy) {
}
