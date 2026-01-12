package com.stockmanagement.inventory.application.mapper;

import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.domain.model.Stock;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * StockMapper - Converts between Stock domain object and StockResponse DTO.
 * 
 * PURPOSE: Decouple domain from API representation
 * 
 * CLEAN ARCHITECTURE:
 * Domain objects should not have framework annotations (Jackson, etc.)
 * Mappers handle conversion in Application Layer
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Component
public class StockMapper {

    /**
     * Converts Stock domain object to StockResponse DTO.
     */
    public StockResponse toResponse(Stock stock) {
        return new StockResponse(
                stock.getId().value(),
                stock.getSku().value(),
                stock.getLocationId().value(),
                stock.getAvailableQuantity().toString(),
                stock.getReservedQuantity().toString(),
                stock.getUnitOfMeasure().name(),
                stock.getVersion(),
                stock.getCreatedAt().toString(),
                stock.getUpdatedAt().toString());
    }

    /**
     * Converts list of Stock to list of StockResponse.
     */
    public List<StockResponse> toResponseList(List<Stock> stocks) {
        return stocks.stream()
                .map(this::toResponse)
                .toList();
    }
}
