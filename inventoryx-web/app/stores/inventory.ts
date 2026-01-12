import { defineStore } from 'pinia'
import type { Stock } from '~/types/inventory'

export const useInventoryStore = defineStore('inventory', {
  state: () => ({
    stocks: [] as Stock[],
    selectedStock: null as Stock | null,
    loading: false,
    error: null as string | null
  }),

  getters: {
    stocksByLocation: (state) => (locationId: string) => {
      return state.stocks.filter(s => s.locationId === locationId)
    },

    totalAvailableQuantity: (state) => (sku: string) => {
      return state.stocks
        .filter(s => s.sku === sku)
        .reduce((sum, s) => sum + s.availableQuantity, 0)
    }
  },

  actions: {
    async fetchStocks(params?: { sku?: string; locationId?: string }) {
      this.loading = true
      this.error = null
      
      try {
        const config = useRuntimeConfig()
        const baseUrl = config.public.apiBaseUrl
        
        const data = await $fetch(`${baseUrl}/stocks`, {
          params
        })
        this.stocks = data as Stock[]
      } catch (e: any) {
        this.error = e.message
        console.error('Failed to fetch stocks:', e)
      } finally {
        this.loading = false
      }
    },

    async receiveStock(command: any) {
      try {
        const config = useRuntimeConfig()
        const baseUrl = config.public.apiBaseUrl
        
        await $fetch(`${baseUrl}/stocks/receive`, {
          method: 'POST',
          body: command
        })
        
        // Refresh stocks after successful operation
        await this.fetchStocks()
      } catch (e: any) {
        this.error = e.message
        throw e
      }
    }
  }
})
