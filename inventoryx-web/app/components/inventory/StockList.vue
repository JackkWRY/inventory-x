<script setup lang="ts">
import type { Stock } from '~/types/inventory'

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
const { t } = useI18n()

// Props
interface Props {
  /** List of stocks to display */
  stocks: Stock[]
  /** Loading state */
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

// Emits
const emit = defineEmits<{
  /** Triggered when user clicks receive stock button */
  receive: []
  /** Triggered when user clicks reserve button for a stock */
  reserve: [stock: Stock]
  /** Triggered when user clicks release button for a stock */
  release: [stock: Stock]
  /** Triggered when user clicks confirm button for a stock */
  confirm: [stock: Stock]
  /** Triggered when user clicks adjust button for a stock */
  adjust: [stock: Stock]
  /** Triggered when user clicks view details for a stock */
  view: [stock: Stock]
}>()

// Search state
const searchSku = ref('')
const searchLocation = ref('')

// Computed filtered stocks
const filteredStocks = computed(() => {
  return props.stocks.filter(stock => {
    const matchesSku = !searchSku.value ||
      stock.sku.toLowerCase().includes(searchSku.value.toLowerCase())
    const matchesLocation = !searchLocation.value ||
      stock.locationId.toLowerCase().includes(searchLocation.value.toLowerCase())
    return matchesSku && matchesLocation
  })
})

// Format quantity for display
const formatQuantity = (value: string): string => {
  const num = parseFloat(value || '0')
  return num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// Clear search filters
const clearFilters = () => {
  searchSku.value = ''
  searchLocation.value = ''
}
</script>

<template>
  <div class="stock-list">
    <!-- Header with Search and Actions -->
    <div class="stock-list__header">
      <div class="stock-list__search">
        <div class="search-field">
          <label for="search-sku">{{ t('inventory.sku') }}</label>
          <input
            id="search-sku"
            v-model="searchSku"
            type="text"
            :placeholder="t('common.search') + '...'"
            class="input"
          />
        </div>
        <div class="search-field">
          <label for="search-location">{{ t('inventory.location') }}</label>
          <input
            id="search-location"
            v-model="searchLocation"
            type="text"
            :placeholder="t('common.search') + '...'"
            class="input"
          />
        </div>
        <button
          v-if="searchSku || searchLocation"
          class="btn btn--secondary"
          @click="clearFilters"
        >
          {{ t('common.filter') }}
        </button>
      </div>
      <div class="stock-list__actions">
        <button class="btn btn--primary" @click="emit('receive')">
          + {{ t('inventory.receiveStock') }}
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="stock-list__loading">
      <span class="spinner"></span>
      {{ t('common.loading') }}
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredStocks.length === 0" class="stock-list__empty">
      <p v-if="stocks.length === 0">{{ t('messages.noData') }}</p>
      <p v-else>{{ t('messages.noData') }}</p>
    </div>

    <!-- Stock Table -->
    <table v-else class="stock-table">
      <thead>
        <tr>
          <th>{{ t('inventory.sku') }}</th>
          <th>{{ t('inventory.location') }}</th>
          <th class="text-right">{{ t('inventory.availableQuantity') }}</th>
          <th class="text-right">{{ t('inventory.reservedQuantity') }}</th>
          <th>{{ t('inventory.unitOfMeasure') }}</th>
          <th class="text-center">{{ t('common.actions') }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="stock in filteredStocks" :key="stock.id">
          <td>
            <NuxtLink :to="`/inventory/${stock.id}`" class="stock-sku stock-sku--link">
              {{ stock.sku }}
            </NuxtLink>
          </td>
          <td>{{ stock.locationId }}</td>
          <td class="text-right">
            <span class="quantity available">{{ formatQuantity(stock.availableQuantity) }}</span>
          </td>
          <td class="text-right">
            <span class="quantity reserved">{{ formatQuantity(stock.reservedQuantity) }}</span>
          </td>
          <td>{{ stock.unitOfMeasure }}</td>
          <td class="text-center">
            <div class="action-buttons">
              <button
                class="btn btn--small btn--secondary"
                :disabled="parseFloat(stock.availableQuantity) <= 0"
                @click="emit('reserve', stock)"
              >
                {{ t('inventory.reserveStock') }}
              </button>
              <button
                class="btn btn--small btn--warning"
                :disabled="parseFloat(stock.reservedQuantity) <= 0"
                @click="emit('release', stock)"
              >
                {{ t('inventory.releaseReservation') }}
              </button>
              <button
                class="btn btn--small btn--danger"
                :disabled="parseFloat(stock.reservedQuantity) <= 0"
                @click="emit('confirm', stock)"
              >
                {{ t('inventory.confirmReservation') }}
              </button>
              <button
                class="btn btn--small btn--ghost"
                @click="emit('adjust', stock)"
              >
                {{ t('inventory.adjustStock') }}
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Summary Footer -->
    <div v-if="filteredStocks.length > 0" class="stock-list__footer">
      <span>{{ filteredStocks.length }} / {{ stocks.length }} {{ t('inventory.stocks') }}</span>
    </div>
  </div>
</template>

<style scoped>
.stock-list {
  background: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.stock-list__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 1.5rem;
  border-bottom: 1px solid #e0e0e0;
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
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.input {
  padding: 0.5rem 0.75rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  font-size: 0.875rem;
  min-width: 180px;
  transition: border-color 0.2s;
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
  to { transform: rotate(360deg); }
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
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.stock-table td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #f0f0f0;
  font-size: 0.875rem;
}

.stock-table tbody tr:hover {
  background: #f8f9fa;
}

.text-right {
  text-align: right;
}

.text-center {
  text-align: center;
}

.stock-sku {
  font-family: 'SF Mono', 'Consolas', monospace;
  font-weight: 500;
  color: #1a73e8;
}

.stock-sku--link {
  text-decoration: none;
  transition: all 0.2s;
}

.stock-sku--link:hover {
  text-decoration: underline;
  color: #1557b0;
}

.quantity {
  font-family: 'SF Mono', 'Consolas', monospace;
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
  background: #f8f9fa;
  border-top: 1px solid #e0e0e0;
  font-size: 0.75rem;
  color: #666;
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
  background: #f1f3f4;
  color: #3c4043;
}

.btn--secondary:hover:not(:disabled) {
  background: #e8eaed;
}

.btn--ghost {
  background: transparent;
  color: #1a73e8;
}

.btn--ghost:hover:not(:disabled) {
  background: #e8f0fe;
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
</style>
