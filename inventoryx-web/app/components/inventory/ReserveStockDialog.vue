<script setup lang="ts">
import type { Stock, ReserveStockCommand } from '~/types/inventory'

/**
 * ReserveStockDialog Component
 *
 * Modal dialog for reserving stock for an order.
 * Shows current stock info and validates quantity against available.
 *
 * @example
 * <ReserveStockDialog
 *   :open="isOpen"
 *   :stock="selectedStock"
 *   :loading="isLoading"
 *   @submit="handleSubmit"
 *   @close="handleClose"
 * />
 */

// i18n
const { t } = useI18n()

// Props
interface Props {
  /** Whether dialog is open */
  open: boolean
  /** Stock to reserve from */
  stock: Stock | null
  /** Loading state during submission */
  loading?: boolean
  /** Error message to display */
  error?: string | null
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  error: null
})

// Emits
const emit = defineEmits<{
  /** Triggered when form is submitted */
  submit: [command: ReserveStockCommand]
  /** Triggered when dialog should close */
  close: []
}>()

// Form state
const form = reactive({
  quantity: '',
  orderId: ''
})

// Computed available quantity
const availableQuantity = computed(() => {
  return parseFloat(props.stock?.availableQuantity || '0')
})

// Validation
const quantityError = computed(() => {
  const qty = parseFloat(form.quantity)
  if (isNaN(qty) || qty <= 0) return null
  if (qty > availableQuantity.value) {
    return `Cannot reserve more than available (${availableQuantity.value})`
  }
  return null
})

const isValid = computed(() => {
  const qty = parseFloat(form.quantity)
  return (
    props.stock !== null &&
    qty > 0 &&
    qty <= availableQuantity.value &&
    form.orderId.trim().length > 0
  )
})

// Handle form submission
const handleSubmit = () => {
  if (!isValid.value || !props.stock) return

  const command: ReserveStockCommand = {
    sku: props.stock.sku,
    locationId: props.stock.locationId,
    quantity: form.quantity,
    orderId: form.orderId.trim()
  }

  emit('submit', command)
}

// Reset form when dialog opens
watch(() => props.open, (isOpen) => {
  if (isOpen) {
    form.quantity = ''
    form.orderId = ''
  }
})

// Format quantity for display
const formatQuantity = (value: string): string => {
  const num = parseFloat(value || '0')
  return num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// Handle backdrop click
const handleBackdropClick = (event: MouseEvent) => {
  if ((event.target as HTMLElement).classList.contains('dialog-backdrop')) {
    emit('close')
  }
}

// Handle escape key
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.open) {
    emit('close')
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="open" class="dialog-backdrop" @click="handleBackdropClick">
        <div class="dialog" role="dialog" aria-modal="true" aria-labelledby="dialog-title">
          <!-- Header -->
          <div class="dialog__header">
            <h2 id="dialog-title" class="dialog__title">{{ t('inventory.reserveStock') }}</h2>
            <button class="dialog__close" @click="emit('close')" aria-label="Close dialog">
              ✕
            </button>
          </div>

          <!-- Error Message -->
          <div v-if="error" class="dialog__error">
            {{ error }}
          </div>

          <!-- Stock Info -->
          <div v-if="stock" class="stock-info">
            <div class="stock-info__row">
              <span class="stock-info__label">{{ t('inventory.sku') }}</span>
              <span class="stock-info__value stock-info__value--sku">{{ stock.sku }}</span>
            </div>
            <div class="stock-info__row">
              <span class="stock-info__label">{{ t('inventory.location') }}</span>
              <span class="stock-info__value">{{ stock.locationId }}</span>
            </div>
            <div class="stock-info__row">
              <span class="stock-info__label">{{ t('inventory.available') }}</span>
              <span class="stock-info__value stock-info__value--available">
                {{ formatQuantity(stock.availableQuantity) }} {{ stock.unitOfMeasure }}
              </span>
            </div>
            <div class="stock-info__row">
              <span class="stock-info__label">{{ t('inventory.reserved') }}</span>
              <span class="stock-info__value stock-info__value--reserved">
                {{ formatQuantity(stock.reservedQuantity) }} {{ stock.unitOfMeasure }}
              </span>
            </div>
          </div>

          <!-- Form -->
          <form class="dialog__body" @submit.prevent="handleSubmit">
            <!-- Quantity -->
            <div class="form-group">
              <label for="quantity" class="form-label">
                {{ t('inventory.quantityToReserve') }} <span class="required">*</span>
              </label>
              <input
                id="quantity"
                v-model="form.quantity"
                type="number"
                class="form-input"
                :class="{ 'form-input--error': quantityError }"
                placeholder="0"
                :max="availableQuantity"
                min="0.01"
                step="0.01"
                :disabled="loading"
                required
              />
              <span v-if="quantityError" class="form-error">{{ quantityError }}</span>
              <span v-else class="form-hint">{{ t('inventory.maximum') }}: {{ formatQuantity(String(availableQuantity)) }}</span>
            </div>

            <!-- Order ID -->
            <div class="form-group">
              <label for="orderId" class="form-label">
                {{ t('inventory.orderId') }} <span class="required">*</span>
              </label>
              <input
                id="orderId"
                v-model="form.orderId"
                type="text"
                class="form-input"
                placeholder="e.g., ORD-2024-001"
                :disabled="loading"
                required
              />
              <span class="form-hint">{{ t('inventory.orderIdHint') }}</span>
            </div>
          </form>

          <!-- Footer -->
          <div class="dialog__footer">
            <button
              type="button"
              class="btn btn--secondary"
              :disabled="loading"
              @click="emit('close')"
            >
              {{ t('common.cancel') }}
            </button>
            <button
              type="submit"
              class="btn btn--primary"
              :disabled="!isValid || loading"
              @click="handleSubmit"
            >
              <span v-if="loading" class="spinner"></span>
              {{ loading ? t('inventory.reserving') : t('inventory.reserveStock') }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
/* All base styles now come from global main.css */
/* Component-specific overrides only */

.stock-info__value--reserved {
  color: #9333ea;
}

/* Spinner */
.spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-enter-active .dialog,
.fade-leave-active .dialog {
  transition: transform 0.2s ease;
}

.fade-enter-from .dialog,
.fade-leave-to .dialog {
  transform: scale(0.95);
}
</style>
