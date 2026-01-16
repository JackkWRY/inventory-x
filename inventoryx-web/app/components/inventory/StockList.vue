<script setup lang="ts">
import type { Stock } from "~/types/inventory";

/**
 * StockList Component
 *
 * Displays a table of stocks with search/filter functionality and action buttons.
 * Emits events for parent component to handle actions.
 *
 * @example
 * <StockList
 *   :stocks="stocks"
 *   :loading="loading"
 *   @receive="handleReceive"
 *   @reserve="handleReserve"
 * />
 */

// i18n
const { t } = useI18n();

// Props
interface Props {
  /** List of stocks to display */
  stocks: Stock[];
  /** Loading state */
  loading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
});

// Emits
const emit = defineEmits<{
  /** Triggered when user clicks receive stock button */
  receive: [];
  /** Triggered when user clicks reserve button for a stock */
  reserve: [stock: Stock];
  /** Triggered when user clicks withdraw button for a stock */
  withdraw: [stock: Stock];
  /** Triggered when user clicks quick sale button for a stock */
  quickSale: [stock: Stock];
  /** Triggered when user clicks release button for a stock */
  release: [stock: Stock];
  /** Triggered when user clicks confirm button for a stock */
  confirm: [stock: Stock];
  /** Triggered when user clicks adjust button for a stock */
  adjust: [stock: Stock];
  /** Triggered when user clicks view details for a stock */
  view: [stock: Stock];
  /** Triggered when bulk reserve is requested */
  bulkReserve: [stocks: Stock[]];
  /** Triggered when bulk adjust is requested */
  bulkAdjust: [stocks: Stock[]];
}>();

// Search state
const searchSku = ref("");
const searchLocation = ref("");
const stockStatus = ref("all"); // 'all' | 'low' | 'normal' | 'out'

// Refs for keyboard shortcuts
const skuInputRef = ref<HTMLInputElement | null>(null);

// Selection state for bulk operations
const selectedIds = ref<Set<string>>(new Set());

// Configuration
const LOW_STOCK_THRESHOLD = 10;

// Computed filtered stocks
const filteredStocks = computed(() => {
  return props.stocks.filter((stock) => {
    // SKU filter
    const matchesSku =
      !searchSku.value ||
      stock.sku.toLowerCase().includes(searchSku.value.toLowerCase());

    // Location filter
    const matchesLocation =
      !searchLocation.value ||
      stock.locationId
        .toLowerCase()
        .includes(searchLocation.value.toLowerCase());

    // Stock status filter
    const qty = parseFloat(stock.availableQuantity || "0");
    let matchesStatus = true;
    if (stockStatus.value === "low") {
      matchesStatus = qty > 0 && qty <= LOW_STOCK_THRESHOLD;
    } else if (stockStatus.value === "normal") {
      matchesStatus = qty > LOW_STOCK_THRESHOLD;
    } else if (stockStatus.value === "out") {
      matchesStatus = qty <= 0;
    }

    return matchesSku && matchesLocation && matchesStatus;
  });
});

// Check if any filter is active
const hasActiveFilters = computed(() => {
  return searchSku.value || searchLocation.value || stockStatus.value !== "all";
});

// Format quantity for display
const formatQuantity = (value: string): string => {
  const num = parseFloat(value || "0");
  return num.toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};

// Clear search filters
const clearFilters = () => {
  searchSku.value = "";
  searchLocation.value = "";
  stockStatus.value = "all";
};

