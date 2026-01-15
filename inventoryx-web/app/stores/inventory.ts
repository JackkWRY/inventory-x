import { defineStore } from 'pinia'
import type {
  Stock,
  ReceiveStockCommand,
  ReserveStockCommand,
  ReleaseReservationCommand,
  ConfirmReservationCommand,
  AdjustStockCommand,
  PaginationParams,
  PagedStockResponse
} from '~/types/inventory'
import { useInventoryApi } from '~/composables/api/useInventoryApi'

/**
 * Inventory Store
 *
 * Centralized state management for inventory operations.
 * Uses useInventoryApi composable for all Backend communication.
 *
 * @example
 * ```ts
 * const store = useInventoryStore()
 * await store.fetchStocks({ sku: 'PROD-001' })
 * console.log(store.stocks)
 * ```
 */
export const useInventoryStore = defineStore('inventory', {
  // ============================================
  // State
  // ============================================
  state: () => ({
    /** List of all stocks */
    stocks: [] as Stock[],
    /** Currently selected stock for detail view */
    selectedStock: null as Stock | null,
    /** Loading state for async operations */
    loading: false,
    /** Error message from last failed operation */
    error: null as string | null,
    /** Pagination state */
    pagination: {
      currentPage: 0,
      pageSize: 20,
      totalElements: 0,
      totalPages: 0,
      isFirst: true,
      isLast: true
    }
  }),

  // ============================================
  // Getters
  // ============================================
  getters: {
    /**
     * Filter stocks by location
     */
    stocksByLocation: (state) => (locationId: string): Stock[] => {
      return state.stocks.filter(s => s.locationId === locationId)
    },

    /**
     * Filter stocks by SKU
     */
    stocksBySku: (state) => (sku: string): Stock[] => {
      return state.stocks.filter(s => s.sku === sku)
    },

    /**
     * Calculate total available quantity for a SKU across all locations
     */
    totalAvailableQuantity: (state) => (sku: string): number => {
      return state.stocks
        .filter(s => s.sku === sku)
        .reduce((sum, s) => sum + parseFloat(s.availableQuantity || '0'), 0)
    },

    /**
     * Calculate total reserved quantity for a SKU across all locations
     */
    totalReservedQuantity: (state) => (sku: string): number => {
      return state.stocks
        .filter(s => s.sku === sku)
        .reduce((sum, s) => sum + parseFloat(s.reservedQuantity || '0'), 0)
    },

    /**
     * Check if store is in loading state
     */
    isLoading: (state): boolean => state.loading,

    /**
     * Check if store has error
     */
    hasError: (state): boolean => state.error !== null
  },

  // ============================================
  // Actions
  // ============================================
  actions: {
    /**
     * Clear current error
     */
    clearError() {
      this.error = null
    },

    /**
     * Clear selected stock
     */
    clearSelection() {
      this.selectedStock = null
    },

    /**
     * Set selected stock
     */
    selectStock(stock: Stock | null) {
      this.selectedStock = stock
    },

    // --------------------------------------------
    // Query Operations
    // --------------------------------------------

    /**
     * Fetch all stocks with optional filters
     * @param params - Optional filter parameters (sku, locationId)
     */
    async fetchStocks(params?: { sku?: string; locationId?: string }) {
      this.loading = true
      this.error = null

      try {
        const api = useInventoryApi()
        this.stocks = await api.getStocks(params)
      } catch (e: unknown) {
        this.error = e instanceof Error ? e.message : 'Failed to fetch stocks'
        console.error('[InventoryStore] fetchStocks error:', e)
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch stocks with pagination (BEST PRACTICE)
     * @param params - Pagination parameters (page, size)
     */
    async fetchStocksPaged(params?: PaginationParams) {
      this.loading = true
      this.error = null

      try {
        const api = useInventoryApi()
        const response = await api.getStocksPaged({
          page: params?.page ?? this.pagination.currentPage,
          size: params?.size ?? this.pagination.pageSize
        })

        // Update stocks
        this.stocks = response.content

        // Update pagination state
        this.pagination = {
          currentPage: response.page,
          pageSize: response.size,
          totalElements: response.totalElements,
          totalPages: response.totalPages,
          isFirst: response.first,
          isLast: response.last
        }
      } catch (e: unknown) {
        this.error = e instanceof Error ? e.message : 'Failed to fetch stocks'
        console.error('[InventoryStore] fetchStocksPaged error:', e)
      } finally {
        this.loading = false
      }
    },

    /**
     * Change page
     */
    async changePage(page: number) {
      await this.fetchStocksPaged({ page, size: this.pagination.pageSize })
    },

    /**
     * Change page size and reset to first page
     */
    async changePageSize(size: number) {
      await this.fetchStocksPaged({ page: 0, size })
    },

    /**
     * Fetch a single stock by ID
     * @param id - Stock ID
     */
    async fetchStockById(id: string) {
      this.loading = true
      this.error = null

      try {
        const api = useInventoryApi()
        this.selectedStock = await api.getStockById(id)
      } catch (e: unknown) {
        this.error = e instanceof Error ? e.message : 'Failed to fetch stock'
        console.error('[InventoryStore] fetchStockById error:', e)
      } finally {
        this.loading = false
      }
    },

    // --------------------------------------------
    // Command Operations
    // --------------------------------------------

    /**
     * Receive stock into warehouse
     * @param command - Receive stock command
     * @returns Promise<Stock> - The updated stock
     */
    async receiveStock(command: ReceiveStockCommand): Promise<Stock | null> {
      this.loading = true
      this.error = null

      try {
        const api = useInventoryApi()
        const result = await api.receiveStock(command)

        // Refresh stocks after successful operation
        await this.fetchStocks()

        return result
      } catch (e: unknown) {
        this.error = e instanceof Error ? e.message : 'Failed to receive stock'
        console.error('[InventoryStore] receiveStock error:', e)
        throw e
      } finally {
        this.loading = false
      }
    },

    /**
     * Reserve stock for an order
     * @param command - Reserve stock command
     * @returns Promise<Stock> - The updated stock
     */
    async reserveStock(command: ReserveStockCommand): Promise<Stock | null> {
      this.loading = true
      this.error = null

      try {
        const api = useInventoryApi()
        const result = await api.reserveStock(command)

        // Refresh stocks after successful operation
        await this.fetchStocks()

        return result
      } catch (e: unknown) {
        this.error = e instanceof Error ? e.message : 'Failed to reserve stock'
        console.error('[InventoryStore] reserveStock error:', e)
        throw e
      } finally {
        this.loading = false
      }
    },

    /**
     * Release a reservation (cancel order)
     * @param command - Release reservation command
     * @returns Promise<Stock> - The updated stock
     */
    async releaseReservation(command: ReleaseReservationCommand): Promise<Stock | null> {
      this.loading = true
      this.error = null

      try {
        const api = useInventoryApi()
        const result = await api.releaseReservation(command)

        // Refresh stocks after successful operation
        await this.fetchStocks()

        return result
      } catch (e: unknown) {
        this.error = e instanceof Error ? e.message : 'Failed to release reservation'
        console.error('[InventoryStore] releaseReservation error:', e)
        throw e
      } finally {
        this.loading = false
      }
    },

    /**
     * Confirm a reservation (complete sale)
     * @param command - Confirm reservation command
     * @returns Promise<Stock> - The updated stock
     */
    async confirmReservation(command: ConfirmReservationCommand): Promise<Stock | null> {
      this.loading = true
      this.error = null

      try {
        const api = useInventoryApi()
        const result = await api.confirmReservation(command)

        // Refresh stocks after successful operation
        await this.fetchStocks()

        return result
      } catch (e: unknown) {
        this.error = e instanceof Error ? e.message : 'Failed to confirm reservation'
        console.error('[InventoryStore] confirmReservation error:', e)
        throw e
      } finally {
        this.loading = false
      }
    },

    /**
     * Adjust stock quantity (manual correction)
     * @param command - Adjust stock command
     * @returns Promise<Stock> - The updated stock
     */
    async adjustStock(command: AdjustStockCommand): Promise<Stock | null> {
      this.loading = true
      this.error = null

      try {
        const api = useInventoryApi()
        const result = await api.adjustStock(command)

        // Refresh stocks after successful operation
        await this.fetchStocks()

        return result
      } catch (e: unknown) {
        this.error = e instanceof Error ? e.message : 'Failed to adjust stock'
        console.error('[InventoryStore] adjustStock error:', e)
        throw e
      } finally {
        this.loading = false
      }
    }
  }
})
