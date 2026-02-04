import { defineStore } from 'pinia';
import type { Product, CreateProductCommand, UpdateProductCommand, ProductPageResponse } from '~/types/product';
import { useProductApi } from '~/composables/api/useProductApi';

export const useProductStore = defineStore('product', () => {
    // State
    const products = ref<Product[]>([]);
    const selectedProduct = ref<Product | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);
    const totalRecords = ref(0);

    const { t } = useI18n();

    // Actions
    async function fetchProducts(page = 0, size = 10, search = '') {
        loading.value = true;
        error.value = null;
        try {
            const api = useProductApi();
            const response = await api.getProducts({ page, size, search, sort: 'createdAt,desc' });
            products.value = response.content;
            totalRecords.value = response.totalElements;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to fetch products';
        } finally {
            loading.value = false;
        }
    }

    async function createProduct(payload: CreateProductCommand) {
        loading.value = true;
        error.value = null;
        try {
            const api = useProductApi();
            await api.createProduct(payload);
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
        try {
            const api = useProductApi();
            await api.updateProduct(id, payload);
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
        try {
            const api = useProductApi();
            const product = await api.getProductById(id);
            selectedProduct.value = product;
            return product;
        } catch (err: any) {
             error.value = err.response?.data?.message || 'Failed to get product';
        } finally {
            loading.value = false;
        }
    }

    async function searchProducts(query: string): Promise<Product[]> {
        try {
            const api = useProductApi();
            const response = await api.getProducts({ 
                search: query, 
                size: 20, 
                sort: 'name,asc' 
            });
            return response.content;
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
