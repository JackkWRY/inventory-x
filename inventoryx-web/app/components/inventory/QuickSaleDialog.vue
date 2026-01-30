<script setup lang="ts">
import type { Stock, QuickSaleCommand } from '~/types/inventory'

/**
 * QuickSaleDialog Component
 *
 * Modal dialog for POS/Walk-in quick sales.
 * Combines Reserve + Confirm into single operation.
 *
 * @example
 * <QuickSaleDialog
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
  /** Stock to sell */
  stock: Stock | null
  /** Loading state */
  loading?: boolean
  /** Error message */
  error?: string | null
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  error: null
})

// Emits
const emit = defineEmits<{
  (e: 'submit', command: QuickSaleCommand): void
  (e: 'close'): void
}>()

// Form state
const form = reactive({
  quantity: '',
  orderId: '',
  performedBy: ''
})

// Computed: Available quantity
const availableQuantity = computed(() => {
  if (!props.stock?.availableQuantity) return 0
  return parseFloat(props.stock.availableQuantity)
})

// Computed: Form validation
const quantityError = computed(() => {
  const qty = parseFloat(form.quantity)
  if (isNaN(qty) || qty <= 0) return null
  if (qty > availableQuantity.value) {
    return t('validation.exceedsAvailable')
  }
  return null
})

const isValid = computed(() => {
  const qty = parseFloat(form.quantity)
  return (
    qty > 0 &&
    qty <= availableQuantity.value &&
    form.orderId.trim().length > 0 &&
    form.performedBy.trim().length > 0 &&
    !props.loading
  )
})

// Format quantity for display
function formatQuantity(qty: string | undefined): string {
  if (!qty) return '0'
  const num = parseFloat(qty)
  return Number.isInteger(num) ? num.toString() : num.toFixed(2)
}

// Submit handler
function handleSubmit() {
  if (!props.stock || !isValid.value) return

  const command: QuickSaleCommand = {
    stockId: props.stock.id,
    quantity: form.quantity,
    orderId: form.orderId.trim(),
    performedBy: form.performedBy.trim()
  }

  emit('submit', command)
}

// Reset form when dialog opens
watch(() => props.open, (isOpen) => {
  if (isOpen) {
    form.quantity = ''
    form.orderId = ''
    form.performedBy = ''
  }
})

// Close on Escape
function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape' && props.open) {
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
      <div v-if="open" class="dialog-backdrop" @click.self="emit('close')">
        <div class="dialog" role="dialog" aria-modal="true">
          <!-- Header -->
          <div class="dialog__header">
            <h2 class="dialog__title">{{ t('inventory.quickSale') }}</h2>
            <button class="dialog__close" @click="emit('close')">✕</button>
          </div>

          <!-- Error -->
          <div v-if="error" class="dialog__error">{{ error }}</div>

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
          </div>

          <!-- Form -->
          <form class="dialog__body" @submit.prevent="handleSubmit">
            <!-- Quantity -->
            <div class="form-group">
              <label for="sale-quantity" class="form-label">
                {{ t('inventory.quantity') }} <span class="required">*</span>
              </label>
              <input
                id="sale-quantity"
                v-model="form.quantity"
                type="number"
                step="0.01"
                min="0"
                :max="availableQuantity"
                class="form-input"
                :placeholder="t('validation.positiveNumber')"
                :disabled="loading"
                required
              />
              <span v-if="quantityError" class="form-error">{{ quantityError }}</span>
              <span v-else class="form-hint">{{ t('inventory.maximum') }}: {{ formatQuantity(String(availableQuantity)) }}</span>
            </div>

            <!-- Order/Receipt ID -->
            <div class="form-group">
              <label for="sale-orderId" class="form-label">
                {{ t('inventory.receiptId') }} <span class="required">*</span>
              </label>
              <input
                id="sale-orderId"
                v-model="form.orderId"
                type="text"
                class="form-input"
                :placeholder="t('inventory.receiptIdPlaceholder')"
                :disabled="loading"
                required
              />
            </div>

            <!-- Performed By -->
            <div class="form-group">
              <label for="sale-performedBy" class="form-label">
                {{ t('inventory.performedBy') }} <span class="required">*</span>
              </label>
              <input
                id="sale-performedBy"
                v-model="form.performedBy"
                type="text"
                class="form-input"
                placeholder="Your name or ID"
                :disabled="loading"
                required
              />
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
              type="button"
              class="btn btn--success"
              :disabled="!isValid || loading"
              @click="handleSubmit"
            >
              <span v-if="loading" class="spinner"></span>
              {{ loading ? t('common.loading') : t('inventory.completeSale') }}
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

