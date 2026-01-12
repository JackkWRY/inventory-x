package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.command.ConfirmReservationCommand;
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
 * ConfirmReservationUseCase - Handles confirming reservations (completing
 * sales).
 * 
 * USE CASE: Payment received, order confirmed, stock leaves inventory
 * 
 * FLOW:
 * 1. Find stock by ID
 * 2. Confirm reservation (domain logic)
 * 3. Save and publish events
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Service
@Transactional
public class ConfirmReservationUseCase {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final DomainEventPublisher eventPublisher;

    public ConfirmReservationUseCase(
            StockRepository stockRepository,
            StockMapper stockMapper,
            DomainEventPublisher eventPublisher) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes confirm reservation use case.
     * 
     * @param command Confirm reservation command
     * @return Stock response with updated quantities
     * @throws StockNotFoundException if stock not found
     */
    public StockResponse execute(ConfirmReservationCommand command) {
        // 1. Find stock
        StockId stockId = StockId.of(command.stockId());
        Quantity quantity = Quantity.of(command.quantity());

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException(
                        "Stock not found: " + command.stockId()));

        // 2. Execute domain logic
        stock.confirmReservation(quantity, command.orderId());

        // 3. Save and publish
        Stock savedStock = stockRepository.save(stock);
        eventPublisher.publish(savedStock.getDomainEvents());
        savedStock.clearDomainEvents();

        return stockMapper.toResponse(savedStock);
    }
}
