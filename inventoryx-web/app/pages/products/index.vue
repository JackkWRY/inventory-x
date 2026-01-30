<script setup lang="ts">
import { storeToRefs } from "pinia";
import { useProductStore } from "~/stores/product";
import { useAuthStore } from "~/stores/auth";
import type { Product, CreateProductCommand } from "~/types/product";
import ProductList from "~/components/product/ProductList.vue";
import ProductForm from "~/components/product/ProductForm.vue";

definePageMeta({
  title: "Product Catalog",
  middleware: ["auth"],
});

const { t } = useI18n();
// Stores
const productStore = useProductStore();
const authStore = useAuthStore();
const { products, loading, error, totalRecords } = storeToRefs(productStore);

// State
const isFormOpen = ref(false);
const isEditMode = ref(false);
const selectedProduct = ref<Product | null>(null);
const searchQuery = ref("");
let searchTimeout: NodeJS.Timeout | null = null;

// Pagination
const currentPage = ref(0);
const pageSize = ref(10);

// Check Permissions
const canManageProducts = computed(() => {
  return authStore.hasRole("ADMIN") || authStore.hasRole("MANAGER");
});

// Lifecycle
onMounted(() => {
  fetchData();
});

// Watch Search
watch(searchQuery, () => {
  currentPage.value = 0;
  if (searchTimeout) clearTimeout(searchTimeout);
  searchTimeout = setTimeout(() => {
    fetchData();
  }, 300);
});

const fetchData = () => {
  productStore.fetchProducts(currentPage.value, pageSize.value, searchQuery.value);
};

// Handlers
const handleCreate = () => {
  selectedProduct.value = null;
  isEditMode.value = false;
  isFormOpen.value = true;
};

const handleEdit = (product: Product) => {
  selectedProduct.value = product;
  isEditMode.value = true;
  isFormOpen.value = true;
};

const handleSubmit = async (values: CreateProductCommand) => {
  try {
    if (isEditMode.value && selectedProduct.value) {
      await productStore.updateProduct(selectedProduct.value.id, values);
    } else {
      await productStore.createProduct(values);
    }
    isFormOpen.value = false;
    // Toast success handled globally or add toast here
  } catch (err) {
    // Error handled in store
  }
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  fetchData();
};

const handlePageSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 0;
  fetchData();
};
</script>

<template>
  <div class="product-page">
    <!-- Header -->
    <header class="page-header">
      <div class="page-header__content">
        <h1 class="page-header__title">{{ t("products.title") }}</h1>
        <p class="page-header__subtitle">{{ t("products.subtitle") }}</p>
      </div>

      <div class="page-header__actions">
        <NuxtLink to="/" class="btn btn--back">
          ← {{ t("common.back") }}
        </NuxtLink>
      </div>
    </header>

    <!-- Error Banner -->
    <CommonErrorBanner
      v-if="error"
      :message="error"
      :title="t('common.error')"
      retryable
      @retry="fetchData"
      @dismiss="productStore.error = null"
    />

    <!-- List -->
    <ProductList
      v-model:search="searchQuery"
      :products="products"
      :loading="loading"
      @edit="handleEdit"
      @refresh="fetchData"
      @create="handleCreate"
    />

    <!-- Pagination -->
    <CommonPagination
      :current-page="currentPage"
      :total-items="totalRecords"
      :total-pages="Math.ceil(totalRecords / pageSize)"
      :page-size="pageSize"
      @page-change="handlePageChange"
      @page-size-change="handlePageSizeChange"
    />

    <!-- Form Modal -->
    <ProductForm
      :is-open="isFormOpen"
      :is-edit-mode="isEditMode"
      :initial-data="selectedProduct"
      :loading="loading"
      @submit="handleSubmit"
      @cancel="isFormOpen = false"
    />
  </div>
</template>

<style scoped>
.product-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.5rem;
  gap: 1rem;
  flex-wrap: wrap;
}

.page-header__content {
  flex: 1;
}

.page-header__title {
  font-size: 1.75rem;
  font-weight: 500;
  color: var(--color-text-primary);
  margin: 0 0 0.25rem 0;
}

.page-header__subtitle {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  margin: 0;
}

.page-header__actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-shrink: 0;
}
</style>
