<script setup lang="ts">
import type { Stock, AdjustStockCommand } from '~/types/inventory'

/**
 * AdjustStockDialog Component
 *
 * Modal dialog for manually adjusting stock quantity.
 * Used for inventory counts, corrections, or shrinkage.
 *
 * BEST PRACTICE: Requires reason and performer for audit trail
 *
 * @example
 * <AdjustStockDialog
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
  submit: [command: AdjustStockCommand]
  close: []
}>()

// Form State
const form = reactive({
  newQuantity: '',
  reason: '',
  performedBy: ''
})

// Computed
const currentQuantity = computed(() => {
  if (!props.stock) return 0
  return parseFloat(props.stock.availableQuantity || '0')
})

const quantityDifference = computed(() => {
  const newQty = parseFloat(form.newQuantity)
  if (isNaN(newQty)) return 0
  return newQty - currentQuantity.value
})

const quantityError = computed(() => {
  const qty = parseFloat(form.newQuantity)
  if (form.newQuantity && (isNaN(qty) || qty < 0)) {
    return t('validation.positiveNumber')
  }
  return null
})

const isValid = computed(() => {
  const qty = parseFloat(form.newQuantity)
  return (
    props.stock !== null &&
    !isNaN(qty) &&
    qty >= 0 &&
    form.reason.trim().length >= 10 &&
    form.performedBy.trim().length > 0
  )
})

// Methods
const handleSubmit = () => {
  if (!isValid.value || !props.stock) return

  const command: AdjustStockCommand = {
    stockId: props.stock.id,
    newQuantity: form.newQuantity,
    reason: form.reason.trim(),
    performedBy: form.performedBy.trim()
  }
  emit('submit', command)
}

// Reset form when dialog opens
watch(() => props.open, (isOpen) => {
  if (isOpen && props.stock) {
    form.newQuantity = props.stock.availableQuantity
    form.reason = ''
    form.performedBy = ''
  }
})

const formatQuantity = (value: string): string => {
  const num = parseFloat(value)
  return isNaN(num) ? '0' : num.toLocaleString()
}

const formatDifference = (diff: number): string => {
  if (diff === 0) return '0 (no change)'
  const sign = diff > 0 ? '+' : ''
  return `${sign}${diff.toLocaleString()}`
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
            <h2 class="dialog__title">{{ t('inventory.adjustStock') }}</h2>
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
              <span class="stock-info__label">{{ t('inventory.currentAvailable') }}</span>
              <span class="stock-info__value stock-info__value--available">
                {{ formatQuantity(stock.availableQuantity) }} {{ stock.unitOfMeasure }}
              </span>
            </div>
          </div>

          <!-- Form -->
          <form class="dialog__body" @submit.prevent="handleSubmit">
            <div class="form-group">
              <label for="adjust-quantity" class="form-label">
                {{ t('inventory.newQuantity') }} <span class="required">*</span>
              </label>
              <input
                id="adjust-quantity"
                v-model="form.newQuantity"
                type="number"
                step="0.01"
                min="0"
                class="form-input"
                :placeholder="t('validation.positiveNumber')"
                :disabled="loading"
                required
              />
              <span v-if="quantityError" class="form-error">{{ quantityError }}</span>
              <span v-else class="form-hint" :class="{ 'form-hint--positive': quantityDifference > 0, 'form-hint--negative': quantityDifference < 0 }">
                {{ t('inventory.difference') }}: {{ formatDifference(quantityDifference) }}
              </span>
            </div>

            <!-- Reason -->
            <div class="form-group">
              <label for="adjust-reason" class="form-label">
                {{ t('inventory.reason') }} <span class="required">*</span>
              </label>
              <textarea
                id="adjust-reason"
                v-model="form.reason"
                class="form-textarea"
                rows="3"
                :placeholder="t('inventory.adjustReasonPlaceholder')"
                :disabled="loading"
                required
              ></textarea>
              <span class="form-hint">{{ t('inventory.reasonHint') }}</span>
            </div>

            <!-- Performed By -->
            <div class="form-group">
              <label for="adjust-performedBy" class="form-label">
                {{ t('inventory.performedBy') }} <span class="required">*</span>
              </label>
              <input
                id="adjust-performedBy"
                v-model="form.performedBy"
                type="text"
                class="form-input"
                placeholder="e.g., John Smith"
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
              class="btn btn--primary"
              :disabled="loading || !isValid"
              @click="handleSubmit"
            >
              <span v-if="loading" class="spinner"></span>
              {{ loading ? t('common.loading') : t('inventory.adjustStock') }}
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

.form-hint--positive {
  color: var(--color-success);
  font-weight: 500;
}

.form-hint--negative {
  color: var(--color-danger);
  font-weight: 500;
}

/* Spinner */
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

.fade-enter-active .dialog,
.fade-leave-active .dialog {
  transition: transform 0.2s ease;
}

.fade-enter-from .dialog,
.fade-leave-to .dialog {
  transform: scale(0.95);
}</style>
