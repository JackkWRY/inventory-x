package com.stockmanagement.inventory.infrastructure.persistence.entity;

import com.stockmanagement.inventory.domain.model.valueobject.UnitOfMeasure;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * StockEntity - JPA entity for Stock aggregate persistence.
 * 
 * CLEAN ARCHITECTURE: Infrastructure Layer
 * ========================================
 * This is a JPA entity (framework-specific).
 * Domain model (Stock) is kept clean and framework-independent.
 * 
 * MAPPING:
 * Domain: Stock ↔ JPA: StockEntity
 * 
 * RELATIONSHIPS:
 * - OneToMany with StockMovementEntity (cascade all, orphan removal)
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Entity
@Table(name = "stocks", schema = "inventory", uniqueConstraints = {
        @UniqueConstraint(name = "uk_stock_sku_location", columnNames = { "sku", "location_id" })
})
public class StockEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "sku", length = 20, nullable = false)
    private String sku;

    @Column(name = "location_id", length = 36, nullable = false)
    private String locationId;

    @Column(name = "product_id", length = 36)
    private String productId;

    @Column(name = "available_quantity", precision = 19, scale = 4, nullable = false)
    private BigDecimal availableQuantity;

    @Column(name = "reserved_quantity", precision = 19, scale = 4, nullable = false)
    private BigDecimal reservedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", length = 10, nullable = false)
    private UnitOfMeasure unitOfMeasure;

    /**
     * Optimistic locking version.
     * Automatically incremented by JPA on updates.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Stock movements (audit trail).
     * CASCADE ALL: Movements are deleted when stock is deleted.
     * ORPHAN REMOVAL: Movements removed from list are deleted.
     */
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StockMovementEntity> movements = new ArrayList<>();

    // Constructors

    public StockEntity() {
        // Required by JPA
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public BigDecimal getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(BigDecimal reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<StockMovementEntity> getMovements() {
        return movements;
    }

    public void setMovements(List<StockMovementEntity> movements) {
        this.movements = movements;
    }
}
