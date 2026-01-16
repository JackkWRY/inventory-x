package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.command.AdjustStockCommand;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.event.DomainEventPublisher;
import com.stockmanagement.inventory.application.mapper.StockMapper;
import com.stockmanagement.inventory.domain.exception.StockNotFoundException;
import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AdjustStockUseCase - Handles manual stock adjustments.
 * 
 * USE CASE: Physical inventory count, damaged goods, theft, data correction
 * 
 * FLOW:
 * 1. Find stock by ID
 * 2. Adjust quantity (domain logic)
 * 3. Save and publish events
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Slf4j
@Service
@Transactional
public class AdjustStockUseCase {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final DomainEventPublisher eventPublisher;

    public AdjustStockUseCase(
            StockRepository stockRepository,
            StockMapper stockMapper,
            DomainEventPublisher eventPublisher) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes adjust stock use case.
     * 
     * @param command Adjust stock command
     * @return Stock response with updated quantities
     * @throws StockNotFoundException if stock not found
     */
    public StockResponse execute(AdjustStockCommand command) {
        log.info("Adjusting stock: stockId={}, newQuantity={}, reason={}",
                command.stockId(), command.newQuantity(), command.reason());

        // 1. Find stock
        StockId stockId = StockId.of(command.stockId());
        Quantity newQuantity = Quantity.of(command.newQuantity());

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> {
                    log.warn("Stock not found: {}", command.stockId());
                    return new StockNotFoundException("Stock not found: " + command.stockId());
                });

        log.debug("Current stock: available={}, reserved={}",
                stock.getAvailableQuantity(), stock.getReservedQuantity());

        // 2. Execute domain logic
        stock.adjustStock(newQuantity, command.reason(), command.performedBy());

        // 3. Save and publish
        Stock savedStock = stockRepository.save(stock);
        eventPublisher.publish(savedStock.getDomainEvents());
        savedStock.clearDomainEvents();

        log.info("Stock adjusted successfully: id={}, newAvailable={}",
                savedStock.getId(), savedStock.getAvailableQuantity());

        return stockMapper.toResponse(savedStock);
    }
}
