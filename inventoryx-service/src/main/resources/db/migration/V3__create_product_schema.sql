-- ============================================================================
-- Flyway Migration: V5__create_product_schema.sql
-- ============================================================================
-- PURPOSE: Creates the Product Master Data schema.
-- This table replaces strict string-based SKUs with a validated Product relationship.
--
-- ENTITY: Product (Aggregate Root)
--
-- AUTHOR: InventoryX Development Team
-- DATE: 2026-01-19
-- ============================================================================

CREATE TABLE inventory.products (
    -- Identity
    id VARCHAR(36) PRIMARY KEY, -- UUID

    -- Core Attributes
    sku VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    
    -- Pricing & Unit
    price_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    price_currency VARCHAR(3) NOT NULL DEFAULT 'USD', -- ISO 4217 Currency Code
    unit_of_measure VARCHAR(20) NOT NULL DEFAULT 'PIECE',

    -- Audit & Optimistic Locking
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT uk_product_sku UNIQUE (sku),
    CONSTRAINT chk_product_price_positive CHECK (price_amount >= 0)
);

-- Indexes for efficient lookup
CREATE INDEX idx_products_sku ON inventory.products(sku);
CREATE INDEX idx_products_category ON inventory.products(category);
CREATE INDEX idx_products_name ON inventory.products(name);

-- Trigger for updated_at (reusing existing function from V1)
CREATE TRIGGER update_products_updated_at
    BEFORE UPDATE ON inventory.products
    FOR EACH ROW
    EXECUTE FUNCTION inventory.update_updated_at_column();
