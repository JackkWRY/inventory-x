<script setup lang="ts">
import type { Stock, ReserveStockCommand } from '~/types/inventory'
import BaseModal from '~/components/common/BaseModal.vue'

/**
 * ReserveStockDialog Component - Modal dialog for reserving stock for an order.
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
  submit: [command: ReserveStockCommand]
  close: []
}>()

// Form state
const form = reactive({
  quantity: '',
  orderId: ''
})

// Computed
const availableQuantity = computed(() => {
  return parseFloat(props.stock?.availableQuantity || '0')
})

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

const formatQuantity = (value: string): string => {
  const num = parseFloat(value || '0')
  return num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
</script>

<template>
  <BaseModal
    :open="open"
    :title="t('inventory.reserveStock')"
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
    </template>

    <!-- Form Body -->
    <template #body>
      <form @submit.prevent="handleSubmit">
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
        type="submit"
        class="btn btn--primary"
        :disabled="!isValid || loading"
        @click="handleSubmit"
      >
        <span v-if="loading" class="spinner"></span>
        {{ loading ? t('inventory.reserving') : t('inventory.reserveStock') }}
      </button>
    </template>
  </BaseModal>
</template>

<style scoped>
/* Component-specific styles only */
.stock-info__value--reserved {
  color: #9333ea;
}

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
</style>
