package com.stockmanagement.inventory.infrastructure.persistence.mapper;

import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.infrastructure.persistence.entity.StockEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * StockEntityMapper - Converts between Stock domain and StockEntity JPA entity.
 * 
 * CLEAN ARCHITECTURE: Adapter Pattern
 * ===================================
 * Adapts domain model to persistence model.
 * 
 * BIDIRECTIONAL MAPPING:
 * - toEntity: Stock → StockEntity (before save)
 * - toDomain: StockEntity → Stock (after load)
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Component
public class StockEntityMapper {

    /**
     * Converts domain Stock to JPA StockEntity.
     * Used before saving to database.
     * 
     * @param stock Domain object
     * @return JPA entity
     */
    public StockEntity toEntity(Stock stock) {
        if (stock == null) {
            return null;
        }

        StockEntity entity = new StockEntity();

        entity.setId(stock.getId().value());
        entity.setSku(stock.getSku().value());
        entity.setLocationId(stock.getLocationId().value());
        entity.setAvailableQuantity(stock.getAvailableQuantity().value());
        entity.setReservedQuantity(stock.getReservedQuantity().value());
        entity.setUnitOfMeasure(stock.getUnitOfMeasure());
        entity.setVersion(stock.getVersion());
        entity.setCreatedAt(stock.getCreatedAt());
        entity.setUpdatedAt(java.time.Instant.now()); // Always update timestamp

        return entity;
    }

    /**
     * Converts JPA StockEntity to domain Stock.
     * Used after loading from database.
     * 
     * IMPORTANT: Uses Stock.reconstitute() not constructor.
     * 
     * @param entity JPA entity
     * @return Domain object
     */
    public Stock toDomain(StockEntity entity) {
        if (entity == null) {
            return null;
        }

        return Stock.reconstitute(
                StockId.of(entity.getId()),
                ProductSKU.of(entity.getSku()),
                LocationId.of(entity.getLocationId()),
                Quantity.of(entity.getAvailableQuantity().toString()),
                Quantity.of(entity.getReservedQuantity().toString()),
                entity.getUnitOfMeasure(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Converts list of entities to list of domain objects.
     * 
     * @param entities JPA entities
     * @return Domain objects
     */
    public List<Stock> toDomainList(List<StockEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }
}
