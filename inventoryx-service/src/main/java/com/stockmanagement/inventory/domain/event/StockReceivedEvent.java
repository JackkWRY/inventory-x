package com.stockmanagement.inventory.domain.event;

import com.stockmanagement.inventory.domain.model.valueobject.LocationId;
import com.stockmanagement.inventory.domain.model.valueobject.ProductSKU;
import com.stockmanagement.inventory.domain.model.valueobject.Quantity;
import com.stockmanagement.inventory.domain.model.valueobject.StockId;

import java.time.Instant;
import java.util.UUID;

/**
 * StockReceivedEvent - Stock was received into warehouse.
 * 
 * BUSINESS SCENARIO:
 * - Supplier delivery accepted
 * - Stock transfer received
 * - Initial stock setup
 * 
 * CONSUMERS:
 * - Accounting: Update inventory value
 * - Reporting: Track stock movements
 * - Alerts: Check reorder points
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record StockReceivedEvent(
        String eventId,
        StockId stockId,
        ProductSKU sku,
        LocationId locationId,
        Quantity quantity,
        String reason,
        String performedBy,
        Instant occurredOn) implements DomainEvent {

    /**
     * Constructor with auto-generated event ID.
     */
    public StockReceivedEvent(
            StockId stockId,
            ProductSKU sku,
            LocationId locationId,
            Quantity quantity,
            String reason,
            String performedBy,
            Instant occurredOn) {
        this(UUID.randomUUID().toString(), stockId, sku, locationId,
                quantity, reason, performedBy, occurredOn);
    }
}
