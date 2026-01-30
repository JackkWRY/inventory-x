<script setup lang="ts">
import type { Stock } from "~/types/inventory";
import InventoryStockMovementHistory from "./StockMovementHistory.vue";
import StockTableActions from "./StockTableActions.vue";
import IconSearch from "~/components/icons/IconSearch.vue";

/**
 * StockList Component
 *
 * Displays a table of stocks with search/filter functionality and action buttons.
 * Emits events for parent component to handle actions.
 */

const { t } = useI18n();

// Props
interface Props {
  stocks: Stock[];
  loading?: boolean;
  locationMap?: Record<string, string>;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
});

// Emits
const emit = defineEmits<{
  receive: [];
  reserve: [stock: Stock];
  withdraw: [stock: Stock];
  quickSale: [stock: Stock];
  release: [stock: Stock];
  confirm: [stock: Stock];
  adjust: [stock: Stock];
  view: [stock: Stock];
  bulkReserve: [stocks: Stock[]];
  bulkAdjust: [stocks: Stock[]];
}>();

// Search state
const searchQuery = ref("");
const stockStatus = ref("all");
const skuInputRef = ref<HTMLInputElement | null>(null);

// Selection state for bulk operations
const selectedIds = ref<Set<string>>(new Set());

// Configuration
const LOW_STOCK_THRESHOLD = 10;

// Computed filtered stocks
const filteredStocks = computed(() => {
  return props.stocks.filter((stock) => {
    const query = searchQuery.value.toLowerCase().trim();
    const locationName = props.locationMap?.[stock.locationId] || "";
    
    const matchesSearch =
      !query ||
      stock.sku.toLowerCase().includes(query) ||
      stock.locationId.toLowerCase().includes(query) ||
      locationName.toLowerCase().includes(query);

    const qty = parseFloat(stock.availableQuantity || "0");
    let matchesStatus = true;
    if (stockStatus.value === "low") {
      matchesStatus = qty > 0 && qty <= LOW_STOCK_THRESHOLD;
    } else if (stockStatus.value === "normal") {
      matchesStatus = qty > LOW_STOCK_THRESHOLD;
    } else if (stockStatus.value === "out") {
      matchesStatus = qty <= 0;
    }

    return matchesSearch && matchesStatus;
  });
});

const hasActiveFilters = computed(() => searchQuery.value || stockStatus.value !== "all");

const formatQuantity = (value: string): string => {
  const num = parseFloat(value || "0");
  return num.toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};

const clearFilters = () => {
  searchQuery.value = "";
  stockStatus.value = "all";
};

const focusSearch = () => {
  skuInputRef.value?.focus();
};

// Selection computed properties
const isAllSelected = computed(() => {
  if (filteredStocks.value.length === 0) return false;
  return filteredStocks.value.every((s) => selectedIds.value.has(s.id));
});

const hasSelection = computed(() => selectedIds.value.size > 0);

const selectedStocks = computed(() => {
  return filteredStocks.value.filter((s) => selectedIds.value.has(s.id));
});

// Selection methods
const toggleSelectAll = () => {
  if (isAllSelected.value) {
    filteredStocks.value.forEach((s) => selectedIds.value.delete(s.id));
  } else {
    filteredStocks.value.forEach((s) => selectedIds.value.add(s.id));
  }
};

const toggleSelect = (id: string) => {
  if (selectedIds.value.has(id)) {
    selectedIds.value.delete(id);
  } else {
    selectedIds.value.add(id);
  }
};

const clearSelection = () => {
  selectedIds.value.clear();
};

// Bulk actions
const handleBulkReserve = () => {
  if (selectedStocks.value.length > 0) {
    emit("bulkReserve", selectedStocks.value);
  }
};

const handleBulkAdjust = () => {
  if (selectedStocks.value.length > 0) {
    emit("bulkAdjust", selectedStocks.value);
  }
};

// History Dialog State
const historyOpen = ref(false);
const historyStockId = ref("");
const historyStockSku = ref("");

const handleViewHistory = (stock: Stock) => {
  historyStockId.value = stock.id;
  historyStockSku.value = stock.sku;
  historyOpen.value = true;
};

// Expose methods for parent components
defineExpose({
  focusSearch,
  clearSelection,
});
</script>

