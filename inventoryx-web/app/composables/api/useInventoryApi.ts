import type {
  Stock,
  StockMovement,
  ReceiveStockCommand,
  ReserveStockCommand,
  ReleaseReservationCommand,
  ConfirmReservationCommand,
  AdjustStockCommand,
  WithdrawStockCommand,
  QuickSaleCommand,
  PagedStockResponse,
  PaginationParams
} from '~/types/inventory'

/**
 * Inventory API Composable
 *
 * Provides type-safe methods for interacting with the Inventory Backend API.
 * All methods use the runtime config for base URL and include proper error handling.
 *
 * @example
 * ```ts
 * const api = useInventoryApi()
 * const stocks = await api.getStocks({ sku: 'PROD-001' })
 * ```
 */
export const useInventoryApi = () => {
  const config = useRuntimeConfig()
  const baseUrl = config.public.apiBaseUrl

  return {
    // ============================================
    // Query Operations
    // ============================================

    /**
     * Get all stocks with optional filters
     * @param params - Optional filter parameters
     * @returns Promise<Stock[]> - List of stocks
     */
    async getStocks(params?: { sku?: string; locationId?: string }): Promise<Stock[]> {
      return await $fetch<Stock[]>(`${baseUrl}/stocks`, {
        params
      })
    },

    /**
     * Get a single stock by ID
     * @param id - Stock ID
     * @returns Promise<Stock> - Stock details
     */
    async getStockById(id: string): Promise<Stock> {
      return await $fetch<Stock>(`${baseUrl}/stocks/${id}`)
    },

    /**
     * Get stocks with pagination
     * BEST PRACTICE: Always paginate large result sets
     * @param params - Pagination parameters (page, size)
     * @returns Promise<PagedStockResponse> - Paginated stocks
     */
    async getStocksPaged(params?: PaginationParams): Promise<PagedStockResponse> {
      return await $fetch<PagedStockResponse>(`${baseUrl}/stocks/paged`, {
        params: {
          page: params?.page ?? 0,
          size: params?.size ?? 20
        }
      })
    },

    /**
     * Get stock movement history
     * @param stockId - Stock ID
     * @returns Promise<StockMovement[]> - List of movements (newest first)
     */
    async getStockMovements(stockId: string): Promise<StockMovement[]> {
      return await $fetch<StockMovement[]>(`${baseUrl}/stocks/${stockId}/movements`)
    },

    // ============================================
    // Command Operations
    // ============================================

    /**
     * Receive stock into warehouse
     * @param command - Receive stock command
     * @returns Promise<Stock> - Updated stock
     */
    async receiveStock(command: ReceiveStockCommand): Promise<Stock> {
      return await $fetch<Stock>(`${baseUrl}/stocks/receive`, {
        method: 'POST',
        body: command
      })
    },

    /**
     * Reserve stock for an order
     * @param command - Reserve stock command
     * @returns Promise<Stock> - Updated stock with reservation
     */
    async reserveStock(command: ReserveStockCommand): Promise<Stock> {
      return await $fetch<Stock>(`${baseUrl}/stocks/reserve`, {
        method: 'POST',
        body: command
      })
    },

    /**
     * Release a reservation (cancel order)
     * @param command - Release reservation command
     * @returns Promise<Stock> - Updated stock with released quantity
     */
    async releaseReservation(command: ReleaseReservationCommand): Promise<Stock> {
      return await $fetch<Stock>(`${baseUrl}/stocks/release`, {
        method: 'POST',
        body: command
      })
    },

    /**
     * Confirm a reservation (complete sale)
     * @param command - Confirm reservation command
     * @returns Promise<Stock> - Updated stock after confirmation
     */
    async confirmReservation(command: ConfirmReservationCommand): Promise<Stock> {
      return await $fetch<Stock>(`${baseUrl}/stocks/confirm`, {
        method: 'POST',
        body: command
      })
    },

    /**
     * Adjust stock quantity (manual correction)
     * @param command - Adjust stock command
     * @returns Promise<Stock> - Updated stock with new quantity
     */
    async adjustStock(command: AdjustStockCommand): Promise<Stock> {
      return await $fetch<Stock>(`${baseUrl}/stocks/adjust`, {
        method: 'POST',
        body: command
      })
    },

    /**
     * Withdraw stock for internal use
     * @param command - Withdraw stock command
     * @returns Promise<Stock> - Updated stock after withdrawal
     */
    async withdrawStock(command: WithdrawStockCommand): Promise<Stock> {
      return await $fetch<Stock>(`${baseUrl}/stocks/withdraw`, {
        method: 'POST',
        body: command
      })
    },

    /**
     * Quick sale for POS/Walk-in
     * @param command - Quick sale command
     * @returns Promise<Stock> - Updated stock after sale
     */
    async quickSale(command: QuickSaleCommand): Promise<Stock> {
      return await $fetch<Stock>(`${baseUrl}/stocks/sale`, {
        method: 'POST',
        body: command
      })
    }
  }
}
