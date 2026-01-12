-- ============================================================================
-- Flyway Migration: V1__create_inventory_schema.sql
-- ============================================================================
-- PURPOSE: Creates foundational schema for Inventory Management bounded context
-- 
-- DDD PATTERN: Persistence for Aggregates
-- - Stock (Aggregate Root) → stocks table
-- - StockMovement (Entity) → stock_movements table
-- 
-- AUTHOR: InventoryX Development Team
-- DATE: 2026-01-12
-- ============================================================================

-- ============================================================================
-- SCHEMA CREATION
-- ============================================================================
-- Dedicated schema for Inventory bounded context (isolation from other contexts)
CREATE SCHEMA IF NOT EXISTS inventory;

-- ============================================================================
-- STOCKS TABLE (Aggregate Root)
-- ============================================================================
-- Stores current state of stock at each location (one row per SKU+Location)
--
-- BUSINESS INVARIANTS (enforced by constraints):
-- - available_quantity >= 0
-- - reserved_quantity >= 0
-- - Unique (sku, location_id)
-- - Optimistic locking via version
CREATE TABLE inventory.stocks (
    -- Identity
    id VARCHAR(36) PRIMARY KEY,
    
    -- Business attributes
    sku VARCHAR(20) NOT NULL,                      -- Foreign key to product.products
    location_id VARCHAR(36) NOT NULL,              -- Foreign key to warehouse.locations
    available_quantity NUMERIC(19,4) NOT NULL DEFAULT 0,
    reserved_quantity NUMERIC(19,4) NOT NULL DEFAULT 0,
    unit_of_measure VARCHAR(10) NOT NULL DEFAULT 'PIECE',
    
    -- Optimistic locking (prevents concurrent update conflicts)
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Audit trail
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT uk_stock_sku_location UNIQUE(sku, location_id),
    CONSTRAINT chk_available_quantity CHECK (available_quantity >= 0),
    CONSTRAINT chk_reserved_quantity CHECK (reserved_quantity >= 0)
);

-- Indexes for common query patterns
CREATE INDEX idx_stocks_sku ON inventory.stocks(sku);           -- Find by SKU
CREATE INDEX idx_stocks_location ON inventory.stocks(location_id); -- Find by location
CREATE INDEX idx_stocks_created_at ON inventory.stocks(created_at); -- Recent stock

-- ============================================================================
-- STOCK_MOVEMENTS TABLE (Entity - Audit Trail)
-- ============================================================================
-- Immutable audit trail of all stock changes
-- Part of Stock aggregate, accessed through Stock root
CREATE TABLE inventory.stock_movements (
    -- Identity
    id VARCHAR(36) PRIMARY KEY,
    
    -- Aggregate relationship
    stock_id VARCHAR(36) NOT NULL,
    
    -- Movement details
    movement_type VARCHAR(20) NOT NULL,  -- RECEIPT, RESERVATION, RELEASE, etc.
    quantity NUMERIC(19,4) NOT NULL,
    reason TEXT,
    reference_id VARCHAR(100),           -- Order ID, Transfer ID, etc.
    
    -- Audit
    performed_by VARCHAR(36),
    performed_at TIMESTAMP NOT NULL,
    
    -- Foreign key with cascade delete (movements deleted with stock)
    CONSTRAINT fk_stock FOREIGN KEY (stock_id) 
        REFERENCES inventory.stocks(id) ON DELETE CASCADE,
    
    -- Valid movement types
    CONSTRAINT chk_movement_type CHECK (movement_type IN 
        ('RECEIPT', 'RESERVATION', 'RELEASE', 'CONFIRMATION', 
         'SALE', 'TRANSFER', 'ADJUSTMENT'))
);

-- Indexes for audit queries
CREATE INDEX idx_movements_stock ON inventory.stock_movements(stock_id);
CREATE INDEX idx_movements_type ON inventory.stock_movements(movement_type);
CREATE INDEX idx_movements_reference ON inventory.stock_movements(reference_id);
CREATE INDEX idx_movements_performed_at ON inventory.stock_movements(performed_at);

-- ============================================================================
-- TRIGGER: Auto-update updated_at timestamp
-- ============================================================================
-- Automatically sets updated_at on every UPDATE (works with JPA, JDBC, SQL)
CREATE OR REPLACE FUNCTION inventory.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_stocks_updated_at 
    BEFORE UPDATE ON inventory.stocks
    FOR EACH ROW
    EXECUTE FUNCTION inventory.update_updated_at_column();

-- ============================================================================
-- MIGRATION COMPLETE
-- ============================================================================
-- Created:
-- ✓ inventory schema
-- ✓ stocks table with constraints and indexes
-- ✓ stock_movements table with constraints and indexes
-- ✓ Auto-update trigger for updated_at
--
-- Verify: SELECT * FROM flyway_schema_history;
-- ============================================================================
