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
  <div class="product-list">
    <!-- Header with Search -->
    <div class="product-list__header">
      <div class="product-list__search">
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
      <div class="product-list__actions">
        <!-- Create Product Button (Moved here) -->
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
    <table v-if="!loading && products.length > 0" class="product-table">
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
        <tr v-for="product in products" :key="product.id" class="hover-row">
          <td>
             <span class="product-sku">{{ product.sku }}</span>
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
              class="action-btn action-btn--primary"
              @click="$emit('edit', product)"
              :title="t('common.edit')"
            >
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
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
    <div v-if="loading" class="product-list__loading">
      {{ t("common.loading") }}
    </div>

    <!-- Empty State -->
    <div v-if="!loading && products.length === 0" class="product-list__empty">
      <p>{{ t("messages.noData") }}</p>
    </div>
  </div>
</template>

<style scoped>
.product-list {
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
  transition: var(--theme-transition);
}

.product-list__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  gap: 1rem;
  flex-wrap: wrap;
}

.product-list__search {
  display: flex;
  gap: 1rem;
  align-items: flex-end;
  flex-wrap: wrap;
}

.search-field {
  display: flex;
  flex-direction: column;
}

.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  color: var(--color-text-secondary);
  pointer-events: none;
}

.input {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 0.875rem;
  min-width: 280px;
  transition: border-color 0.2s, background-color 0.3s;
  background: var(--color-surface);
  color: var(--color-text-primary);
}

.input.with-icon {
  padding-left: 2.5rem;
}

.input:focus {
  outline: none;
  border-color: #4285f4;
}

.product-table {
  width: 100%;
  border-collapse: collapse;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.product-table th {
  padding: 0.875rem 1rem;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--color-text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  background: var(--glass-bg-strong);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--color-border);
}

.product-table td {
  padding: 0.875rem 1rem;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.875rem;
  color: var(--color-text-primary);
  transition: all 0.2s ease;
}

.hover-row {
  transition: all 0.2s ease;
}

.hover-row:hover {
  background: var(--color-surface-hover);
}

.hover-row:hover td:first-child {
  box-shadow: inset 3px 0 0 var(--color-primary);
}

.product-sku {
  font-family: "SF Mono", "Consolas", monospace;
  font-weight: 500;
  color: var(--color-primary);
}

.text-center {
  text-align: center;
}

.text-muted {
  color: var(--color-text-secondary);
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
}

.badge--surface {
  background: var(--color-background);
  color: var(--color-text-primary);
  border: 1px solid var(--color-border);
}

/* Action Button Styles */
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  font-size: 0.75rem;
  font-weight: 500;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.action-btn--primary {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.action-btn--primary:hover {
  background: rgba(59, 130, 246, 0.2);
}

/* Loading/Empty States */
.product-list__loading,
.product-list__empty {
  padding: 3rem;
  text-align: center;
  color: var(--color-text-secondary);
}

/* Keyboard hint */
.kbd-hint {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 1.25rem;
  height: 1.25rem;
  padding: 0 0.25rem;
  font-family: monospace;
  font-size: 0.7rem;
  font-weight: 500;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  color: var(--color-text-secondary);
  margin-left: 0.25rem;
}
</style>
