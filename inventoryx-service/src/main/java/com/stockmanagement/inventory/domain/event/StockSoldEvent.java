package com.stockmanagement.inventory.domain.event;

import com.stockmanagement.inventory.domain.model.valueobject.*;
import java.time.Instant;
import java.util.UUID;

/**
 * StockSoldEvent - Domain event when stock is sold directly (POS/Walk-in).
 * 
 * USE CASE: Point of Sale, walk-in customers, immediate sales
 * 
 * BUSINESS CONTEXT:
 * - Quick sale = Reserve + Confirm in one step
 * - Stock is immediately deducted from available quantity
 * - Simplifies retail/POS operations
 * 
 * CONSUMERS:
 * - Accounting: Record sales
 * - Reporting: Sales analytics
 * - POS Integration: Receipt generation
 * 
 * @author InventoryX Development Team
 * @since 2026-01-16
 */
public record StockSoldEvent(
        String eventId,
        StockId stockId,
        ProductSKU sku,
        LocationId locationId,
        Quantity quantity,
        String orderId,
        String performedBy,
        Instant occurredOn) implements DomainEvent {

    /**
     * Constructor with auto-generated event ID.
     */
    public StockSoldEvent(
            StockId stockId,
            ProductSKU sku,
            LocationId locationId,
            Quantity quantity,
            String orderId,
            String performedBy,
            Instant occurredOn) {
        this(UUID.randomUUID().toString(), stockId, sku, locationId,
                quantity, orderId, performedBy, occurredOn);
    }
}
