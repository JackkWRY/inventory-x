<script setup lang="ts">
import type { Stock, ConfirmReservationCommand } from '~/types/inventory'

/**
 * ConfirmReservationDialog Component
 *
 * Modal dialog for confirming a stock reservation (completing sale).
 * Deducts reserved quantity from stock permanently.
 *
 * BEST PRACTICE: Follows same pattern as other dialog components
 *
 * @example
 * <ConfirmReservationDialog
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
  open: boolean
  stock: Stock | null
  loading?: boolean
  error?: string | null
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  error: null
})

// Emits
const emit = defineEmits<{
  submit: [command: ConfirmReservationCommand]
  close: []
}>()

// Form State
const form = reactive({
  quantity: '',
  orderId: ''
})

// Computed
const reservedQuantity = computed(() => {
  if (!props.stock) return 0
  return parseFloat(props.stock.reservedQuantity || '0')
})

const quantityError = computed(() => {
  const qty = parseFloat(form.quantity)
  if (form.quantity && (isNaN(qty) || qty <= 0)) {
    return t('validation.positiveNumber')
  }
  if (qty > reservedQuantity.value) {
    return `Cannot confirm more than reserved (${reservedQuantity.value})`
  }
  return null
})

const isValid = computed(() => {
  const qty = parseFloat(form.quantity)
  return (
    props.stock !== null &&
    qty > 0 &&
    qty <= reservedQuantity.value &&
    form.orderId.trim().length > 0
  )
})

// Methods
const handleSubmit = () => {
  if (!isValid.value || !props.stock) return

  const command: ConfirmReservationCommand = {
    stockId: props.stock.id,
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

const formatQuantity = (value: string): string => {
  const num = parseFloat(value)
  return isNaN(num) ? '0' : num.toLocaleString()
}

const handleBackdropClick = (event: MouseEvent) => {
  if (event.target === event.currentTarget) {
    emit('close')
  }
}

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
        <div class="dialog" role="dialog" aria-modal="true">
          <!-- Header -->
          <div class="dialog__header">
            <h2 class="dialog__title">{{ t('inventory.confirmReservation') }}</h2>
            <button class="dialog__close" @click="emit('close')">✕</button>
          </div>

          <!-- Warning -->
          <div class="dialog__warning">
            {{ t('inventory.confirmWarning') }}
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
              <span class="stock-info__label">{{ t('inventory.reservedQuantity') }}</span>
              <span class="stock-info__value stock-info__value--reserved">
                {{ formatQuantity(stock.reservedQuantity) }} {{ stock.unitOfMeasure }}
              </span>
            </div>
          </div>

          <!-- Form -->
          <form class="dialog__body" @submit.prevent="handleSubmit">
            <!-- Quantity -->
            <div class="form-group">
              <label for="confirm-quantity" class="form-label">
                {{ t('inventory.quantity') }} <span class="required">*</span>
              </label>
              <input
                id="confirm-quantity"
                v-model="form.quantity"
                type="number"
                step="0.01"
                min="0"
                :max="reservedQuantity"
                class="form-input"
                :placeholder="t('validation.positiveNumber')"
                :disabled="loading"
                required
              />
              <span v-if="quantityError" class="form-error">{{ quantityError }}</span>
              <span v-else class="form-hint">{{ t('inventory.maximum') }}: {{ formatQuantity(String(reservedQuantity)) }}</span>
            </div>

            <!-- Order ID -->
            <div class="form-group">
              <label for="confirm-orderId" class="form-label">
                {{ t('inventory.orderId') }} <span class="required">*</span>
              </label>
              <input
                id="confirm-orderId"
                v-model="form.orderId"
                type="text"
                class="form-input"
                placeholder="e.g., ORD-2024-001"
                :disabled="loading"
                required
              />
              <span class="form-hint">{{ t('inventory.orderFulfilledHint') }}</span>
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
              class="btn btn--danger"
              :disabled="loading || !isValid"
              @click="handleSubmit"
            >
              <span v-if="loading" class="spinner"></span>
              {{ loading ? t('common.loading') : t('inventory.confirmReservation') }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: var(--color-card);
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 480px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dialog__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

.dialog__title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.dialog__close {
  background: none;
  border: none;
  font-size: 1.25rem;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
}

.dialog__close:hover {
  background: var(--color-surface-hover);
  color: var(--color-text-primary);
}

.dialog__warning {
  padding: 0.75rem 1.5rem;
  background: #fef3c7;
  color: #92400e;
  font-size: 0.875rem;
  font-weight: 500;
}

.dialog__error {
  padding: 0.75rem 1.5rem;
  background: #fef2f2;
  color: #dc2626;
  font-size: 0.875rem;
}

.stock-info {
  padding: 1rem 1.5rem;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
}

.stock-info__row {
  display: flex;
  justify-content: space-between;
  padding: 0.375rem 0;
}

.stock-info__label {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

.stock-info__value {
  font-weight: 500;
  color: var(--color-text-primary);
}

.stock-info__value--sku {
  font-family: monospace;
  color: var(--color-primary);
}

.stock-info__value--reserved {
  color: #f59e0b;
}

.dialog__body {
  padding: 1.5rem;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
}

.required {
  color: #ef4444;
}

.form-input {
  width: 100%;
  padding: 0.625rem 0.875rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.9375rem;
  background: var(--color-surface);
  color: var(--color-text-primary);
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.form-input:disabled {
  background: var(--color-surface);
  cursor: not-allowed;
  opacity: 0.6;
}

.form-error {
  display: block;
  font-size: 0.75rem;
  color: #ef4444;
  margin-top: 0.375rem;
}

.form-hint {
  display: block;
  font-size: 0.75rem;
  color: var(--color-text-secondary);
  margin-top: 0.375rem;
}

.dialog__footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid var(--color-border);
  background: var(--color-surface);
}

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
}

.btn--secondary {
  background: var(--color-card);
  border: 1px solid var(--color-border);
  color: var(--color-text-primary);
}

.btn--secondary:hover:not(:disabled) {
  background: var(--color-surface-hover);
}

.btn--danger {
  background: #ef4444;
  border: none;
  color: white;
}

.btn--danger:hover:not(:disabled) {
  background: #dc2626;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid currentColor;
  border-right-color: transparent;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
