package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.command.WithdrawStockCommand;
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
 * WithdrawStockUseCase - Handles internal stock withdrawal.
 * 
 * USE CASE: Department requisition, material consumption, internal usage
 * 
 * FLOW:
 * 1. Find stock by ID
 * 2. Validate sufficient available quantity
 * 3. Execute domain logic (stock.withdraw)
 * 4. Save aggregate
 * 5. Publish domain events
 * 6. Return response DTO
 * 
 * @author InventoryX Development Team
 * @since 2026-01-16
 */
@Service
@Transactional
public class WithdrawStockUseCase {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final DomainEventPublisher eventPublisher;

    public WithdrawStockUseCase(
            StockRepository stockRepository,
            StockMapper stockMapper,
            DomainEventPublisher eventPublisher) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes withdraw stock use case.
     * 
     * @param command Withdraw stock command
     * @return Stock response with updated quantities
     * @throws StockNotFoundException if stock not found
     */
    public StockResponse execute(WithdrawStockCommand command) {
        // 1. Find stock by ID
        StockId stockId = StockId.of(command.stockId());
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException(
                        "Stock not found: " + command.stockId()));

        // 2. Convert quantity to Value Object
        Quantity quantity = Quantity.of(command.quantity());

        // 3. Execute domain logic
        stock.withdraw(quantity, command.department(),
                command.reason(), command.performedBy());

        // 4. Save aggregate
        Stock savedStock = stockRepository.save(stock);

        // 5. Publish events
        eventPublisher.publish(savedStock.getDomainEvents());
        savedStock.clearDomainEvents();

        // 6. Return DTO
        return stockMapper.toResponse(savedStock);
    }
}
