<script setup lang="ts">
import type { Stock, StockMovement, ReserveStockCommand, AdjustStockCommand } from '~/types/inventory'
import { useInventoryStore } from '~/stores/inventory'
import { useInventoryApi } from '~/composables/api/useInventoryApi'
import { useToastStore } from '~/stores/toast'

/**
 * Stock Detail Page
 *
 * Displays detailed information about a specific stock item including:
 * - Stock info (SKU, Location, Quantities)
 * - Movement history timeline
 * - Quick action buttons (Reserve, Adjust)
 *
 * @route /inventory/:id
 */

// Page Meta
definePageMeta({
  title: 'Stock Details'
})

// i18n
const { t } = useI18n()

// Route
const route = useRoute()
const stockId = computed(() => route.params.id as string)

// Stores
const inventoryStore = useInventoryStore()
const toast = useToastStore()

// State
const stock = ref<Stock | null>(null)
const movements = ref<StockMovement[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

// Dialog states
const showReserveDialog = ref(false)
const showAdjustDialog = ref(false)

// Fetch stock on mount
onMounted(async () => {
  await fetchStockDetails()
})

// Watch for route changes
watch(stockId, async () => {
  await fetchStockDetails()
})

/**
 * Fetch stock details and movements
 */
async function fetchStockDetails() {
  loading.value = true
  error.value = null

  try {
    // Fetch stock details
    await inventoryStore.fetchStockById(stockId.value)
    stock.value = inventoryStore.selectedStock

    // Fetch real movements from backend API
    const api = useInventoryApi()
    try {
      movements.value = await api.getStockMovements(stockId.value)
    } catch {
      // If movements API fails, use empty array (no movements yet)
      movements.value = []
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : t('messages.loadError')
  } finally {
    loading.value = false
  }
}

// Helpers
const formatQuantity = (value: string): string => {
  const num = parseFloat(value || '0')
  return num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const formatDate = (isoDate: string): string => {
  const date = new Date(isoDate)
  return date.toLocaleString()
}

const getMovementIcon = (type: StockMovement['movementType']): string => {
  const icons: Record<string, string> = {
    RECEIPT: '📥',
    RESERVATION: '🔒',
    RELEASE: '🔓',
    CONFIRMATION: '✅',
    SALE: '💰',
    TRANSFER: '🔄',
    ADJUSTMENT: '📝'
  }
  return icons[type] || '📋'
}

const getMovementColor = (type: StockMovement['movementType']): string => {
  const colors: Record<string, string> = {
    RECEIPT: 'movement--receipt',
    RESERVATION: 'movement--reservation',
    RELEASE: 'movement--release',
    CONFIRMATION: 'movement--confirmation',
    SALE: 'movement--sale',
    TRANSFER: 'movement--transfer',
    ADJUSTMENT: 'movement--adjustment'
  }
  return colors[type] || ''
}

const isPositiveMovement = (quantity: string): boolean => {
  return parseFloat(quantity) > 0
}

// Reserve Dialog Handlers
const handleOpenReserveDialog = () => {
  inventoryStore.clearError()
  showReserveDialog.value = true
}

const handleCloseReserveDialog = () => {
  showReserveDialog.value = false
}

const handleReserveSubmit = async (command: ReserveStockCommand) => {
  try {
    await inventoryStore.reserveStock(command)
    showReserveDialog.value = false
    toast.success(t('toast.stockReserved'))
    await fetchStockDetails()
  } catch {
    toast.error(t('toast.operationFailed'))
  }
}

// Adjust Dialog Handlers
const handleOpenAdjustDialog = () => {
  inventoryStore.clearError()
  showAdjustDialog.value = true
}

const handleCloseAdjustDialog = () => {
  showAdjustDialog.value = false
}

const handleAdjustSubmit = async (command: AdjustStockCommand) => {
  try {
    await inventoryStore.adjustStock(command)
    showAdjustDialog.value = false
    toast.success(t('toast.stockAdjusted'))
    await fetchStockDetails()
  } catch {
    toast.error(t('toast.operationFailed'))
  }
}
</script>

<template>
  <div class="stock-detail-page">
    <!-- Header -->
    <header class="page-header">
      <div class="page-header__nav">
        <NuxtLink to="/inventory" class="back-link">
          ← {{ t('common.back') }}
        </NuxtLink>
        <CommonLanguageSwitcher />
      </div>
      <h1 class="page-header__title">{{ t('inventory.stockDetails') }}</h1>
    </header>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <span class="spinner"></span>
      {{ t('common.loading') }}
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-state">
      <span class="error-icon">⚠️</span>
      <p>{{ error }}</p>
      <button class="btn btn--primary" @click="fetchStockDetails">
        {{ t('common.submit') }}
      </button>
    </div>

    <!-- Stock Detail Content -->
    <template v-else-if="stock">
      <!-- Stock Info Card -->
      <div class="stock-card">
        <div class="stock-card__header">
          <span class="stock-card__sku">{{ stock.sku }}</span>
          <span class="stock-card__location">📍 {{ stock.locationId }}</span>
        </div>

        <div class="stock-card__body">
          <div class="quantity-grid">
            <div class="quantity-item quantity-item--available">
              <span class="quantity-item__label">{{ t('inventory.availableQuantity') }}</span>
              <span class="quantity-item__value">{{ formatQuantity(stock.availableQuantity) }}</span>
            </div>
            <div class="quantity-item quantity-item--reserved">
              <span class="quantity-item__label">{{ t('inventory.reservedQuantity') }}</span>
              <span class="quantity-item__value">{{ formatQuantity(stock.reservedQuantity) }}</span>
            </div>
            <div class="quantity-item">
              <span class="quantity-item__label">{{ t('inventory.unitOfMeasure') }}</span>
              <span class="quantity-item__value">{{ stock.unitOfMeasure }}</span>
            </div>
          </div>
        </div>

        <div class="stock-card__actions">
          <button
            class="btn btn--secondary"
            :disabled="parseFloat(stock.availableQuantity) <= 0"
            @click="handleOpenReserveDialog"
          >
            🔒 {{ t('inventory.reserveStock') }}
          </button>
          <button class="btn btn--ghost" @click="handleOpenAdjustDialog">
            📝 {{ t('inventory.adjustStock') }}
          </button>
        </div>
      </div>

      <!-- Movement History -->
      <div class="movements-section">
        <h2 class="movements-section__title">{{ t('inventory.movementHistory') }}</h2>

        <div v-if="movements.length === 0" class="movements-empty">
          {{ t('messages.noData') }}
        </div>

        <div v-else class="movements-timeline">
          <div
            v-for="movement in movements"
            :key="movement.id"
            class="movement-item"
            :class="getMovementColor(movement.movementType)"
          >
            <div class="movement-item__icon">
              {{ getMovementIcon(movement.movementType) }}
            </div>
            <div class="movement-item__content">
              <div class="movement-item__header">
                <span class="movement-item__type">
                  {{ t(`movementTypes.${movement.movementType}`) }}
                </span>
                <span
                  class="movement-item__quantity"
                  :class="{ 'positive': isPositiveMovement(movement.quantity), 'negative': !isPositiveMovement(movement.quantity) }"
                >
                  {{ movement.quantity }}
                </span>
              </div>
              <div class="movement-item__details">
                <span v-if="movement.reason" class="movement-item__reason">
                  {{ movement.reason }}
                </span>
                <span v-if="movement.referenceId" class="movement-item__reference">
                  Ref: {{ movement.referenceId }}
                </span>
              </div>
              <div class="movement-item__footer">
                <span v-if="movement.performedBy" class="movement-item__performer">
                  👤 {{ movement.performedBy }}
                </span>
                <span class="movement-item__date">
                  🕐 {{ formatDate(movement.performedAt) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- Reserve Stock Dialog -->
    <InventoryReserveStockDialog
      :open="showReserveDialog"
      :stock="stock"
      :loading="inventoryStore.loading"
      :error="inventoryStore.error"
      @submit="handleReserveSubmit"
      @close="handleCloseReserveDialog"
    />

    <!-- Adjust Stock Dialog -->
    <InventoryAdjustStockDialog
      :open="showAdjustDialog"
      :stock="stock"
      :loading="inventoryStore.loading"
      :error="inventoryStore.error"
      @submit="handleAdjustSubmit"
      @close="handleCloseAdjustDialog"
    />
  </div>
</template>

<style scoped>
.stock-detail-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}

/* Header */
.page-header {
  margin-bottom: 2rem;
}

.page-header__nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.back-link {
  color: #1a73e8;
  text-decoration: none;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.back-link:hover {
  text-decoration: underline;
}

.page-header__title {
  font-size: 1.75rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

/* Loading & Error States */
.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  color: #6b7280;
}

.spinner {
  display: inline-block;
  width: 2rem;
  height: 2rem;
  border: 3px solid #e5e7eb;
  border-top-color: #1a73e8;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.error-state p {
  margin: 0 0 1rem;
  color: #ef4444;
}

/* Stock Card */
.stock-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 2rem;
}

.stock-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.stock-card__sku {
  font-family: 'SF Mono', 'Consolas', monospace;
  font-size: 1.25rem;
  font-weight: 600;
  color: #1a73e8;
}

.stock-card__location {
  color: #6b7280;
  font-size: 0.9375rem;
}

.stock-card__body {
  padding: 1.5rem;
}

.quantity-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}

.quantity-item {
  text-align: center;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
}

.quantity-item__label {
  display: block;
  font-size: 0.75rem;
  font-weight: 500;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.5rem;
}

.quantity-item__value {
  display: block;
  font-size: 1.5rem;
  font-weight: 600;
  font-family: 'SF Mono', 'Consolas', monospace;
}

.quantity-item--available .quantity-item__value {
  color: #10b981;
}

.quantity-item--reserved .quantity-item__value {
  color: #f59e0b;
}

.stock-card__actions {
  display: flex;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
}

/* Buttons */
.btn {
  padding: 0.625rem 1.25rem;
  font-size: 0.9375rem;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  border: none;
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
  background: white;
  border: 1px solid #d1d5db;
  color: #374151;
}

.btn--secondary:hover:not(:disabled) {
  background: #f9fafb;
}

.btn--ghost {
  background: transparent;
  color: #1a73e8;
}

.btn--ghost:hover:not(:disabled) {
  background: #e8f0fe;
}

/* Movement History */
.movements-section {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
}

.movements-section__title {
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
  padding: 1rem 1.5rem;
  margin: 0;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.movements-empty {
  padding: 3rem 2rem;
  text-align: center;
  color: #6b7280;
}

.movements-timeline {
  padding: 1rem 0;
}

.movement-item {
  display: flex;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.movement-item:last-child {
  border-bottom: none;
}

.movement-item__icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.movement-item__content {
  flex: 1;
  min-width: 0;
}

.movement-item__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.25rem;
}

.movement-item__type {
  font-weight: 600;
  color: #111827;
}

.movement-item__quantity {
  font-family: 'SF Mono', 'Consolas', monospace;
  font-weight: 600;
}

.movement-item__quantity.positive {
  color: #10b981;
}

.movement-item__quantity.negative {
  color: #ef4444;
}

.movement-item__details {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  font-size: 0.875rem;
  color: #4b5563;
  margin-bottom: 0.25rem;
}

.movement-item__reference {
  color: #1a73e8;
  font-family: monospace;
}

.movement-item__footer {
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  color: #9ca3af;
}

/* Movement Colors */
.movement--receipt {
  border-left: 3px solid #10b981;
}

.movement--reservation {
  border-left: 3px solid #f59e0b;
}

.movement--release {
  border-left: 3px solid #6366f1;
}

.movement--confirmation {
  border-left: 3px solid #22c55e;
}

.movement--sale {
  border-left: 3px solid #ec4899;
}

.movement--transfer {
  border-left: 3px solid #8b5cf6;
}

.movement--adjustment {
  border-left: 3px solid #64748b;
}

/* Responsive */
@media (max-width: 640px) {
  .quantity-grid {
    grid-template-columns: 1fr;
  }

  .stock-card__header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .stock-card__actions {
    flex-direction: column;
  }
}
</style>