// Focus search input (for keyboard shortcut)
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
    // Deselect all filtered stocks
    filteredStocks.value.forEach((s) => selectedIds.value.delete(s.id));
  } else {
    // Select all filtered stocks
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
          <label for="search-sku"
            >{{ t("inventory.sku") }} <kbd class="kbd-hint">/</kbd></label
          >
          <input
            id="search-sku"
            ref="skuInputRef"
            v-model="searchSku"
            type="text"
            :placeholder="t('common.search') + '...'"
            class="input"
          />
        </div>
        <div class="search-field">
          <label for="search-location">{{ t("inventory.location") }}</label>
          <input
            id="search-location"
            v-model="searchLocation"
            type="text"
            :placeholder="t('common.search') + '...'"
            class="input"
          />
        </div>
        <div class="search-field">
          <label for="stock-status">{{ t("inventory.stockStatus") }}</label>
          <select id="stock-status" v-model="stockStatus" class="input">
            <option value="all">{{ t("inventory.statusAll") }}</option>
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
    <div v-if="loading" class="stock-list__loading">
      <span class="spinner"></span>
      {{ t("common.loading") }}
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredStocks.length === 0" class="stock-list__empty">
      <p v-if="stocks.length === 0">{{ t("messages.noData") }}</p>
      <p v-else>{{ t("messages.noData") }}</p>
    </div>

    <!-- Bulk Action Bar (shows when items selected) -->
    <div v-if="hasSelection && !loading" class="bulk-action-bar">
      <div class="bulk-action-bar__info">
        <span class="bulk-action-bar__count">
          {{ selectedIds.size }} {{ t("bulk.selected") }}
        </span>
        <button class="btn btn--ghost btn--small" @click="clearSelection">
          {{ t("bulk.clearSelection") }}
        </button>
      </div>
      <div class="bulk-action-bar__actions">
        <button
          class="btn btn--secondary btn--small"
          @click="handleBulkReserve"
        >
          📦 {{ t("bulk.reserveSelected") }}
        </button>
        <button class="btn btn--warning btn--small" @click="handleBulkAdjust">
          ✏️ {{ t("bulk.adjustSelected") }}
        </button>
      </div>
    </div>

    <!-- Stock Table -->
    <table v-if="!loading && filteredStocks.length > 0" class="stock-table">
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
            <NuxtLink
              :to="`/inventory/${stock.id}`"
              class="stock-sku stock-sku--link"
            >
              {{ stock.sku }}
            </NuxtLink>
          </td>
          <td>{{ stock.locationId }}</td>
          <td class="text-right">
            <span class="quantity available">{{
              formatQuantity(stock.availableQuantity)
            }}</span>
          </td>
          <td class="text-right">
            <span class="quantity reserved">{{
              formatQuantity(stock.reservedQuantity)
            }}</span>
          </td>
          <td>{{ stock.unitOfMeasure }}</td>
          <td class="text-center">
            <div class="action-buttons">
              <!-- Primary Actions: เบิก > จอง > ขาย -->
              <button
                class="btn btn--small btn--secondary"
                :disabled="parseFloat(stock.availableQuantity) <= 0"
                @click="emit('withdraw', stock)"
                :title="t('inventory.withdrawStock')"
              >
                📦 {{ t("inventory.withdrawStock") }}
              </button>
              <button
                class="btn btn--small btn--secondary"
                :disabled="parseFloat(stock.availableQuantity) <= 0"
                @click="emit('reserve', stock)"
                :title="t('inventory.reserveStock')"
              >
                🔒 {{ t("inventory.reserveStock") }}
              </button>
              <button
                class="btn btn--small btn--secondary"
                :disabled="parseFloat(stock.availableQuantity) <= 0"
                @click="emit('quickSale', stock)"
                :title="t('inventory.quickSale')"
              >
                💰 {{ t("inventory.quickSale") }}
              </button>

              <!-- Secondary Actions -->
              <button
                class="btn btn--small btn--warning"
                :disabled="parseFloat(stock.reservedQuantity) <= 0"
                @click="emit('release', stock)"
                :title="t('inventory.releaseReservation')"
              >
                {{ t("inventory.releaseReservation") }}
              </button>
              <button
                class="btn btn--small btn--danger"
                :disabled="parseFloat(stock.reservedQuantity) <= 0"
                @click="emit('confirm', stock)"
                :title="t('inventory.confirmReservation')"
              >
                {{ t("inventory.confirmReservation") }}
              </button>
              <button
                class="btn btn--small btn--ghost"
                @click="emit('adjust', stock)"
                :title="t('inventory.adjustStock')"
              >
                {{ t("inventory.adjustStock") }}
              </button>
            </div>

          </td>
        </tr>
      </tbody>
    </table>

    <!-- Summary Footer -->
    <div v-if="filteredStocks.length > 0" class="stock-list__footer">
      <span
        >{{ filteredStocks.length }} / {{ stocks.length }}
        {{ t("inventory.stocks") }}</span
      >
    </div>
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
  gap: 0.25rem;
}

