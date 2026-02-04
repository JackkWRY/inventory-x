import type { Product, CreateProductCommand, UpdateProductCommand, ProductPageResponse } from '~/types/product'

/**
 * Product API Composable
 * 
 * Centralizes all Product-related API calls.
 */
export const useProductApi = () => {
  const { $api } = useNuxtApp()

  return {
    /**
     * Get products with pagination and search
     */
    async getProducts(params: { page?: number; size?: number; search?: string; sort?: string }): Promise<ProductPageResponse> {
        const queryParams: any = {
            page: params.page ?? 0,
            size: params.size ?? 10,
            sort: params.sort ?? 'createdAt,desc'
        }
        if (params.search) queryParams.search = params.search

        const response = await $api.get<ProductPageResponse>('/products', { params: queryParams })
        return response.data
    },

    /**
     * Get a single product by ID
     */
    async getProductById(id: string): Promise<Product> {
        const response = await $api.get<Product>(`/products/${id}`)
        return response.data
    },

    /**
     * Create a new product
     */
    async createProduct(command: CreateProductCommand): Promise<Product> {
        const response = await $api.post<Product>('/products', command)
        return response.data
    },

    /**
     * Update an existing product
     */
    async updateProduct(id: string, command: UpdateProductCommand): Promise<Product> {
        const response = await $api.put<Product>(`/products/${id}`, command)
        return response.data
    }
  }
}
