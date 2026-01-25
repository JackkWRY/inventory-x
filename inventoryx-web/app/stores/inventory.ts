import { defineStore } from 'pinia'
import type {
  Stock,
  ReceiveStockCommand,
  ReserveStockCommand,
  ReleaseReservationCommand,
  ConfirmReservationCommand,
  AdjustStockCommand,
  WithdrawStockCommand,
  QuickSaleCommand,
  PaginationParams
} from '~/types/inventory'
import { useInventoryApi } from '~/composables/api/useInventoryApi'

/**
 * Inventory Store
 *
 * Centralized state management for inventory operations.
 * Uses useInventoryApi composable for all Backend communication.
 */
export const useInventoryStore = defineStore('inventory', () => {
  // ============================================
  // State
  // ============================================
  const stocks = ref<Stock[]>([])
  const selectedStock = ref<Stock | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const pagination = ref({
    currentPage: 0,
    pageSize: 20,
    totalElements: 0,
    totalPages: 0,
    isFirst: true,
    isLast: true
  })

  // ============================================
  // Getters
  // ============================================
  const stocksByLocation = computed(() => (locationId: string): Stock[] => {
    return stocks.value.filter(s => s.locationId === locationId)
  })

  const stocksBySku = computed(() => (sku: string): Stock[] => {
    return stocks.value.filter(s => s.sku === sku)
  })

  const totalAvailableQuantity = computed(() => (sku: string): number => {
    return stocks.value
      .filter(s => s.sku === sku)
      .reduce((sum, s) => sum + parseFloat(s.availableQuantity || '0'), 0)
  })

  const totalReservedQuantity = computed(() => (sku: string): number => {
    return stocks.value
      .filter(s => s.sku === sku)
      .reduce((sum, s) => sum + parseFloat(s.reservedQuantity || '0'), 0)
  })

  const isLoading = computed(() => loading.value)
  const hasError = computed(() => error.value !== null)

  // ============================================
  // Actions
  // ============================================

  function clearError() {
    error.value = null
  }

  function clearSelection() {
    selectedStock.value = null
  }

  function selectStock(stock: Stock | null) {
    selectedStock.value = stock
  }

  // --------------------------------------------
  // Query Operations
  // --------------------------------------------

  async function fetchStocks(params?: { sku?: string; locationId?: string }) {
    loading.value = true
    error.value = null

    try {
      const api = useInventoryApi()
      stocks.value = await api.getStocks(params)
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Failed to fetch stocks'
      console.error('[InventoryStore] fetchStocks error:', e)
    } finally {
      loading.value = false
    }
  }

  async function fetchStocksPaged(params?: PaginationParams) {
    loading.value = true
    error.value = null

    try {
      const api = useInventoryApi()
      const response = await api.getStocksPaged({
        page: params?.page ?? pagination.value.currentPage,
        size: params?.size ?? pagination.value.pageSize
      })

      stocks.value = response.content
      pagination.value = {
        currentPage: response.page,
        pageSize: response.size,
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        isFirst: response.first,
        isLast: response.last
      }
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Failed to fetch stocks'
      console.error('[InventoryStore] fetchStocksPaged error:', e)
    } finally {
      loading.value = false
    }
  }

  async function changePage(page: number) {
    await fetchStocksPaged({ page, size: pagination.value.pageSize })
  }

  async function changePageSize(size: number) {
    await fetchStocksPaged({ page: 0, size })
  }

  async function fetchStockById(id: string) {
    loading.value = true
    error.value = null

    try {
      const api = useInventoryApi()
      selectedStock.value = await api.getStockById(id)
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Failed to fetch stock'
      console.error('[InventoryStore] fetchStockById error:', e)
    } finally {
      loading.value = false
    }
  }

  // --------------------------------------------
  // Command Operations
  // --------------------------------------------

  async function receiveStock(command: ReceiveStockCommand): Promise<Stock | null> {
    loading.value = true
    error.value = null

    try {
      const api = useInventoryApi()
      const result = await api.receiveStock(command)
      await fetchStocks()
      return result
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Failed to receive stock'
      console.error('[InventoryStore] receiveStock error:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function reserveStock(command: ReserveStockCommand): Promise<Stock | null> {
    loading.value = true
    error.value = null

    try {
      const api = useInventoryApi()
      const result = await api.reserveStock(command)
      await fetchStocks()
      return result
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Failed to reserve stock'
      console.error('[InventoryStore] reserveStock error:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function releaseReservation(command: ReleaseReservationCommand): Promise<Stock | null> {
    loading.value = true
    error.value = null

    try {
      const api = useInventoryApi()
      const result = await api.releaseReservation(command)
      await fetchStocks()
      return result
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Failed to release reservation'
      console.error('[InventoryStore] releaseReservation error:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function confirmReservation(command: ConfirmReservationCommand): Promise<Stock | null> {
    loading.value = true
    error.value = null

    try {
      const api = useInventoryApi()
      const result = await api.confirmReservation(command)
      await fetchStocks()
      return result
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Failed to confirm reservation'
      console.error('[InventoryStore] confirmReservation error:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function adjustStock(command: AdjustStockCommand): Promise<Stock | null> {
    loading.value = true
    error.value = null

    try {
      const api = useInventoryApi()
      const result = await api.adjustStock(command)
      await fetchStocks()
      return result
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Failed to adjust stock'
      console.error('[InventoryStore] adjustStock error:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function withdrawStock(command: WithdrawStockCommand): Promise<Stock | null> {
    loading.value = true
    error.value = null

    try {
      const api = useInventoryApi()
      const result = await api.withdrawStock(command)
      await fetchStocks()
      return result
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Failed to withdraw stock'
      console.error('[InventoryStore] withdrawStock error:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function quickSale(command: QuickSaleCommand): Promise<Stock | null> {
    loading.value = true
    error.value = null

    try {
      const api = useInventoryApi()
      const result = await api.quickSale(command)
      await fetchStocks()
      return result
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Failed to complete sale'
      console.error('[InventoryStore] quickSale error:', e)
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    stocks,
    selectedStock,
    loading,
    error,
    pagination,
    stocksByLocation,
    stocksBySku,
    totalAvailableQuantity,
    totalReservedQuantity,
    isLoading,
    hasError,
    clearError,
    clearSelection,
    selectStock,
    fetchStocks,
    fetchStocksPaged,
    changePage,
    changePageSize,
    fetchStockById,
    receiveStock,
    reserveStock,
    releaseReservation,
    confirmReservation,
    adjustStock,
    withdrawStock,
    quickSale
  }
})
