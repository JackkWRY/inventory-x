<script setup lang="ts">
/**
 * BulkActionDialog Component
 *
 * Dialog for performing bulk actions (reserve/adjust) on multiple stocks.
 * Shows list of selected items and allows user to input quantity.
 */

import type { Stock } from "~/types/inventory";

// i18n
const { t } = useI18n();

// Props
interface Props {
  /** Whether dialog is open */
  modelValue: boolean;
  /** Type of bulk action */
  actionType: "reserve" | "adjust";
  /** List of selected stocks */
  stocks: Stock[];
  /** Loading state */
  loading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
});

// Emits
const emit = defineEmits<{
  "update:modelValue": [value: boolean];
  confirm: [quantity: number, reason?: string];
  cancel: [];
}>();

// Form state
const quantity = ref<number>(1);
const reason = ref<string>("");

// Computed
const isVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});

const actionTitle = computed(() => {
  return props.actionType === "reserve"
    ? t("bulk.reserveTitle")
    : t("bulk.adjustTitle");
});

const actionButtonText = computed(() => {
  return props.actionType === "reserve"
    ? t("bulk.confirmReserve")
    : t("bulk.confirmAdjust");
});

// Validation
const isValid = computed(() => {
  if (props.actionType === "adjust") {
    return quantity.value !== 0; // Allow negative for adjust
  }
  return quantity.value > 0;
});

// Methods
const handleConfirm = () => {
  if (!isValid.value) return;
  emit("confirm", quantity.value, reason.value || undefined);
};

const handleCancel = () => {
  emit("cancel");
  isVisible.value = false;
};

const handleClose = () => {
  isVisible.value = false;
};

// Reset form when dialog opens
watch(
  () => props.modelValue,
  (open) => {
    if (open) {
      quantity.value = 1;
      reason.value = "";
    }
  }
);
</script>

<template>
  <Teleport to="body">
    <div v-if="isVisible" class="dialog-overlay" @click.self="handleClose">
      <div class="dialog" role="dialog" aria-modal="true">
        <!-- Header -->
        <div class="dialog__header">
          <h2 class="dialog__title">{{ actionTitle }}</h2>
          <button class="dialog__close" @click="handleClose" aria-label="Close">
            ✕
          </button>
        </div>

        <!-- Content -->
        <div class="dialog__content">
          <!-- Selected Items Summary -->
          <div class="selected-summary">
            <span class="selected-summary__count">
              {{ stocks.length }} {{ t("bulk.itemsSelected") }}
            </span>
          </div>

          <!-- Selected Items List -->
          <div class="selected-list">
            <div
              v-for="stock in stocks"
              :key="stock.id"
              class="selected-item"
            >
              <span class="selected-item__sku">{{ stock.sku }}</span>
              <span class="selected-item__location">{{ stock.locationId }}</span>
              <span class="selected-item__qty">
                {{ t("inventory.availableQuantity") }}: {{ stock.availableQuantity }}
              </span>
            </div>
          </div>

          <!-- Quantity Input -->
          <div class="form-group">
            <label for="bulk-quantity">
              {{ actionType === "reserve" ? t("inventory.quantity") : t("inventory.adjustQuantity") }}
            </label>
            <input
              id="bulk-quantity"
              v-model.number="quantity"
              type="number"
              :min="actionType === 'reserve' ? 1 : undefined"
              class="input"
              :placeholder="actionType === 'adjust' ? t('inventory.adjustQuantityHint') : ''"
            />
            <small v-if="actionType === 'adjust'" class="form-hint">
              {{ t("inventory.adjustQuantityHint") }}
            </small>
          </div>

          <!-- Reason (for adjust only) -->
          <div v-if="actionType === 'adjust'" class="form-group">
            <label for="bulk-reason">{{ t("inventory.reason") }}</label>
            <input
              id="bulk-reason"
              v-model="reason"
              type="text"
              class="input"
              :placeholder="t('inventory.reasonPlaceholder')"
            />
          </div>
        </div>

        <!-- Footer -->
        <div class="dialog__footer">
          <button class="btn btn--ghost" @click="handleCancel" :disabled="loading">
            {{ t("common.cancel") }}
          </button>
          <button
            class="btn"
            :class="actionType === 'reserve' ? 'btn--primary' : 'btn--warning'"
            :disabled="!isValid || loading"
            @click="handleConfirm"
          >
            <span v-if="loading" class="spinner-small"></span>
            {{ actionButtonText }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.dialog-overlay {
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
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.dialog__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
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
}

.dialog__close:hover {
  color: var(--color-text-primary);
}

.dialog__content {
  padding: 1.5rem;
  overflow-y: auto;
  flex: 1;
}

.dialog__footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid var(--color-border);
}

/* Selected Summary */
.selected-summary {
  background: var(--color-surface);
  padding: 0.75rem 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.selected-summary__count {
  font-weight: 600;
  color: var(--color-primary);
}

/* Selected Items List */
.selected-list {
  max-height: 150px;
  overflow-y: auto;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  margin-bottom: 1.5rem;
}

.selected-item {
  display: flex;
  gap: 1rem;
  padding: 0.5rem 1rem;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.875rem;
}

.selected-item:last-child {
  border-bottom: none;
}

.selected-item__sku {
  font-family: monospace;
  font-weight: 500;
  color: var(--color-primary);
  min-width: 120px;
}

.selected-item__location {
  color: var(--color-text-secondary);
  flex: 1;
}

.selected-item__qty {
  color: var(--color-text-secondary);
}

/* Form */
.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
}

.input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  font-size: 1rem;
  background: var(--color-surface);
  color: var(--color-text-primary);
}

.input:focus {
  outline: none;
  border-color: var(--color-primary);
}

.form-hint {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: var(--color-text-secondary);
}

/* Button styles */
.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn--primary {
  background: var(--color-primary);
  color: white;
}

.btn--primary:hover:not(:disabled) {
  background: var(--color-primary-hover);
}

.btn--warning {
  background: #f59e0b;
  color: white;
}

.btn--warning:hover:not(:disabled) {
  background: #d97706;
}

.btn--ghost {
  background: transparent;
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
}

.btn--ghost:hover:not(:disabled) {
  background: var(--color-surface-hover);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.spinner-small {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 0.5rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
