-- ============================================================================
-- Flyway Migration: V4__create_location_and_link_stocks.sql
-- ============================================================================
-- PURPOSE: 
-- 1. Create Location Master Data schema.
-- 2. Link Stocks to Products and Locations via Foreign Keys.
--
-- AUTHOR: InventoryX Development Team
-- DATE: 2026-01-19
-- ============================================================================

-- ============================================================================
-- 1. Create LOCATIONS Table
-- ============================================================================
CREATE TABLE inventory.locations (
    -- Identity
    id VARCHAR(36) PRIMARY KEY,
    
    -- Attributes
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'WAREHOUSE', -- WAREHOUSE, STORE, TRANSIT
    description TEXT,
    address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE
    
    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT uk_location_name UNIQUE (name)
);

-- Indexes
CREATE INDEX idx_locations_status ON inventory.locations(status);

-- Trigger for updated_at
CREATE TRIGGER update_locations_updated_at
    BEFORE UPDATE ON inventory.locations
    FOR EACH ROW
    EXECUTE FUNCTION inventory.update_updated_at_column();

-- ============================================================================
-- 2. Seed Default Location (Migration Support)
-- ============================================================================
-- Ensure we have at least one location to link existing stocks to (if any)
INSERT INTO inventory.locations (id, name, type, description, status)
VALUES ('LOC-DEFAULT-001', 'Main Warehouse', 'WAREHOUSE', 'Default migration location', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 3. Update STOCKS Table (Link to Product & Location)
-- ============================================================================

-- 3.1 Link to Products
ALTER TABLE inventory.stocks ADD COLUMN product_id VARCHAR(36);

-- Populate product_id by matching SKU
UPDATE inventory.stocks s
SET product_id = p.id
FROM inventory.products p
WHERE s.sku = p.sku;

-- Verify migration integrity (Optional safety check - practically enforced by subsequent FK)
-- DELETE FROM inventory.stocks WHERE product_id IS NULL; -- Uncomment if you want to purge unlinked stocks

-- Enforce Not Null and Add FK
-- We allow NULL temporarily if migration fails to match, but strictly we should enforce it.
-- Assuming all stocks have matching products from Phase 8.
-- ALTER TABLE inventory.stocks ALTER COLUMN product_id SET NOT NULL; -- Enable after verification
CREATE INDEX idx_stocks_product_id ON inventory.stocks(product_id);

ALTER TABLE inventory.stocks
ADD CONSTRAINT fk_stock_product FOREIGN KEY (product_id)
    REFERENCES inventory.products(id) ON DELETE RESTRICT;

-- 3.2 Link to Locations
-- Existing 'location_id' column in stocks was a String/VARCHAR.
-- We need to ensure these string IDs actually exist in our new locations table.
-- Strategy: If the location_id string doesn't match a UUID format or isn't in locations, 
-- we typically need to map it. For this migration, we'll assume the 'location_id' 
-- stored was 'Main Warehouse' or similar, or we just rely on the new location we created.

-- If existing stocks have random string location_ids, we insert them into locations table to preserve data integrity
INSERT INTO inventory.locations (id, name, type, description, status)
SELECT DISTINCT location_id, 'Migrated Location ' || location_id, 'WAREHOUSE', 'Auto-generated from existing stocks', 'ACTIVE'
FROM inventory.stocks
WHERE location_id NOT IN (SELECT id FROM inventory.locations);

-- Now add the FK constraint
-- Note: existing column is defined as `location_id VARCHAR(36)` in V1, so type matches.
ALTER TABLE inventory.stocks
ADD CONSTRAINT fk_stock_location FOREIGN KEY (location_id)
    REFERENCES inventory.locations(id) ON DELETE RESTRICT;

-- ============================================================================
-- MIGRATION COMPLETE
-- ============================================================================
