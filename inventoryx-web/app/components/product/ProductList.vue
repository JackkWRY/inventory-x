<script setup lang="ts">
import type { Product } from "~/types/product";
import { useAuthStore } from "~/stores/auth";
import IconSearch from "~/components/icons/IconSearch.vue";
import IconEdit from "~/components/icons/IconEdit.vue";

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
            <IconSearch class="search-icon" />
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
              <IconEdit />
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
