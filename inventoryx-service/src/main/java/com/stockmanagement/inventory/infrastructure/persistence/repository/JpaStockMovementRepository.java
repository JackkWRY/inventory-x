package com.stockmanagement.inventory.infrastructure.persistence.repository;

import com.stockmanagement.inventory.infrastructure.persistence.entity.StockMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JpaStockMovementRepository - Spring Data JPA repository for stock movements.
 * 
 * SPRING DATA MAGIC:
 * =================
 * Spring Data automatically implements this interface.
 * Method names are converted to SQL queries.
 * 
 * QUERY DERIVATION:
 * - findBy... → SELECT WHERE
 * - OrderBy...Desc → ORDER BY ... DESC
 * 
 * @author InventoryX Development Team
 * @since 2026-01-15
 */
@Repository
public interface JpaStockMovementRepository extends JpaRepository<StockMovementEntity, String> {

    /**
     * Finds all movements for a stock, ordered by date descending (newest first).
     * 
     * GENERATED SQL:
     * SELECT * FROM stock_movements WHERE stock_id = ? ORDER BY performed_at DESC
     * 
     * @param stockId Stock ID
     * @return List of movements (newest first)
     */
    List<StockMovementEntity> findByStock_IdOrderByPerformedAtDesc(String stockId);

    /**
     * Finds all movements for a stock (no ordering).
     * 
     * @param stockId Stock ID
     * @return List of movements
     */
    List<StockMovementEntity> findByStock_Id(String stockId);
}
