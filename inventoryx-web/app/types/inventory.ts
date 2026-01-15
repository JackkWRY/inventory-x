/**
 * Stock - Represents stock at a specific location
 *
 * NOTE: quantity fields are strings to match Backend BigDecimal serialization
 */
export interface Stock {
  /** Unique stock identifier */
  id: string
  /** Product SKU code */
  sku: string
  /** Storage location identifier */
  locationId: string
  /** Quantity available for reservation (as string for BigDecimal precision) */
  availableQuantity: string
  /** Quantity already reserved (as string for BigDecimal precision) */
  reservedQuantity: string
  /** Unit of measure (PIECE, KILOGRAM, etc.) */
  unitOfMeasure: string
  /** Optimistic locking version */
  version: number
  /** Creation timestamp (ISO 8601) */
  createdAt: string
  /** Last update timestamp (ISO 8601) */
  updatedAt: string
}

/**
 * StockMovement - Represents a stock movement event
 */
export interface StockMovement {
  id: string
  stockId: string
  movementType: 'RECEIPT' | 'RESERVATION' | 'RELEASE' | 'CONFIRMATION' | 'SALE' | 'TRANSFER' | 'ADJUSTMENT'
  quantity: string
  reason?: string
  referenceId?: string
  performedBy?: string
  performedAt: string
}

// ============================================
// Command DTOs (match Backend exactly)
// ============================================

/**
 * Command to receive stock into warehouse
 */
export interface ReceiveStockCommand {
  sku: string
  locationId: string
  quantity: string
  unitOfMeasure: string
  reason: string
  performedBy: string
}

/**
 * Command to reserve stock for an order
 */
export interface ReserveStockCommand {
  sku: string
  locationId: string
  quantity: string
  orderId: string
}

/**
 * Command to release/cancel a reservation
 */
export interface ReleaseReservationCommand {
  stockId: string
  quantity: string
  orderId: string
}

/**
 * Command to confirm reservation (complete sale)
 */
export interface ConfirmReservationCommand {
  stockId: string
  quantity: string
  orderId: string
}

/**
 * Command to manually adjust stock quantity
 */
export interface AdjustStockCommand {
  stockId: string
  newQuantity: string
  reason: string
  performedBy: string
}

// ============================================
// Pagination Types
// ============================================

/**
 * Pagination query parameters
 */
export interface PaginationParams {
  page?: number
  size?: number
}

/**
 * Paginated response for stock queries
 * BEST PRACTICE: Always paginate large result sets
 */
export interface PagedStockResponse {
  /** List of stocks in current page */
  content: Stock[]
  /** Current page number (0-indexed) */
  page: number
  /** Number of items per page */
  size: number
  /** Total number of items across all pages */
  totalElements: number
  /** Total number of pages */
  totalPages: number
  /** Whether this is the first page */
  first: boolean
  /** Whether this is the last page */
  last: boolean
}
