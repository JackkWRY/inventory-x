package com.stockmanagement.inventory.application.dto.command;

/**
 * ConfirmReservationCommand - Command to confirm reservation (complete sale).
 * 
 * USE CASE: Payment received, order confirmed, stock leaves inventory
 * 
 * VALIDATION:
 * - StockId: Required
 * - Quantity: Required, positive
 * - OrderId: Required
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record ConfirmReservationCommand(
        String stockId,
        String quantity,
        String orderId) {
}
