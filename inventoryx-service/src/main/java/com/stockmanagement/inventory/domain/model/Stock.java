package com.stockmanagement.inventory.domain.model;

import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.event.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stock - Aggregate Root for Inventory Management.
 * 
 * DOMAIN-DRIVEN DESIGN: Aggregate Root
 * ====================================
 * Entry point to the Stock aggregate. Enforces business invariants and
 * coordinates
 * all changes to the aggregate.
 * 
 * BUSINESS INVARIANTS:
 * - availableQuantity >= 0
 * - reservedQuantity >= 0
 * - Cannot reserve more than available
 * - Cannot release more than reserved
 * 
 * CLEAN ARCHITECTURE:
 * Domain layer - no dependencies on frameworks, database, or UI.
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public class Stock {

    // Identity & Business Attributes
    private final StockId id;
    private final String productId;
    private final ProductSKU sku;
    private final LocationId locationId;
    private Quantity availableQuantity;
    private Quantity reservedQuantity;
    private final UnitOfMeasure unitOfMeasure;

    // Optimistic Locking & Audit
    private Long version;
    private final Instant createdAt;
    private Instant updatedAt;

    // Domain Events
    private final List<Object> domainEvents = new ArrayList<>();

    // ========================================================================
    // CONSTRUCTORS & FACTORY METHODS
    // ========================================================================

    /**
     * Private constructor - use factory methods instead.
     */
    private Stock(StockId id, String productId, ProductSKU sku, LocationId locationId,
            Quantity availableQuantity, Quantity reservedQuantity,
            UnitOfMeasure unitOfMeasure, Long version,
            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.productId = productId;
        this.sku = sku;
        this.locationId = locationId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.unitOfMeasure = unitOfMeasure;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates new Stock with zero quantities.
     * Used when first receiving a product at a location.
     */
    public static Stock create(String productId, ProductSKU sku, LocationId locationId, UnitOfMeasure unitOfMeasure) {
        Instant now = Instant.now();
        return new Stock(StockId.generate(), productId, sku, locationId,
                Quantity.zero(), Quantity.zero(), unitOfMeasure,
                null, now, now); // version = null for new entities
    }

    /**
     * Reconstitutes Stock from persistence.
     * Used by Infrastructure layer to rebuild aggregate from database.
     */
    public static Stock reconstitute(StockId id, String productId, ProductSKU sku, LocationId locationId,
            Quantity availableQuantity, Quantity reservedQuantity,
            UnitOfMeasure unitOfMeasure, Long version,
            Instant createdAt, Instant updatedAt) {
        return new Stock(id, productId, sku, locationId, availableQuantity, reservedQuantity,
                unitOfMeasure, version, createdAt, updatedAt);
    }

    // ========================================================================
    // GETTERS
    // ========================================================================

    public StockId getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public ProductSKU getSku() {
        return sku;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    public Quantity getAvailableQuantity() {
        return availableQuantity;
    }

    public Quantity getReservedQuantity() {
        return reservedQuantity;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Returns unmodifiable list of domain events.
     */
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clears domain events after publishing.
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }

    // ========================================================================
    // BUSINESS METHODS
    // ========================================================================

    /**
     * Receives stock into warehouse.
     * 
     * BUSINESS RULE: Quantity must be positive
     * SIDE EFFECTS: Increases availableQuantity, publishes StockReceivedEvent
     * 
     * @param quantity    Amount received
     * @param reason      Reason for receipt (audit trail)
     * @param performedBy Who received the stock
     * @throws IllegalArgumentException if quantity is not positive
     */
    public void receiveStock(Quantity quantity, String reason, String performedBy) {
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException(
                    "Cannot receive stock: quantity must be positive. Received: " + quantity);
        }

        this.availableQuantity = this.availableQuantity.add(quantity);
        this.updatedAt = Instant.now();

        registerEvent(new StockReceivedEvent(id, sku, locationId, quantity,
                reason, performedBy, Instant.now()));
    }

    /**
     * Reserves stock for an order.
     * 
     * BUSINESS RULE: Cannot reserve more than available
     * SIDE EFFECTS: Decreases availableQuantity, increases reservedQuantity
     * 
     * @param quantity Amount to reserve
     * @param orderId  Order reference
     * @throws IllegalArgumentException if insufficient stock
     */
    public void reserve(Quantity quantity, String orderId) {
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (!this.availableQuantity.isGreaterThanOrEqual(quantity)) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock. Available: %s, Requested: %s",
                            availableQuantity, quantity));
        }

        this.availableQuantity = this.availableQuantity.subtract(quantity);
        this.reservedQuantity = this.reservedQuantity.add(quantity);
        this.updatedAt = Instant.now();

        registerEvent(new StockReservedEvent(id, sku, quantity, orderId, Instant.now()));
    }

    /**
     * Releases reservation (order cancelled).
     * 
     * BUSINESS RULE: Cannot release more than reserved
     * SIDE EFFECTS: Increases availableQuantity, decreases reservedQuantity
     * 
     * @param quantity Amount to release
     * @param orderId  Order reference
     * @throws IllegalArgumentException if insufficient reserved quantity
     */
    public void releaseReservation(Quantity quantity, String orderId) {
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (!this.reservedQuantity.isGreaterThanOrEqual(quantity)) {
            throw new IllegalArgumentException(
                    String.format("Cannot release more than reserved. Reserved: %s, Requested: %s",
                            reservedQuantity, quantity));
        }

        this.reservedQuantity = this.reservedQuantity.subtract(quantity);
        this.availableQuantity = this.availableQuantity.add(quantity);
        this.updatedAt = Instant.now();

        registerEvent(new ReservationReleasedEvent(id, sku, quantity, orderId, Instant.now()));
    }

    /**
     * Confirms reservation (order completed, stock sold).
     * 
     * BUSINESS RULE: Cannot confirm more than reserved
     * SIDE EFFECTS: Decreases reservedQuantity (stock leaves inventory)
     * 
     * @param quantity Amount to confirm
     * @param orderId  Order reference
     * @throws IllegalArgumentException if insufficient reserved quantity
     */
    public void confirmReservation(Quantity quantity, String orderId) {
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (!this.reservedQuantity.isGreaterThanOrEqual(quantity)) {
            throw new IllegalArgumentException(
                    String.format("Cannot confirm more than reserved. Reserved: %s, Requested: %s",
                            reservedQuantity, quantity));
        }

        this.reservedQuantity = this.reservedQuantity.subtract(quantity);
        this.updatedAt = Instant.now();

        registerEvent(new ReservationConfirmedEvent(id, sku, quantity, orderId, Instant.now()));
    }

    /**
     * Adjusts stock quantity (manual correction).
     * 
     * USE CASES: Physical inventory count, damaged goods, theft
     * SIDE EFFECTS: Sets availableQuantity to new value
     * 
     * @param newQuantity New quantity after adjustment
     * @param reason      Reason for adjustment (required for audit)
     * @param performedBy Who performed the adjustment
     */
    public void adjustStock(Quantity newQuantity, String reason, String performedBy) {
        if (newQuantity == null) {
            throw new IllegalArgumentException("New quantity cannot be null");
        }

        Quantity difference = newQuantity.subtract(this.availableQuantity);
        this.availableQuantity = newQuantity;
        this.updatedAt = Instant.now();

        registerEvent(new StockAdjustedEvent(id, sku, difference, reason,
                performedBy, Instant.now()));
    }

    /**
     * Registers domain event for publishing.
     */
    private void registerEvent(Object event) {
        this.domainEvents.add(event);
    }

    // ========================================================================
    // DIRECT DEDUCTION METHODS (NO RESERVATION)
    // ========================================================================

    /**
     * Withdraws stock for internal use.
     * 
     * USE CASE: Department requisition, material consumption, internal usage
     * BUSINESS RULE: Cannot withdraw more than available
     * SIDE EFFECTS: Decreases availableQuantity immediately
     * 
     * @param quantity    Amount to withdraw
     * @param department  Requesting department
     * @param reason      Reason for withdrawal (audit trail)
     * @param performedBy Who withdrew the stock
     * @throws IllegalArgumentException if insufficient stock
     */
    public void withdraw(Quantity quantity, String department, String reason, String performedBy) {
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (!this.availableQuantity.isGreaterThanOrEqual(quantity)) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock for withdrawal. Available: %s, Requested: %s",
                            availableQuantity, quantity));
        }

        this.availableQuantity = this.availableQuantity.subtract(quantity);
        this.updatedAt = Instant.now();

        registerEvent(new StockWithdrawnEvent(id, sku, locationId, quantity,
                department, reason, performedBy, Instant.now()));
    }

    /**
     * Sells stock directly (POS/Walk-in).
     * 
     * USE CASE: Point of Sale, retail counter, immediate sales
     * BUSINESS RULE: Combines reserve + confirm for efficiency
     * SIDE EFFECTS: Decreases availableQuantity immediately (stock leaves
     * inventory)
     * 
     * @param quantity    Amount to sell
     * @param orderId     POS receipt/invoice number
     * @param performedBy Who made the sale
     * @throws IllegalArgumentException if insufficient stock
     */
    public void quickSale(Quantity quantity, String orderId, String performedBy) {
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (!this.availableQuantity.isGreaterThanOrEqual(quantity)) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock for sale. Available: %s, Requested: %s",
                            availableQuantity, quantity));
        }

        this.availableQuantity = this.availableQuantity.subtract(quantity);
        this.updatedAt = Instant.now();

        registerEvent(new StockSoldEvent(id, sku, locationId, quantity,
                orderId, performedBy, Instant.now()));
    }
}
