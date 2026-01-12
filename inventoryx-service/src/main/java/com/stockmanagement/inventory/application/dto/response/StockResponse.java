package com.stockmanagement.inventory.application.dto.response;

/**
 * StockResponse - Response DTO for Stock aggregate.
 * 
 * PURPOSE: Decouple API from domain model
 * 
 * FIELDS:
 * - id: Stock identifier
 * - sku: Product SKU
 * - locationId: Storage location
 * - availableQuantity: Quantity available for reservation/sale
 * - reservedQuantity: Quantity reserved for orders
 * - unitOfMeasure: Unit (PIECE, KILOGRAM, etc.)
 * - version: Optimistic locking version
 * - createdAt: Creation timestamp
 * - updatedAt: Last update timestamp
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record StockResponse(
        String id,
        String sku,
        String locationId,
        String availableQuantity,
        String reservedQuantity,
        String unitOfMeasure,
        Long version,
        String createdAt,
        String updatedAt) {
}
