package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.command.ReceiveStockCommand;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.event.DomainEventPublisher;
import com.stockmanagement.inventory.application.mapper.StockMapper;
import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ReceiveStockUseCase - Handles receiving stock into warehouse.
 * 
 * USE CASE: Supplier delivery, stock transfer receipt, initial stock setup
 * 
 * FLOW:
 * 1. Convert command to domain objects
 * 2. Find existing stock or create new
 * 3. Execute domain logic (stock.receiveStock)
 * 4. Save aggregate
 * 5. Publish domain events
 * 6. Return response DTO
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Slf4j
@Service
@Transactional
public class ReceiveStockUseCase {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final DomainEventPublisher eventPublisher;

    public ReceiveStockUseCase(
            StockRepository stockRepository,
            StockMapper stockMapper,
            DomainEventPublisher eventPublisher) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes receive stock use case.
     * 
     * @param command Receive stock command
     * @return Stock response with updated quantities
     */
    public StockResponse execute(ReceiveStockCommand command) {
        log.info("Receiving stock: SKU={}, location={}, quantity={}",
                command.sku(), command.locationId(), command.quantity());

        // 1. Convert DTO to Value Objects
        ProductSKU sku = ProductSKU.of(command.sku());
        LocationId locationId = LocationId.of(command.locationId());
        Quantity quantity = Quantity.of(command.quantity());
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.valueOf(command.unitOfMeasure());

        // 2. Find or create Stock aggregate
        Stock stock = stockRepository
                .findBySkuAndLocation(sku, locationId)
                .orElseGet(() -> {
                    log.debug("Creating new stock for SKU={} at location={}",
                            command.sku(), command.locationId());
                    return Stock.create(sku, locationId, unitOfMeasure);
                });

        // 3. Execute domain logic
        stock.receiveStock(quantity, command.reason(), command.performedBy());

        // 4. Save aggregate
        Stock savedStock = stockRepository.save(stock);
        log.debug("Stock saved: id={}", savedStock.getId());

        // 5. Publish events
        eventPublisher.publish(savedStock.getDomainEvents());
        savedStock.clearDomainEvents();

        log.info("Stock received successfully: id={}, newAvailable={}",
                savedStock.getId(), savedStock.getAvailableQuantity());

        // 6. Return DTO
        return stockMapper.toResponse(savedStock);
    }
}
