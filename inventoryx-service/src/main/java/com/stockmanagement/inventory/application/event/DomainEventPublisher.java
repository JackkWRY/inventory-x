package com.stockmanagement.inventory.application.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DomainEventPublisher - Publishes domain events to Spring's event system.
 * 
 * PURPOSE:
 * - Decouple domain from event infrastructure
 * - Enable event-driven architecture
 * - Support eventual consistency
 * 
 * USAGE:
 * After saving aggregate, publish its events then clear them
 * 
 * FUTURE ENHANCEMENTS:
 * - Send to message queue (Kafka, RabbitMQ)
 * - Persist events for event sourcing
 * - Retry failed events
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Slf4j
@Component
public class DomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public DomainEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes domain events to Spring's event system.
     * Events can be consumed by @EventListener methods.
     */
    public void publish(List<Object> events) {
        if (events.isEmpty()) {
            return;
        }

        log.debug("Publishing {} domain event(s)", events.size());
        events.forEach(event -> {
            log.debug("Publishing event: {}", event.getClass().getSimpleName());
            eventPublisher.publishEvent(event);
        });
    }
}
