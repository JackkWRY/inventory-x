<script setup lang="ts">
import type { Product } from "~/types/product";
import { useAuthStore } from "~/stores/auth";

const props = defineProps<{
  products: Product[];
  loading: boolean;
  search?: string;
}>();

const emit = defineEmits(["edit", "refresh", "update:search", "create"]);
const authStore = useAuthStore();
const { t } = useI18n();

// Check if user has edit permission
const canEdit = computed(() => {
  return authStore.hasRole("ADMIN") || authStore.hasRole("MANAGER");
});

const formatCurrency = (amount: number, currency: string) => {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: currency,
  }).format(amount);
};
</script>

<template>
  <div class="list-container">
    <!-- Header with Search -->
    <div class="list-header">
      <div class="list-header__search">
        <div class="search-field">
          <div class="search-input-wrapper">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="search-icon"
            >
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
            <input
              id="search-input"
              type="text"
              :value="search"
              :placeholder="t('common.search') + '...'"
              class="input with-icon"
              @input="$emit('update:search', ($event.target as HTMLInputElement).value)"
            />
          </div>
        </div>
      </div>
      <div class="list-header__actions">
         <button
          v-if="canEdit"
          class="btn btn--primary"
          @click="$emit('create')"
        >
          + {{ t("products.createProduct") }}
        </button>
      </div>
    </div>

    <!-- Product Table -->
    <table v-if="!loading && products.length > 0" class="data-table">
      <thead>
        <tr>
          <th>{{ t("products.productCode") }}</th>
          <th>{{ t("products.productName") }}</th>
          <th>{{ t("products.category") }}</th>
          <th>{{ t("products.price") }}</th>
          <th>{{ t("common.unit") || "Unit" }}</th>
          <th v-if="canEdit" class="text-center">{{ t("common.actions") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in products" :key="product.id">
          <td>
             <span class="text-mono">{{ product.sku }}</span>
          </td>
          <td class="font-medium">{{ product.name }}</td>
          <td>
            <span v-if="product.category" class="badge badge--surface">
              {{ product.category }}
            </span>
            <span v-else class="text-muted">-</span>
          </td>
          <td>{{ formatCurrency(product.price, product.currency) }}</td>
          <td>{{ product.unitOfMeasure }}</td>
          <td v-if="canEdit" class="text-center">
            <button
              class="btn btn--small btn--ghost"
              @click="$emit('edit', product)"
              :title="t('common.edit')"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                 <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                 <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
              </svg>
              {{ t("common.edit") }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      {{ t("common.loading") }}
    </div>

    <!-- Empty State -->
    <div v-if="!loading && products.length === 0" class="empty-state">
      <p>{{ t("messages.noData") }}</p>
    </div>
  </div>
</template>

<style scoped>
/* All base styles now come from global main.css */
/* Only component-specific overrides remain here */
</style>
