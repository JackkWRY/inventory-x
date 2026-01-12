package com.stockmanagement.inventory.domain.event;

import com.stockmanagement.inventory.domain.model.valueobject.ProductSKU;
import com.stockmanagement.inventory.domain.model.valueobject.Quantity;
import com.stockmanagement.inventory.domain.model.valueobject.StockId;

import java.time.Instant;
import java.util.UUID;

/**
 * ReservationConfirmedEvent - Reservation was confirmed (stock sold).
 * 
 * BUSINESS SCENARIO:
 * - Payment received
 * - Order confirmed
 * - Stock leaves inventory
 * 
 * CONSUMERS:
 * - Order Management: Proceed with fulfillment
 * - Accounting: Record sale
 * - Reporting: Sales analytics
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record ReservationConfirmedEvent(
        String eventId,
        StockId stockId,
        ProductSKU sku,
        Quantity quantity,
        String orderId,
        Instant occurredOn) implements DomainEvent {

    /**
     * Constructor with auto-generated event ID.
     */
    public ReservationConfirmedEvent(
            StockId stockId,
            ProductSKU sku,
            Quantity quantity,
            String orderId,
            Instant occurredOn) {
        this(UUID.randomUUID().toString(), stockId, sku,
                quantity, orderId, occurredOn);
    }
}
