package com.stockmanagement.inventory.application.dto.command;

/**
 * WithdrawStockCommand - Command to withdraw stock for internal use.
 * 
 * USE CASE: Internal requisition, department usage, material consumption
 * 
 * VALIDATION:
 * - stockId: Required, must exist
 * - quantity: Required, must be positive, cannot exceed available
 * - department: Required, identifies requesting department
 * - reason: Required, for audit trail
 * - performedBy: Required, who performed the action
 * 
 * @author InventoryX Development Team
 * @since 2026-01-16
 */
public record WithdrawStockCommand(
        String stockId,
        String quantity,
        String department,
        String reason,
        String performedBy) {
    // Compact constructor for validation
    public WithdrawStockCommand {
        if (stockId == null || stockId.isBlank()) {
            throw new IllegalArgumentException("Stock ID is required");
        }
        if (quantity == null || quantity.isBlank()) {
            throw new IllegalArgumentException("Quantity is required");
        }
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department is required");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required");
        }
        if (performedBy == null || performedBy.isBlank()) {
            throw new IllegalArgumentException("Performed by is required");
        }
    }
}
