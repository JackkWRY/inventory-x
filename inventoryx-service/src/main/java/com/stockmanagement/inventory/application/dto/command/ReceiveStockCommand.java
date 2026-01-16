package com.stockmanagement.inventory.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * ReceiveStockCommand - Command to receive stock into warehouse.
 * 
 * USE CASE: Supplier delivery, stock transfer receipt, initial stock setup
 * 
 * VALIDATION:
 * - SKU: Required, 3-50 chars
 * - LocationId: Required
 * - Quantity: Required, positive number
 * - UnitOfMeasure: Required
 * - PerformedBy: Required
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record ReceiveStockCommand(
                @NotBlank(message = "SKU is required") @Size(min = 1, max = 50, message = "SKU must be 1-50 characters") String sku,

                @NotBlank(message = "Location ID is required") String locationId,

                @NotBlank(message = "Quantity is required") @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Quantity must be a positive number") String quantity,

                @NotBlank(message = "Unit of measure is required") String unitOfMeasure,

                String reason,

                @NotBlank(message = "Performed by is required") String performedBy) {
}
