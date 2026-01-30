<script setup lang="ts">
import type { Stock, ReleaseReservationCommand } from '~/types/inventory'
import BaseModal from '~/components/common/BaseModal.vue'

/**
 * ReleaseReservationDialog Component - Modal for releasing/canceling stock reservation.
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
  submit: [command: ReleaseReservationCommand]
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
    return `Cannot release more than reserved (${reservedQuantity.value})`
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

const handleSubmit = () => {
  if (!isValid.value || !props.stock) return

  const command: ReleaseReservationCommand = {
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
</script>

<template>
  <BaseModal
    :open="open"
    :title="t('inventory.releaseReservation')"
    size="md"
    @close="emit('close')"
  >
    <!-- Error -->
    <template #error>
      <div v-if="error" class="dialog__error">{{ error }}</div>
    </template>

    <!-- Stock Info -->
    <template #info>
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
    </template>

    <!-- Form Body -->
    <template #body>
      <form @submit.prevent="handleSubmit">
        <!-- Quantity -->
        <div class="form-group">
          <label for="release-quantity" class="form-label">
            {{ t('inventory.quantity') }} <span class="required">*</span>
          </label>
          <input
            id="release-quantity"
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
          <label for="release-orderId" class="form-label">
            {{ t('inventory.orderId') }} <span class="required">*</span>
          </label>
          <input
            id="release-orderId"
            v-model="form.orderId"
            type="text"
            class="form-input"
            placeholder="e.g., ORD-2024-001"
            :disabled="loading"
            required
          />
          <span class="form-hint">{{ t('inventory.orderReference') }}</span>
        </div>
      </form>
    </template>

    <!-- Footer -->
    <template #footer>
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
        class="btn btn--warning"
        :disabled="loading || !isValid"
        @click="handleSubmit"
      >
        <span v-if="loading" class="spinner"></span>
        {{ loading ? t('common.loading') : t('inventory.releaseReservation') }}
      </button>
    </template>
  </BaseModal>
</template>

<style scoped>
/* Component-specific styles only */
.stock-info {
  padding: 1rem 1.5rem;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  margin: 0;
  border-radius: 0;
}

.stock-info__value--reserved {
  color: #f59e0b;
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
</style>
