package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.command.QuickSaleCommand;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.event.DomainEventPublisher;
import com.stockmanagement.inventory.application.mapper.StockMapper;
import com.stockmanagement.inventory.domain.exception.StockNotFoundException;
import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * QuickSaleUseCase - Handles immediate POS/Walk-in sales.
 * 
 * USE CASE: Point of Sale, retail counter, walk-in customers
 * 
 * BUSINESS LOGIC:
 * Combines Reserve + Confirm into single operation for efficiency.
 * Stock is immediately deducted from available quantity.
 * 
 * FLOW:
 * 1. Find stock by ID
 * 2. Validate sufficient available quantity
 * 3. Execute domain logic (stock.quickSale)
 * 4. Save aggregate
 * 5. Publish domain events
 * 6. Return response DTO
 * 
 * @author InventoryX Development Team
 * @since 2026-01-16
 */
@Service
@Transactional
public class QuickSaleUseCase {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final DomainEventPublisher eventPublisher;

    public QuickSaleUseCase(
            StockRepository stockRepository,
            StockMapper stockMapper,
            DomainEventPublisher eventPublisher) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes quick sale use case.
     * 
     * @param command Quick sale command
     * @return Stock response with updated quantities
     * @throws StockNotFoundException if stock not found
     */
    public StockResponse execute(QuickSaleCommand command) {
        // 1. Find stock by ID
        StockId stockId = StockId.of(command.stockId());
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException(
                        "Stock not found: " + command.stockId()));

        // 2. Convert quantity to Value Object
        Quantity quantity = Quantity.of(command.quantity());

        // 3. Execute domain logic
        stock.quickSale(quantity, command.orderId(), command.performedBy());

        // 4. Save aggregate
        Stock savedStock = stockRepository.save(stock);

        // 5. Publish events
        eventPublisher.publish(savedStock.getDomainEvents());
        savedStock.clearDomainEvents();

        // 6. Return DTO
        return stockMapper.toResponse(savedStock);
    }
}
