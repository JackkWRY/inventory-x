package com.stockmanagement.inventory.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
                @NotBlank(message = "SKU is required") String sku,

                @NotBlank(message = "Location ID is required") String locationId,

                @NotBlank(message = "Quantity is required") @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Quantity must be a positive number") String quantity,

                @NotBlank(message = "Order ID is required") String orderId) {
}
