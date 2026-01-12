package com.stockmanagement.inventory.domain.event;

import com.stockmanagement.inventory.domain.model.valueobject.ProductSKU;
import com.stockmanagement.inventory.domain.model.valueobject.Quantity;
import com.stockmanagement.inventory.domain.model.valueobject.StockId;

import java.time.Instant;
import java.util.UUID;

/**
 * ReservationReleasedEvent - Reservation was cancelled.
 * 
 * BUSINESS SCENARIO:
 * - Order cancelled
 * - Payment failed
 * - Reservation timeout
 * 
 * CONSUMERS:
 * - Order Management: Update order status
 * - Reporting: Track cancellations
 * - Alerts: Stock available again
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record ReservationReleasedEvent(
        String eventId,
        StockId stockId,
        ProductSKU sku,
        Quantity quantity,
        String orderId,
        Instant occurredOn) implements DomainEvent {

    /**
     * Constructor with auto-generated event ID.
     */
    public ReservationReleasedEvent(
            StockId stockId,
            ProductSKU sku,
            Quantity quantity,
            String orderId,
            Instant occurredOn) {
        this(UUID.randomUUID().toString(), stockId, sku,
                quantity, orderId, occurredOn);
    }
}
