package com.stockmanagement.inventory.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * WithdrawStockCommand - Command to withdraw stock for internal use.
 * 
 * USE CASE: Internal requisition, department usage, material consumption
 * 
 * VALIDATION:
 * - stockId: Required, must exist
 * - quantity: Required, must be positive
 * - department: Required, identifies requesting department
 * - reason: Required, for audit trail
 * - performedBy: Required, who performed the action
 * 
 * @author InventoryX Development Team
 * @since 2026-01-16
 */
public record WithdrawStockCommand(
        @NotBlank(message = "Stock ID is required") String stockId,

        @NotBlank(message = "Quantity is required") @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Quantity must be a positive number") String quantity,

        @NotBlank(message = "Department is required") String department,

        @NotBlank(message = "Reason is required for audit trail") String reason,

        @NotBlank(message = "Performed by is required") String performedBy) {
}
