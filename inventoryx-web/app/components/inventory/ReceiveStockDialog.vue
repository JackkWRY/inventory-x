<script setup lang="ts">
import type { ReceiveStockCommand } from '~/types/inventory'

/**
 * ReceiveStockDialog Component
 *
 * Modal dialog for receiving stock into warehouse.
 * Includes form validation and submission handling.
 *
 * @example
 * <ReceiveStockDialog
 *   :open="isOpen"
 *   :loading="isLoading"
 *   @submit="handleSubmit"
 *   @close="handleClose"
 * />
 */

// Props
interface Props {
  /** Whether dialog is open */
  open: boolean
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
  submit: [command: ReceiveStockCommand]
  /** Triggered when dialog should close */
  close: []
}>()

// Form state
const form = reactive({
  sku: '',
  locationId: '',
  quantity: '',
  unitOfMeasure: 'PIECE',
  reason: '',
  performedBy: ''
})

// Unit options
const unitOptions = [
  { value: 'PIECE', label: 'Piece' },
  { value: 'KILOGRAM', label: 'Kilogram' },
  { value: 'LITER', label: 'Liter' },
  { value: 'METER', label: 'Meter' },
  { value: 'BOX', label: 'Box' },
  { value: 'PACK', label: 'Pack' }
]

// Validation
const isValid = computed(() => {
  return (
    form.sku.trim().length >= 3 &&
    form.locationId.trim().length > 0 &&
    parseFloat(form.quantity) > 0 &&
    form.unitOfMeasure.length > 0 &&
    form.performedBy.trim().length > 0
  )
})

// Handle form submission
const handleSubmit = () => {
  if (!isValid.value) return

  const command: ReceiveStockCommand = {
    sku: form.sku.trim().toUpperCase(),
    locationId: form.locationId.trim(),
    quantity: form.quantity,
    unitOfMeasure: form.unitOfMeasure,
    reason: form.reason.trim(),
    performedBy: form.performedBy.trim()
  }

  emit('submit', command)
}

// Reset form when dialog opens
watch(() => props.open, (isOpen) => {
  if (isOpen) {
    form.sku = ''
    form.locationId = ''
    form.quantity = ''
    form.unitOfMeasure = 'PIECE'
    form.reason = ''
    form.performedBy = ''
  }
})

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
            <h2 id="dialog-title" class="dialog__title">Receive Stock</h2>
            <button class="dialog__close" @click="emit('close')" aria-label="Close dialog">
              ✕
            </button>
          </div>

          <!-- Error Message -->
          <div v-if="error" class="dialog__error">
            {{ error }}
          </div>

          <!-- Form -->
          <form class="dialog__body" @submit.prevent="handleSubmit">
            <!-- SKU -->
            <div class="form-group">
              <label for="sku" class="form-label">
                SKU <span class="required">*</span>
              </label>
              <input
                id="sku"
                v-model="form.sku"
                type="text"
                class="form-input"
                placeholder="e.g., PROD-001"
                :disabled="loading"
                required
                minlength="3"
                maxlength="20"
              />
              <span class="form-hint">3-20 characters, alphanumeric and hyphens</span>
            </div>

            <!-- Location -->
            <div class="form-group">
              <label for="location" class="form-label">
                Location <span class="required">*</span>
              </label>
              <input
                id="location"
                v-model="form.locationId"
                type="text"
                class="form-input"
                placeholder="e.g., WH-A-01"
                :disabled="loading"
                required
              />
            </div>

            <!-- Quantity and Unit -->
            <div class="form-row">
              <div class="form-group">
                <label for="quantity" class="form-label">
                  Quantity <span class="required">*</span>
                </label>
                <input
                  id="quantity"
                  v-model="form.quantity"
                  type="number"
                  class="form-input"
                  placeholder="0"
                  min="0.01"
                  step="0.01"
                  :disabled="loading"
                  required
                />
              </div>
              <div class="form-group">
                <label for="unit" class="form-label">
                  Unit <span class="required">*</span>
                </label>
                <select
                  id="unit"
                  v-model="form.unitOfMeasure"
                  class="form-input"
                  :disabled="loading"
                  required
                >
                  <option v-for="unit in unitOptions" :key="unit.value" :value="unit.value">
                    {{ unit.label }}
                  </option>
                </select>
              </div>
            </div>

            <!-- Reason -->
            <div class="form-group">
              <label for="reason" class="form-label">Reason</label>
              <textarea
                id="reason"
                v-model="form.reason"
                class="form-input form-textarea"
                placeholder="e.g., Supplier delivery, Initial stock"
                :disabled="loading"
                rows="2"
              ></textarea>
            </div>

            <!-- Performed By -->
            <div class="form-group">
              <label for="performedBy" class="form-label">
                Performed By <span class="required">*</span>
              </label>
              <input
                id="performedBy"
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
              Cancel
            </button>
            <button
              type="submit"
              class="btn btn--primary"
              :disabled="!isValid || loading"
              @click="handleSubmit"
            >
              <span v-if="loading" class="spinner"></span>
              {{ loading ? 'Saving...' : 'Receive Stock' }}
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
  padding: 1rem;
}

.dialog {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
  max-width: 480px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dialog__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #e0e0e0;
}

.dialog__title {
  font-size: 1.25rem;
  font-weight: 500;
  margin: 0;
  color: #1a1a1a;
}

.dialog__close {
  background: none;
  border: none;
  font-size: 1.25rem;
  color: #666;
  cursor: pointer;
  padding: 0.25rem;
  line-height: 1;
  border-radius: 4px;
  transition: background 0.2s;
}

.dialog__close:hover {
  background: #f0f0f0;
}

.dialog__error {
  margin: 1rem 1.5rem 0;
  padding: 0.75rem 1rem;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 4px;
  color: #dc2626;
  font-size: 0.875rem;
}

.dialog__body {
  padding: 1.5rem;
  overflow-y: auto;
}

.dialog__footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #e0e0e0;
  background: #f8f9fa;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.375rem;
}

.required {
  color: #dc2626;
}

.form-input {
  width: 100%;
  padding: 0.625rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.875rem;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.1);
}

.form-input:disabled {
  background: #f3f4f6;
  cursor: not-allowed;
}

.form-textarea {
  resize: vertical;
  min-height: 60px;
}

.form-hint {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: #6b7280;
}

/* Buttons */
.btn {
  padding: 0.625rem 1.25rem;
  border: none;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn:disabled {
  opacity: 0.6;
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
