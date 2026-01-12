-- Create inventory schema
CREATE SCHEMA IF NOT EXISTS inventory;

-- Create stocks table
CREATE TABLE inventory.stocks (
    id VARCHAR(36) PRIMARY KEY,
    sku VARCHAR(20) NOT NULL,
    location_id VARCHAR(36) NOT NULL,
    available_quantity NUMERIC(19,4) NOT NULL DEFAULT 0,
    reserved_quantity NUMERIC(19,4) NOT NULL DEFAULT 0,
    unit_of_measure VARCHAR(10) NOT NULL DEFAULT 'PIECE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_stock_sku_location UNIQUE(sku, location_id),
    CONSTRAINT chk_available_quantity CHECK (available_quantity >= 0),
    CONSTRAINT chk_reserved_quantity CHECK (reserved_quantity >= 0)
);

-- Create indexes
CREATE INDEX idx_stocks_sku ON inventory.stocks(sku);
CREATE INDEX idx_stocks_location ON inventory.stocks(location_id);
CREATE INDEX idx_stocks_created_at ON inventory.stocks(created_at);

-- Create stock_movements table
CREATE TABLE inventory.stock_movements (
    id VARCHAR(36) PRIMARY KEY,
    stock_id VARCHAR(36) NOT NULL,
    movement_type VARCHAR(20) NOT NULL,
    quantity NUMERIC(19,4) NOT NULL,
    reason TEXT,
    reference_id VARCHAR(100),
    performed_by VARCHAR(36),
    performed_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_stock FOREIGN KEY (stock_id) 
        REFERENCES inventory.stocks(id) ON DELETE CASCADE,
    CONSTRAINT chk_movement_type CHECK (movement_type IN 
        ('RECEIPT', 'RESERVATION', 'RELEASE', 'CONFIRMATION', 'SALE', 'TRANSFER', 'ADJUSTMENT'))
);

-- Create indexes for movements
CREATE INDEX idx_movements_stock ON inventory.stock_movements(stock_id);
CREATE INDEX idx_movements_type ON inventory.stock_movements(movement_type);
CREATE INDEX idx_movements_reference ON inventory.stock_movements(reference_id);
CREATE INDEX idx_movements_performed_at ON inventory.stock_movements(performed_at);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION inventory.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger
CREATE TRIGGER update_stocks_updated_at 
    BEFORE UPDATE ON inventory.stocks
    FOR EACH ROW
    EXECUTE FUNCTION inventory.update_updated_at_column();
