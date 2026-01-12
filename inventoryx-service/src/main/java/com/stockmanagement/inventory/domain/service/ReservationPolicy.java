package com.stockmanagement.inventory.domain.service;

import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.Quantity;

/**
 * ReservationPolicy - Domain service for stock reservation business rules.
 * 
 * DDD PATTERN: Domain Service
 * ===========================
 * Domain services contain business logic that:
 * - Doesn't naturally fit in a single entity/value object
 * - Operates on multiple domain objects
 * - Represents a business operation or policy
 * 
 * WHY NOT IN STOCK AGGREGATE?
 * ===========================
 * While Stock.reserve() enforces basic rules (quantity >= 0),
 * this service provides additional business policies that might:
 * - Check multiple stocks
 * - Apply complex business rules
 * - Coordinate between aggregates
 * 
 * STATELESS:
 * =========
 * Domain services are stateless - they operate on objects passed to them.
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public class ReservationPolicy {

    /**
     * Checks if stock can be reserved for requested quantity.
     * 
     * BUSINESS RULE:
     * Available quantity must be >= requested quantity
     * 
     * USE CASE:
     * Pre-validation before attempting reservation
     * 
     * @param stock             Stock to check
     * @param requestedQuantity Quantity to reserve
     * @return true if reservation is possible, false otherwise
     */
    public boolean canReserve(Stock stock, Quantity requestedQuantity) {
        if (stock == null || requestedQuantity == null) {
            return false;
        }

        return stock.getAvailableQuantity()
                .isGreaterThanOrEqual(requestedQuantity);
    }

    /**
     * Calculates maximum quantity that can be reserved.
     * 
     * USE CASE:
     * "How much of this product can I order?"
     * Partial fulfillment scenarios
     * 
     * @param stock Stock to check
     * @return Maximum reservable quantity (equals available quantity)
     */
    public Quantity calculateReservableQuantity(Stock stock) {
        if (stock == null) {
            return Quantity.zero();
        }

        return stock.getAvailableQuantity();
    }

    /**
     * Checks if stock level is below minimum threshold.
     * 
     * BUSINESS RULE:
     * Alert when available quantity < minimum threshold
     * 
     * USE CASE:
     * Low stock alerts, reorder triggers
     * 
     * @param stock            Stock to check
     * @param minimumThreshold Minimum acceptable quantity
     * @return true if stock is low, false otherwise
     */
    public boolean isLowStock(Stock stock, Quantity minimumThreshold) {
        if (stock == null || minimumThreshold == null) {
            return false;
        }

        return minimumThreshold.isGreaterThan(stock.getAvailableQuantity());
    }
}
