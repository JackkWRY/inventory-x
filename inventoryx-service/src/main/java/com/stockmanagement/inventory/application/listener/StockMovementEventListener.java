package com.stockmanagement.inventory.application.listener;

import com.stockmanagement.inventory.domain.event.*;
import com.stockmanagement.inventory.infrastructure.persistence.entity.StockEntity;
import com.stockmanagement.inventory.infrastructure.persistence.entity.StockMovementEntity;
import com.stockmanagement.inventory.infrastructure.persistence.repository.JpaStockMovementRepository;
import com.stockmanagement.inventory.infrastructure.persistence.repository.JpaStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * StockMovementEventListener - Captures domain events and records stock
 * movement history.
 * 
 * CLEAN ARCHITECTURE: Application Layer
 * =====================================
 * Decouples the simplified Domain Model from the complex Audit requirement.
 * The Domain only needs to know "Stock was Received", it doesn't need to know
 * about "Tables".
 * 
 * TRANSACTION:
 * Uses MANDATORY propagation to participate in the existing transaction of the
 * command.
 * If the command fails, the audit log should also roll back.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockMovementEventListener {

    private final JpaStockMovementRepository movementRepository;
    private final JpaStockRepository stockRepository;

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    public void handleStockReceived(StockReceivedEvent event) {
        log.debug("Processing StockReceivedEvent: {}", event);
        saveMovement(
                event.stockId().value(),
                "RECEIPT",
                event.quantity().value(),
                event.reason(),
                null,
                event.performedBy(),
                event.occurredOn());
    }

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    public void handleStockReserved(StockReservedEvent event) {
        log.debug("Processing StockReservedEvent: {}", event);
        saveMovement(
                event.stockId().value(),
                "RESERVATION",
                event.quantity().value(), // Reserved amount (positive)
                "Order Reservation",
                event.orderId(),
                "system", // Usually system action, or we need to update Event to include operator
                event.occurredOn());
    }

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    public void handleReservationReleased(ReservationReleasedEvent event) {
        log.debug("Processing ReservationReleasedEvent: {}", event);
        saveMovement(
                event.stockId().value(),
                "RELEASE",
                event.quantity().value(),
                "Order Cancellation",
                event.orderId(),
                "system",
                event.occurredOn());
    }

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    public void handleReservationConfirmed(ReservationConfirmedEvent event) {
        log.debug("Processing ReservationConfirmedEvent: {}", event);
        saveMovement(
                event.stockId().value(),
                "CONFIRMATION",
                event.quantity().value(),
                "Order Confirmation",
                event.orderId(),
                "system",
                event.occurredOn());
    }

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    public void handleStockAdjusted(StockAdjustedEvent event) {
        log.debug("Processing StockAdjustedEvent: {}", event);
        saveMovement(
                event.stockId().value(),
                "ADJUSTMENT",
                event.difference().value(), // Can be negative/positive
                event.reason(),
                null,
                event.performedBy(),
                event.occurredOn());
    }

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    public void handleStockWithdrawn(StockWithdrawnEvent event) {
        log.debug("Processing StockWithdrawnEvent: {}", event);
        saveMovement(
                event.stockId().value(),
                "WITHDRAWAL",
                event.quantity().value().negate(),
                event.reason(),
                event.department(),
                event.performedBy(),
                event.occurredOn());
    }

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    public void handleStockSold(StockSoldEvent event) {
        log.debug("Processing StockSoldEvent: {}", event);
        saveMovement(
                event.stockId().value(),
                "SALE",
                event.quantity().value().negate(), // Deduction
                "Quick Sale / POS",
                event.orderId(),
                event.performedBy(),
                event.occurredOn());
    }

    private void saveMovement(String stockId, String type, BigDecimal quantity, String reason, String refId,
            String performedBy, java.time.Instant occurredOn) {
        StockEntity stockEntity = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalStateException("Stock not found for movement logging: " + stockId));

        StockMovementEntity movement = new StockMovementEntity();
        movement.setStock(stockEntity);
        movement.setMovementType(type);
        movement.setQuantity(quantity);
        movement.setReason(reason);
        movement.setReferenceId(refId);
        movement.setPerformedBy(performedBy);
        movement.setPerformedAt(occurredOn);

        // ID is usually generated by UUID if not set, or let Hibernate do it?
        // Entity uses String ID. Let's check generator.
        if (movement.getId() == null) {
            movement.setId(java.util.UUID.randomUUID().toString());
        }

        movementRepository.save(movement);
    }
}
