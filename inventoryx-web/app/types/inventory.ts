export interface Stock {
  id: string
  sku: string
  locationId: string
  availableQuantity: number
  reservedQuantity: number
  unitOfMeasure: string
  version: number
  createdAt: string
  updatedAt: string
}

export interface StockMovement {
  id: string
  stockId: string
  movementType: 'RECEIPT' | 'RESERVATION' | 'RELEASE' | 'CONFIRMATION' | 'SALE' | 'TRANSFER' | 'ADJUSTMENT'
  quantity: number
  reason?: string
  referenceId?: string
  performedBy?: string
  performedAt: string
}

export interface ReceiveStockCommand {
  sku: string
  locationId: string
  quantity: string
  reason: string
  receivedBy: string
}

export interface ReserveStockCommand {
  sku: string
  locationId: string
  quantity: string
  orderId: string
}
