package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.response.StockMovementResponse;
import com.stockmanagement.inventory.application.mapper.StockMovementMapper;
import com.stockmanagement.inventory.infrastructure.persistence.entity.StockMovementEntity;
import com.stockmanagement.inventory.infrastructure.persistence.repository.JpaStockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * StockMovementQueryService - Handles read-only stock movement queries.
 * 
 * CQRS Pattern: Query Service for READ operations only.
 * 
 * QUERIES:
 * - Get all movements for a stock (ordered by date desc)
 * 
 * READ-ONLY: No modifications, no events
 * 
 * @author InventoryX Development Team
 * @since 2026-01-15
 */
@Service
@Transactional(readOnly = true)
public class StockMovementQueryService {

    private final JpaStockMovementRepository movementRepository;
    private final StockMovementMapper mapper;

    public StockMovementQueryService(
            JpaStockMovementRepository movementRepository,
            StockMovementMapper mapper) {
        this.movementRepository = movementRepository;
        this.mapper = mapper;
    }

    /**
     * Gets all movements for a stock, ordered by date descending.
     * 
     * USE CASE: View movement history on Stock Detail page
     * 
     * @param stockId Stock ID
     * @return List of movements (newest first)
     */
    public List<StockMovementResponse> getMovementsByStockId(String stockId) {
        List<StockMovementEntity> movements = movementRepository
                .findByStock_IdOrderByPerformedAtDesc(stockId);
        return mapper.toResponseList(movements);
    }
}
