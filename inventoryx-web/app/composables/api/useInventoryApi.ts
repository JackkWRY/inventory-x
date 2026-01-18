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
  const { $api } = useNuxtApp();
  // We can let the Axios instance handle the BaseURL as configured in the plugin
  // const config = useRuntimeConfig()
  // const baseUrl = config.public.apiBaseUrl // Not needed if axios has baseURL

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
      const response = await $api.get<Stock[]>('/stocks', { params })
      return response.data
    },

    /**
     * Get a single stock by ID
     * @param id - Stock ID
     * @returns Promise<Stock> - Stock details
     */
    async getStockById(id: string): Promise<Stock> {
        const response = await $api.get<Stock>(`/stocks/${id}`)
        return response.data
    },

    /**
     * Get stocks with pagination
     * BEST PRACTICE: Always paginate large result sets
     * @param params - Pagination parameters (page, size)
     * @returns Promise<PagedStockResponse> - Paginated stocks
     */
    async getStocksPaged(params?: PaginationParams): Promise<PagedStockResponse> {
      const response = await $api.get<PagedStockResponse>('/stocks/paged', {
        params: {
          page: params?.page ?? 0,
          size: params?.size ?? 20
        }
      })
      return response.data
    },

    /**
     * Get stock movement history
     * @param stockId - Stock ID
     * @returns Promise<StockMovement[]> - List of movements (newest first)
     */
    async getStockMovements(stockId: string): Promise<StockMovement[]> {
        const response = await $api.get<StockMovement[]>(`/stocks/${stockId}/movements`)
        return response.data
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
        const response = await $api.post<Stock>('/stocks/receive', command)
        return response.data
    },

    /**
     * Reserve stock for an order
     * @param command - Reserve stock command
     * @returns Promise<Stock> - Updated stock with reservation
     */
    async reserveStock(command: ReserveStockCommand): Promise<Stock> {
        const response = await $api.post<Stock>('/stocks/reserve', command)
        return response.data
    },

    /**
     * Release a reservation (cancel order)
     * @param command - Release reservation command
     * @returns Promise<Stock> - Updated stock with released quantity
     */
    async releaseReservation(command: ReleaseReservationCommand): Promise<Stock> {
        const response = await $api.post<Stock>('/stocks/release', command)
        return response.data
    },

    /**
     * Confirm a reservation (complete sale)
     * @param command - Confirm reservation command
     * @returns Promise<Stock> - Updated stock after confirmation
     */
    async confirmReservation(command: ConfirmReservationCommand): Promise<Stock> {
        const response = await $api.post<Stock>('/stocks/confirm', command)
        return response.data
    },

    /**
     * Adjust stock quantity (manual correction)
     * @param command - Adjust stock command
     * @returns Promise<Stock> - Updated stock with new quantity
     */
    async adjustStock(command: AdjustStockCommand): Promise<Stock> {
        const response = await $api.post<Stock>('/stocks/adjust', command)
        return response.data
    },

    /**
     * Withdraw stock for internal use
     * @param command - Withdraw stock command
     * @returns Promise<Stock> - Updated stock after withdrawal
     */
    async withdrawStock(command: WithdrawStockCommand): Promise<Stock> {
        const response = await $api.post<Stock>('/stocks/withdraw', command)
        return response.data
    },

    /**
     * Quick sale for POS/Walk-in
     * @param command - Quick sale command
     * @returns Promise<Stock> - Updated stock after sale
     */
    async quickSale(command: QuickSaleCommand): Promise<Stock> {
        const response = await $api.post<Stock>('/stocks/sale', command)
        return response.data
    }
  }
}
