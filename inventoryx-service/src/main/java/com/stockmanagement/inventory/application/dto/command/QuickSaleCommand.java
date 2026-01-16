package com.stockmanagement.inventory.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
 * - quantity: Required, must be positive
 * - orderId: Required, POS receipt/invoice number
 * - performedBy: Required, who made the sale
 * 
 * @author InventoryX Development Team
 * @since 2026-01-16
 */
public record QuickSaleCommand(
        @NotBlank(message = "Stock ID is required") String stockId,

        @NotBlank(message = "Quantity is required") @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Quantity must be a positive number") String quantity,

        @NotBlank(message = "Order ID is required") String orderId,

        @NotBlank(message = "Performed by is required") String performedBy) {
}
