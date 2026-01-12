package com.stockmanagement.inventory.domain.repository;

import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.LocationId;
import com.stockmanagement.inventory.domain.model.valueobject.ProductSKU;
import com.stockmanagement.inventory.domain.model.valueobject.StockId;

import java.util.List;
import java.util.Optional;

/**
 * StockRepository - Repository interface for Stock aggregate.
 * 
 * DDD PATTERN: Repository (Port)
 * ==============================
 * Defines persistence operations for Stock aggregate.
 * This is a PORT in Clean Architecture - an interface owned by the domain.
 * 
 * CLEAN ARCHITECTURE: Dependency Inversion
 * ========================================
 * Domain Layer (this interface) ← Infrastructure Layer (implementation)
 * 
 * The domain defines WHAT it needs (this interface).
 * Infrastructure provides HOW (JpaStockRepository implementation).
 * 
 * AGGREGATE PATTERN:
 * =================
 * Repository works with aggregate roots only (Stock).
 * StockMovements are accessed through Stock, not directly.
 * 
 * IMPLEMENTATION:
 * ==============
 * Infrastructure layer will implement this using:
 * - JPA entities (StockEntity)
 * - Spring Data JPA repository
 * - Mapper (StockEntity ↔ Stock)
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public interface StockRepository {

    /**
     * Saves Stock aggregate (create or update).
     * 
     * BEHAVIOR:
     * - If stock.id doesn't exist: INSERT
     * - If stock.id exists: UPDATE
     * 
     * OPTIMISTIC LOCKING:
     * Version field prevents concurrent update conflicts.
     * 
     * @param stock Stock aggregate to save
     * @return Saved stock with updated version
     */
    Stock save(Stock stock);

    /**
     * Finds Stock by ID.
     * 
     * @param id Stock identifier
     * @return Optional containing stock if found, empty otherwise
     */
    Optional<Stock> findById(StockId id);

    /**
     * Finds Stock by SKU and location.
     * 
     * USE CASE: Check if stock exists before creating new record
     * BUSINESS RULE: Only one stock record per SKU+Location
     * 
     * @param sku        Product SKU
     * @param locationId Storage location
     * @return Optional containing stock if found, empty otherwise
     */
    Optional<Stock> findBySkuAndLocation(ProductSKU sku, LocationId locationId);

    /**
     * Finds all Stock records for a SKU (across all locations).
     * 
     * USE CASE: "Show me where product X is stored"
     * 
     * @param sku Product SKU
     * @return List of stock records (may be empty)
     */
    List<Stock> findBySku(ProductSKU sku);

    /**
     * Finds all Stock records at a location.
     * 
     * USE CASE: "Show me all products in warehouse A"
     * 
     * @param locationId Storage location
     * @return List of stock records (may be empty)
     */
    List<Stock> findByLocation(LocationId locationId);

    /**
     * Deletes Stock by ID.
     * 
     * USE CASE: Product discontinued, location closed
     * CASCADE: StockMovements are deleted automatically (FK constraint)
     * 
     * @param id Stock identifier
     */
    void delete(StockId id);

    /**
     * Checks if Stock exists for SKU and location.
     * 
     * USE CASE: Validation before creating new stock
     * PERFORMANCE: More efficient than findBySkuAndLocation when only checking
     * existence
     * 
     * @param sku        Product SKU
     * @param locationId Storage location
     * @return true if stock exists, false otherwise
     */
    boolean exists(ProductSKU sku, LocationId locationId);
}
