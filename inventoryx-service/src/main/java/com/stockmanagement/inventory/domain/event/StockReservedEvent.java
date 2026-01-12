package com.stockmanagement.inventory.domain.event;

import com.stockmanagement.inventory.domain.model.valueobject.ProductSKU;
import com.stockmanagement.inventory.domain.model.valueobject.Quantity;
import com.stockmanagement.inventory.domain.model.valueobject.StockId;

import java.time.Instant;
import java.util.UUID;

/**
 * StockReservedEvent - Stock was reserved for an order.
 * 
 * BUSINESS SCENARIO:
 * - Customer placed order
 * - Stock allocated to order
 * - Awaiting payment/confirmation
 * 
 * CONSUMERS:
 * - Order Management: Update order status
 * - Reporting: Track reservations
 * - Alerts: Low stock warnings
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record StockReservedEvent(
        String eventId,
        StockId stockId,
        ProductSKU sku,
        Quantity quantity,
        String orderId,
        Instant occurredOn) implements DomainEvent {

    /**
     * Constructor with auto-generated event ID.
     */
    public StockReservedEvent(
            StockId stockId,
            ProductSKU sku,
            Quantity quantity,
            String orderId,
            Instant occurredOn) {
        this(UUID.randomUUID().toString(), stockId, sku,
                quantity, orderId, occurredOn);
    }
}
