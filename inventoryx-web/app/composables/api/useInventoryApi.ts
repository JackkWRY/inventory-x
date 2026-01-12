export const useInventoryApi = () => {
  const config = useRuntimeConfig()
  const baseUrl = config.public.apiBaseUrl

  return {
    async getStocks(params?: { sku?: string; locationId?: string }) {
      return await $fetch(`${baseUrl}/stocks`, {
        params
      })
    },

    async getStockById(id: string) {
      return await $fetch(`${baseUrl}/stocks/${id}`)
    },

    async receiveStock(command: {
      sku: string
      locationId: string
      quantity: string
      reason: string
      receivedBy: string
    }) {
      return await $fetch(`${baseUrl}/stocks/receive`, {
        method: 'POST',
        body: command
      })
    },

    async reserveStock(command: {
      sku: string
      locationId: string
      quantity: string
      orderId: string
    }) {
      return await $fetch(`${baseUrl}/stocks/reserve`, {
        method: 'POST',
        body: command
      })
    }
  }
}
