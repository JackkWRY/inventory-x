import { defineStore } from 'pinia';
import type { Product, CreateProductCommand, UpdateProductCommand, ProductPageResponse } from '~/types/product';

export const useProductStore = defineStore('product', () => {
    // State
    const products = ref<Product[]>([]);
    const selectedProduct = ref<Product | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);
    const totalRecords = ref(0);

    // Actions
    async function fetchProducts(page = 0, size = 10, search = '') {
        loading.value = true;
        error.value = null;
        const { $api } = useNuxtApp();
        try {
            const params: any = { page, size, sort: 'createdAt,desc' };
            if (search) params.search = search;
            
            const response = await $api.get<ProductPageResponse>('/products', { params });
            products.value = response.data.content;
            totalRecords.value = response.data.totalElements;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to fetch products';
        } finally {
            loading.value = false;
        }
    }

    async function createProduct(payload: CreateProductCommand) {
        loading.value = true;
        error.value = null;
        const { $api } = useNuxtApp();
        try {
            await $api.post<Product>('/products', payload);
            await fetchProducts(); // Refresh list
        } catch (err: any) {
             const message = err.response?.data?.message || 'Failed to create product';
             error.value = message;
             throw new Error(message);
        } finally {
            loading.value = false;
        }
    }

    async function updateProduct(id: string, payload: UpdateProductCommand) {
        loading.value = true;
        error.value = null;
        const { $api } = useNuxtApp();
        try {
            await $api.put<Product>(`/products/${id}`, payload);
            await fetchProducts(); // Refresh list
        } catch (err: any) {
             const message = err.response?.data?.message || 'Failed to update product';
             error.value = message;
            throw new Error(message);
        } finally {
            loading.value = false;
        }
    }

    async function getProduct(id: string) {
        loading.value = true;
        const { $api } = useNuxtApp();
        try {
            const response = await $api.get<Product>(`/products/${id}`);
            selectedProduct.value = response.data;
            return response.data;
        } catch (err: any) {
             error.value = err.response?.data?.message || 'Failed to get product';
        } finally {
            loading.value = false;
        }
    }

    async function searchProducts(query: string): Promise<Product[]> {
        const { $api } = useNuxtApp();
        try {
            const response = await $api.get<ProductPageResponse>('/products', { 
                params: { 
                    search: query, 
                    size: 20, 
                    sort: 'name,asc' 
                } 
            });
            return response.data.content;
        } catch (err) {
            console.error("Failed to search products", err);
            return [];
        }
    }

    return {
        products,
        selectedProduct,
        loading,
        error,
        totalRecords,
        fetchProducts,
        createProduct,
        updateProduct,
        getProduct,
        searchProducts
    };
});
