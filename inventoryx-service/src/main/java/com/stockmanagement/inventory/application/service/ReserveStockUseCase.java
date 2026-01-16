package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.command.ReserveStockCommand;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.event.DomainEventPublisher;
import com.stockmanagement.inventory.application.mapper.StockMapper;
import com.stockmanagement.inventory.domain.exception.InsufficientStockException;
import com.stockmanagement.inventory.domain.exception.StockNotFoundException;
import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.StockRepository;
import com.stockmanagement.inventory.domain.service.ReservationPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ReserveStockUseCase - Handles reserving stock for orders.
 * 
 * USE CASE: Customer places order, stock allocation
 * 
 * FLOW:
 * 1. Find stock by SKU and location
 * 2. Check reservation policy
 * 3. Reserve stock (domain logic)
 * 4. Save and publish events
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Slf4j
@Service
@Transactional
public class ReserveStockUseCase {

    private final StockRepository stockRepository;
    private final ReservationPolicy reservationPolicy;
    private final StockMapper stockMapper;
    private final DomainEventPublisher eventPublisher;

    public ReserveStockUseCase(
            StockRepository stockRepository,
            ReservationPolicy reservationPolicy,
            StockMapper stockMapper,
            DomainEventPublisher eventPublisher) {
        this.stockRepository = stockRepository;
        this.reservationPolicy = reservationPolicy;
        this.stockMapper = stockMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes reserve stock use case.
     * 
     * @param command Reserve stock command
     * @return Stock response with updated quantities
     * @throws StockNotFoundException     if stock not found
     * @throws InsufficientStockException if not enough stock available
     */
    public StockResponse execute(ReserveStockCommand command) {
        log.info("Reserving stock: SKU={}, location={}, quantity={}, orderId={}",
                command.sku(), command.locationId(), command.quantity(), command.orderId());

        // 1. Convert and find stock
        ProductSKU sku = ProductSKU.of(command.sku());
        LocationId locationId = LocationId.of(command.locationId());
        Quantity quantity = Quantity.of(command.quantity());

        Stock stock = stockRepository
                .findBySkuAndLocation(sku, locationId)
                .orElseThrow(() -> {
                    log.warn("Stock not found: SKU={}, location={}", command.sku(), command.locationId());
                    return new StockNotFoundException(
                            String.format("Stock not found for SKU: %s at location: %s",
                                    sku, locationId));
                });

        // 2. Check policy
        if (!reservationPolicy.canReserve(stock, quantity)) {
            log.warn("Insufficient stock: requested={}, available={}",
                    quantity, stock.getAvailableQuantity());
            throw new InsufficientStockException(
                    String.format("Cannot reserve %s. Available: %s",
                            quantity, stock.getAvailableQuantity()));
        }

        // 3. Execute domain logic
        stock.reserve(quantity, command.orderId());

        // 4. Save and publish
        Stock savedStock = stockRepository.save(stock);
        eventPublisher.publish(savedStock.getDomainEvents());
        savedStock.clearDomainEvents();

        log.info("Stock reserved successfully: id={}, reserved={}",
                savedStock.getId(), savedStock.getReservedQuantity());

        return stockMapper.toResponse(savedStock);
    }
}
