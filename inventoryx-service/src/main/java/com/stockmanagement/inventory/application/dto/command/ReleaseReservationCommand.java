package com.stockmanagement.inventory.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * ReleaseReservationCommand - Command to release/cancel a reservation.
 * 
 * USE CASE: Order cancelled, payment failed, reservation timeout
 * 
 * VALIDATION:
 * - StockId: Required
 * - Quantity: Required, positive
 * - OrderId: Required
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record ReleaseReservationCommand(
                @NotBlank(message = "Stock ID is required") String stockId,

                @NotBlank(message = "Quantity is required") @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Quantity must be a positive number") String quantity,

                @NotBlank(message = "Order ID is required") String orderId) {
}
