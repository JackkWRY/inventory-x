package com.stockmanagement.inventory.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * StockMovementEntity - JPA entity for stock movement audit trail.
 * 
 * CLEAN ARCHITECTURE: Infrastructure Layer
 * ========================================
 * Persistence entity for stock movement history.
 * 
 * RELATIONSHIP:
 * ManyToOne with StockEntity (parent aggregate)
 * 
 * IMMUTABILITY:
 * In domain, movements are immutable.
 * JPA requires setters, but movements should not be updated after creation.
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Entity
@Table(name = "stock_movements", schema = "inventory")
public class StockMovementEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    /**
     * Parent stock aggregate.
     * LAZY fetch for performance (don't load stock when querying movements).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private StockEntity stock;

    @Column(name = "movement_type", length = 20, nullable = false)
    private String movementType;

    @Column(name = "quantity", precision = 19, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "performed_by", length = 36)
    private String performedBy;

    @Column(name = "performed_at", nullable = false)
    private Instant performedAt;

    // Constructors

    public StockMovementEntity() {
        // Required by JPA
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StockEntity getStock() {
        return stock;
    }

    public void setStock(StockEntity stock) {
        this.stock = stock;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public Instant getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(Instant performedAt) {
        this.performedAt = performedAt;
    }
}
