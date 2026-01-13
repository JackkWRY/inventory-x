package com.stockmanagement.inventory.infrastructure.persistence.repository;

import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.StockRepository;
import com.stockmanagement.inventory.infrastructure.persistence.entity.StockEntity;
import com.stockmanagement.inventory.infrastructure.persistence.mapper.StockEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * StockRepositoryImpl - Implementation of domain's StockRepository interface.
 * 
 * CLEAN ARCHITECTURE: Adapter Pattern
 * ===================================
 * Implements domain interface using JPA infrastructure.
 * 
 * PATTERN:
 * 1. Convert domain → entity
 * 2. Call JPA repository
 * 3. Convert entity → domain
 * 
 * TRANSACTION MANAGEMENT:
 * Handled by @Transactional in Application Layer (Use Cases).
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Repository
public class StockRepositoryImpl implements StockRepository {

    private final JpaStockRepository jpaRepository;
    private final StockEntityMapper mapper;

    public StockRepositoryImpl(
            JpaStockRepository jpaRepository,
            StockEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Stock save(Stock stock) {
        // 1. Convert domain to entity
        StockEntity entity = mapper.toEntity(stock);

        // 2. Check if this is a new entity (version is null) or existing
        StockEntity savedEntity;
        if (stock.getVersion() == null) {
            // New entity - let JPA generate version
            entity.setVersion(null);
            savedEntity = jpaRepository.save(entity);
        } else {
            // Existing entity - JPA will handle optimistic locking
            savedEntity = jpaRepository.save(entity);
        }

        // 3. Convert back to domain
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Stock> findById(StockId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Stock> findBySkuAndLocation(ProductSKU sku, LocationId locationId) {
        return jpaRepository.findBySkuAndLocationId(
                sku.value(),
                locationId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Stock> findBySku(ProductSKU sku) {
        List<StockEntity> entities = jpaRepository.findBySku(sku.value());
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Stock> findByLocation(LocationId locationId) {
        List<StockEntity> entities = jpaRepository.findByLocationId(locationId.value());
        return mapper.toDomainList(entities);
    }

    @Override
    public void delete(StockId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public boolean exists(ProductSKU sku, LocationId locationId) {
        return jpaRepository.existsBySkuAndLocationId(
                sku.value(),
                locationId.value());
    }
}