<template>
  <div class="stock-list">
    <!-- Header with Search and Actions -->
    <div class="stock-list__header">
      <div class="stock-list__search">
        <div class="search-field">
          <div class="search-input-wrapper">
            <IconSearch class="search-icon" />
            <input
              id="search-input"
              ref="skuInputRef"
              v-model="searchQuery"
              type="text"
              :placeholder="t('common.search') + '...'"
              class="form-input form-input--with-icon"
            />
          </div>
        </div>
        <div class="search-field">
          <select id="stock-status" v-model="stockStatus" class="form-input">
            <option value="all">{{ t("inventory.stockStatus") }}</option>
            <option value="normal">{{ t("inventory.statusNormal") }}</option>
            <option value="low">{{ t("inventory.statusLow") }}</option>
            <option value="out">{{ t("inventory.statusOut") }}</option>
          </select>
        </div>
        <button
          v-if="hasActiveFilters"
          class="btn btn--secondary btn--clear"
          @click="clearFilters"
        >
          ✕ {{ t("common.clearFilter") }}
        </button>
      </div>
      <div class="stock-list__actions">
        <button class="btn btn--primary" @click="emit('receive')">
          + {{ t("inventory.receiveStock") }}
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <CommonTableSkeleton v-if="loading" :rows="5" :columns="6" show-header />

    <!-- Empty State -->
    <div v-else-if="filteredStocks.length === 0" class="stock-list__empty">
      <p>{{ t("messages.noData") }}</p>
    </div>

    <!-- Bulk Action Bar -->
    <CommonBulkActionBar
      v-if="hasSelection && !loading"
      :selected-count="selectedIds.size"
      @clear="clearSelection"
      @bulk-reserve="handleBulkReserve"
      @bulk-adjust="handleBulkAdjust"
    />

    <!-- Stock Table -->
    <table v-if="!loading && filteredStocks.length > 0" class="data-table">
      <thead>
        <tr>
          <th class="checkbox-col">
            <input
              type="checkbox"
              :checked="isAllSelected"
              :indeterminate="hasSelection && !isAllSelected"
              @change="toggleSelectAll"
              :title="t('bulk.selectAll')"
              class="checkbox"
            />
          </th>
          <th>{{ t("inventory.sku") }}</th>
          <th>{{ t("inventory.location") }}</th>
          <th class="text-right">{{ t("inventory.availableQuantity") }}</th>
          <th class="text-right">{{ t("inventory.reservedQuantity") }}</th>
          <th>{{ t("inventory.unitOfMeasure") }}</th>
          <th class="text-center">{{ t("common.actions") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="stock in filteredStocks"
          :key="stock.id"
          :class="{ 'row--selected': selectedIds.has(stock.id) }"
        >
          <td class="checkbox-col">
            <input
              type="checkbox"
              :checked="selectedIds.has(stock.id)"
              @change="toggleSelect(stock.id)"
              class="checkbox"
            />
          </td>
          <td>
            <NuxtLink :to="`/inventory/${stock.id}`" class="link--primary">
              {{ stock.sku }}
            </NuxtLink>
          </td>
          <td>{{ locationMap?.[stock.locationId] || stock.locationId }}</td>
          <td class="text-right">
            <span class="quantity quantity--available">
              {{ formatQuantity(stock.availableQuantity) }}
            </span>
          </td>
          <td class="text-right">
            <span class="quantity quantity--reserved">
              {{ formatQuantity(stock.reservedQuantity) }}
            </span>
          </td>
          <td>{{ stock.unitOfMeasure }}</td>
          <td class="text-center">
            <StockTableActions
              :stock="stock"
              @withdraw="emit('withdraw', $event)"
              @quick-sale="emit('quickSale', $event)"
              @reserve="emit('reserve', $event)"
              @release="emit('release', $event)"
              @confirm="emit('confirm', $event)"
              @adjust="emit('adjust', $event)"
              @view-history="handleViewHistory"
            />
          </td>
        </tr>
      </tbody>
    </table>

    <!-- History Dialog -->
    <InventoryStockMovementHistory
      :open="historyOpen"
      :stock-id="historyStockId"
      :stock-sku="historyStockSku"
      @close="historyOpen = false"
    />
  </div>
</template>

<style scoped>
.stock-list {
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
  transition: var(--theme-transition);
}

.stock-list__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  gap: 1rem;
  flex-wrap: wrap;
}

.stock-list__search {
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

.form-input--with-icon {
  padding-left: 2.5rem;
  min-width: 280px;
}

.stock-list__actions {
  flex-shrink: 0;
}

.stock-list__empty {
  padding: 3rem;
  text-align: center;
  color: var(--color-text-secondary);
}

/* Checkbox column */
.checkbox-col {
  width: 40px;
  text-align: center;
}

.checkbox {
  width: 1rem;
  height: 1rem;
  cursor: pointer;
  accent-color: var(--color-primary);
}

/* Selected row */
.row--selected {
  background: var(--color-surface-hover) !important;
}

/* Quantity styling */
.quantity {
  font-family: "SF Mono", "Consolas", monospace;
  font-weight: 500;
}

.quantity--available {
  color: #0d9488;
}

.quantity--reserved {
  color: #9333ea;
}

/* Link styling */
.link--primary {
  font-family: "SF Mono", "Consolas", monospace;
  font-weight: 500;
  color: var(--color-primary);
  text-decoration: none;
  transition: all 0.2s;
}

.link--primary:hover {
  text-decoration: underline;
  color: var(--color-primary-hover);
}

.text-right {
  text-align: right;
}

.text-center {
  text-align: center;
}
</style>
