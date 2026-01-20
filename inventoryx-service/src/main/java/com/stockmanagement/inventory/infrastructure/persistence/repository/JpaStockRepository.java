package com.stockmanagement.inventory.infrastructure.persistence.repository;

import com.stockmanagement.inventory.infrastructure.persistence.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JpaStockRepository - Spring Data JPA repository interface.
 * 
 * SPRING DATA MAGIC:
 * =================
 * Spring Data automatically implements this interface.
 * Method names are converted to SQL queries.
 * 
 * NO IMPLEMENTATION NEEDED:
 * Spring generates implementation at runtime.
 * 
 * QUERY DERIVATION:
 * - findBy... → SELECT WHERE
 * - existsBy... → SELECT COUNT WHERE
 * - deleteBy... → DELETE WHERE
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Repository
public interface JpaStockRepository extends JpaRepository<StockEntity, String> {

    /**
     * Finds stock by SKU and location.
     * 
     * GENERATED SQL:
     * SELECT * FROM stocks WHERE sku = ? AND location_id = ?
     */
    Optional<StockEntity> findBySkuAndLocationId(String sku, String locationId);

    /**
     * Finds all stock for a SKU across all locations.
     * 
     * GENERATED SQL:
     * SELECT * FROM stocks WHERE sku = ?
     */
    List<StockEntity> findBySku(String sku);

    /**
     * Finds all stock at a location.
     * 
     * GENERATED SQL:
     * SELECT * FROM stocks WHERE location_id = ?
     */
    List<StockEntity> findByLocationId(String locationId);

    /**
     * Checks if stock exists for SKU and location.
     * 
     * GENERATED SQL:
     * SELECT COUNT(*) > 0 FROM stocks WHERE sku = ? AND location_id = ?
     */
    boolean existsBySkuAndLocationId(String sku, String locationId);

    /**
     * Counts stocks with available quantity less than threshold.
     */
    long countByAvailableQuantityLessThan(java.math.BigDecimal threshold);

    /**
     * Calculates total value of all stock (availableQuantity * product.price).
     * Uses Native Query because StockEntity has no direct ManyToOne to
     * ProductEntity,
     * only sku/productId reference.
     * 
     * Formula: SUM(stock.available_quantity * product.price)
     */
    @org.springframework.data.jpa.repository.Query(value = "SELECT COALESCE(SUM(s.available_quantity * p.price_amount), 0) "
            +
            "FROM inventory.stocks s " +
            "JOIN inventory.products p ON s.sku = p.sku", nativeQuery = true)
    java.math.BigDecimal calculateTotalStockValue();
}