.search-field label {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--color-text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.input {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 0.875rem;
  min-width: 180px;
  transition: border-color 0.2s, background-color 0.3s;
  background: var(--color-surface);
  color: var(--color-text-primary);
}

.input:focus {
  outline: none;
  border-color: #4285f4;
}

.stock-list__actions {
  flex-shrink: 0;
}

.stock-list__loading,
.stock-list__empty {
  padding: 3rem;
  text-align: center;
  color: #666;
}

.spinner {
  display: inline-block;
  width: 1.25rem;
  height: 1.25rem;
  border: 2px solid #e0e0e0;
  border-top-color: #4285f4;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 0.5rem;
  vertical-align: middle;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.stock-table {
  width: 100%;
  border-collapse: collapse;
}

.stock-table th {
  padding: 0.75rem 1rem;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--color-text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
}

.stock-table td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.875rem;
  color: var(--color-text-primary);
}

.stock-table tbody tr:hover {
  background: var(--color-surface-hover);
}

.text-right {
  text-align: right;
}

.text-center {
  text-align: center;
}

.stock-sku {
  font-family: "SF Mono", "Consolas", monospace;
  font-weight: 500;
  color: var(--color-primary);
}

.stock-sku--link {
  text-decoration: none;
  transition: all 0.2s;
}

.stock-sku--link:hover {
  text-decoration: underline;
  color: var(--color-primary-hover);
}

.quantity {
  font-family: "SF Mono", "Consolas", monospace;
  font-weight: 500;
}

.quantity.available {
  color: #0d9488;
}

.quantity.reserved {
  color: #9333ea;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}

.stock-list__footer {
  padding: 0.75rem 1.5rem;
  background: var(--color-surface);
  border-top: 1px solid var(--color-border);
  font-size: 0.75rem;
  color: var(--color-text-secondary);
}

/* Button Styles */
.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn--primary {
  background: #1a73e8;
  color: white;
}

.btn--primary:hover:not(:disabled) {
  background: #1557b0;
}

.btn--secondary {
  background: var(--color-card);
  border: 1px solid var(--color-border);
  color: var(--color-text-primary);
}

.btn--secondary:hover:not(:disabled) {
  background: var(--color-surface-hover);
}

.btn--ghost {
  background: transparent;
  color: var(--color-primary);
}

.btn--ghost:hover:not(:disabled) {
  background: var(--color-surface-hover);
}

.btn--small {
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
}

.btn--warning {
  background: #f59e0b;
  color: white;
}

.btn--warning:hover:not(:disabled) {
  background: #d97706;
}

.btn--danger {
  background: #ef4444;
  color: white;
}

.btn--danger:hover:not(:disabled) {
  background: #dc2626;
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

/* Bulk action bar */
.bulk-action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background: var(--color-primary);
  color: white;
  border-radius: 8px;
  margin-bottom: 1rem;
  gap: 1rem;
  flex-wrap: wrap;
}

.bulk-action-bar__info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.bulk-action-bar__count {
  font-weight: 600;
}

.bulk-action-bar__actions {
  display: flex;
  gap: 0.5rem;
}

.bulk-action-bar .btn--ghost {
  color: white;
  border-color: rgba(255, 255, 255, 0.3);
}

.bulk-action-bar .btn--ghost:hover {
  background: rgba(255, 255, 255, 0.1);
}
</style>
