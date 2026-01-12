package com.stockmanagement.inventory.application.dto.command;

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
        String stockId,
        String quantity,
        String orderId) {
}
