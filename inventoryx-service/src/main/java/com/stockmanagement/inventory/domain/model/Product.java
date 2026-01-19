package com.stockmanagement.inventory.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Product - Aggregate Root for Master Data.
 * 
 * Represents a catalog item that can be stocked.
 * 
 * INVARIANTS:
 * - SKU must be unique (enforced by Repo/DB)
 * - Price cannot be negative
 */
public class Product {

    private final UUID id;
    private String sku;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private String currency; // ISO 4217
    private String unitOfMeasure; // Stored as String for now, could be Enum later

    private Long version;
    private final Instant createdAt;
    private Instant updatedAt;

    // ========================================================================
    // CONSTRUCTORS
    // ========================================================================

    private Product(UUID id, String sku, String name, String description,
            String category, BigDecimal price, String currency, String unitOfMeasure,
            Long version, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.currency = currency;
        this.unitOfMeasure = unitOfMeasure;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method to create a NEW Product.
     */
    public static Product create(String sku, String name, String description,
            String category, BigDecimal price, String currency, String unitOfMeasure) {
        validatePrice(price);

        Instant now = Instant.now();
        return new Product(
                UUID.randomUUID(),
                sku,
                name,
                description,
                category,
                price,
                currency != null ? currency : "USD",
                unitOfMeasure != null ? unitOfMeasure : "PIECE",
                null, // version
                now,
                now);
    }

    /**
     * Reconstitutes from Persistence.
     */
    public static Product reconstitute(UUID id, String sku, String name, String description,
            String category, BigDecimal price, String currency, String unitOfMeasure,
            Long version, Instant createdAt, Instant updatedAt) {
        return new Product(id, sku, name, description, category, price, currency, unitOfMeasure, version, createdAt,
                updatedAt);
    }

    // ========================================================================
    // BUSINESS LOGIC
    // ========================================================================

    public void updateDetails(String name, String description, String category, String unitOfMeasure) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        this.name = name;
        this.description = description;
        this.category = category;
        if (unitOfMeasure != null) {
            this.unitOfMeasure = unitOfMeasure;
        }
        this.updatedAt = Instant.now();
    }

    public void updatePrice(BigDecimal newPrice, String currency) {
        validatePrice(newPrice);
        this.price = newPrice;
        if (currency != null) {
            this.currency = currency;
        }
        this.updatedAt = Instant.now();
    }

    private static void validatePrice(BigDecimal price) {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    // ========================================================================
    // GETTERS
    // ========================================================================

    public UUID getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getUnitOfMeasure() {
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
}
