package com.stockmanagement.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * InventoryServiceApplication - Main entry point for Inventory Management
 * Service.
 * 
 * CLEAN ARCHITECTURE: Layered Structure
 * =====================================
 * 1. Domain Layer (com.*.domain) - Business logic, no framework dependencies
 * 2. Application Layer (com.*.application) - Use cases, orchestration
 * 3. Infrastructure Layer (com.*.infrastructure) - JPA, database, external
 * systems
 * 4. Presentation Layer (com.*.presentation) - REST controllers, API
 * 
 * DEPENDENCY RULE: Dependencies point inward (Presentation → Application →
 * Domain)
 * 
 * BOUNDED CONTEXT: Inventory Management
 * - Tracks stock levels at locations
 * - Manages stock movements (receipt, transfer, adjustment)
 * - Reserves stock for orders
 * - Provides stock availability information
 * 
 * CONTEXT INTEGRATION:
 * - Product Catalog: Validates SKUs
 * - Warehouse: Validates locations
 * - Order Management: Reserves stock
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@SpringBootApplication
public class InventoryServiceApplication {

    /**
     * Application entry point.
     * 
     * BOOTSTRAP SEQUENCE:
     * 1. Spring context initialization
     * 2. Database connection pool setup
     * 3. Flyway migrations execution
     * 4. JPA repositories initialization
     * 5. Embedded Tomcat server start
     * 
     * HEALTH CHECK: http://localhost:8081/api/v1/actuator/health
     */
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

}
