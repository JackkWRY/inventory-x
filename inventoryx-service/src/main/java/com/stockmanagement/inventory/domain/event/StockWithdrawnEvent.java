package com.stockmanagement.inventory.domain.event;

import com.stockmanagement.inventory.domain.model.valueobject.*;
import java.time.Instant;
import java.util.UUID;

/**
 * StockWithdrawnEvent - Domain event when stock is withdrawn internally.
 * 
 * USE CASE: Internal requisition, material consumption, department usage
 * 
 * BUSINESS CONTEXT:
 * - Stock is immediately deducted from available quantity
 * - Used for internal operations (kitchen, maintenance, etc.)
 * - No reservation step needed
 * 
 * CONSUMERS:
 * - Reporting: Track internal usage
 * - Audit: Record all withdrawals
 * - Analytics: Department consumption patterns
 * 
 * @author InventoryX Development Team
 * @since 2026-01-16
 */
public record StockWithdrawnEvent(
        String eventId,
        StockId stockId,
        ProductSKU sku,
        LocationId locationId,
        Quantity quantity,
        String department,
        String reason,
        String performedBy,
        Instant occurredOn) implements DomainEvent {

    /**
     * Constructor with auto-generated event ID.
     */
    public StockWithdrawnEvent(
            StockId stockId,
            ProductSKU sku,
            LocationId locationId,
            Quantity quantity,
            String department,
            String reason,
            String performedBy,
            Instant occurredOn) {
        this(UUID.randomUUID().toString(), stockId, sku, locationId,
                quantity, department, reason, performedBy, occurredOn);
    }
}
