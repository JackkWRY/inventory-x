package com.stockmanagement.inventory.domain.event;

import com.stockmanagement.inventory.domain.model.valueobject.ProductSKU;
import com.stockmanagement.inventory.domain.model.valueobject.Quantity;
import com.stockmanagement.inventory.domain.model.valueobject.StockId;

import java.time.Instant;
import java.util.UUID;

/**
 * StockAdjustedEvent - Stock quantity was manually adjusted.
 * 
 * BUSINESS SCENARIO:
 * - Physical inventory count
 * - Damaged goods
 * - Theft or loss
 * - Data correction
 * 
 * CONSUMERS:
 * - Reporting: Track adjustments
 * - Audit: Investigate discrepancies
 * - Alerts: Significant adjustments
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public record StockAdjustedEvent(
        String eventId,
        StockId stockId,
        ProductSKU sku,
        Quantity difference,
        String reason,
        String performedBy,
        Instant occurredOn) implements DomainEvent {

    /**
     * Constructor with auto-generated event ID.
     */
    public StockAdjustedEvent(
            StockId stockId,
            ProductSKU sku,
            Quantity difference,
            String reason,
            String performedBy,
            Instant occurredOn) {
        this(UUID.randomUUID().toString(), stockId, sku,
                difference, reason, performedBy, occurredOn);
    }
}
