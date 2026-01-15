package com.stockmanagement.inventory.application.mapper;

import com.stockmanagement.inventory.application.dto.response.StockMovementResponse;
import com.stockmanagement.inventory.infrastructure.persistence.entity.StockMovementEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * StockMovementMapper - Maps between StockMovementEntity and
 * StockMovementResponse.
 * 
 * CLEAN ARCHITECTURE: Application Layer
 * =====================================
 * Transforms infrastructure entities to application DTOs.
 * 
 * MAPPING STRATEGY:
 * - Entity → Response (for queries)
 * - Quantity sign indicates direction (+receipt, -reservation)
 * 
 * @author InventoryX Development Team
 * @since 2026-01-15
 */
@Component
public class StockMovementMapper {

    /**
     * Maps entity to response.
     * 
     * @param entity StockMovementEntity from database
     * @return StockMovementResponse for API
     */
    public StockMovementResponse toResponse(StockMovementEntity entity) {
        return new StockMovementResponse(
                entity.getId(),
                entity.getStock().getId(),
                entity.getMovementType(),
                formatQuantity(entity),
                entity.getReason(),
                entity.getReferenceId(),
                entity.getPerformedBy(),
                entity.getPerformedAt().toString());
    }

    /**
     * Maps list of entities to responses.
     * 
     * @param entities List of StockMovementEntity
     * @return List of StockMovementResponse
     */
    public List<StockMovementResponse> toResponseList(List<StockMovementEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Formats quantity with sign prefix for display.
     * 
     * RECEIPT/RELEASE: positive (+50.00)
     * RESERVATION/CONFIRMATION/SALE/ADJUSTMENT: depends on actual value
     */
    private String formatQuantity(StockMovementEntity entity) {
        var quantity = entity.getQuantity();
        var type = entity.getMovementType();

        // RECEIPT and RELEASE add stock (positive display)
        if ("RECEIPT".equals(type) || "RELEASE".equals(type)) {
            return "+" + quantity.toPlainString();
        }
        // RESERVATION, CONFIRMATION, SALE reduce stock (negative display)
        else if ("RESERVATION".equals(type) || "CONFIRMATION".equals(type) || "SALE".equals(type)) {
            return "-" + quantity.abs().toPlainString();
        }
        // ADJUSTMENT and TRANSFER can be either
        else {
            if (quantity.signum() >= 0) {
                return "+" + quantity.toPlainString();
            } else {
                return quantity.toPlainString();
            }
        }
    }
}
