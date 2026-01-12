package com.stockmanagement.inventory.domain.event;

import java.time.Instant;

/**
 * DomainEvent - Base interface for all domain events.
 * 
 * DDD PATTERN: Domain Event
 * =========================
 * Domain events represent something that happened in the domain.
 * They are immutable facts about the past.
 * 
 * EVENT-DRIVEN ARCHITECTURE:
 * =========================
 * Events enable:
 * - Eventual consistency between bounded contexts
 * - Audit trail and event sourcing
 * - Decoupling of components
 * - Asynchronous processing
 * 
 * NAMING CONVENTION:
 * ================
 * Events are named in past tense (StockReceivedEvent, not ReceiveStockEvent)
 * because they represent something that already happened.
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
public interface DomainEvent {

    /**
     * Unique identifier for this event instance.
     * Used for deduplication and tracking.
     */
    String eventId();

    /**
     * When this event occurred.
     * Used for ordering and audit trail.
     */
    Instant occurredOn();
}
