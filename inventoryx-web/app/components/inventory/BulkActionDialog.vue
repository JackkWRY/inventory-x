<script setup lang="ts">
/**
 * BulkActionDialog Component - Dialog for bulk actions on multiple stocks.
 */

import type { Stock } from "~/types/inventory";
import BaseModal from "~/components/common/BaseModal.vue";

const { t } = useI18n();

// Props
interface Props {
  modelValue: boolean;
  actionType: "reserve" | "adjust";
  stocks: Stock[];
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

const isValid = computed(() => {
  if (props.actionType === "adjust") {
    return quantity.value !== 0;
  }
  return quantity.value > 0;
});

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
  <BaseModal
    :open="isVisible"
    :title="actionTitle"
    size="md"
    @close="handleClose"
  >
    <!-- Content Body -->
    <template #body>
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
        <label for="bulk-quantity" class="form-label">
          {{ actionType === "reserve" ? t("inventory.quantity") : t("inventory.adjustQuantity") }}
        </label>
        <input
          id="bulk-quantity"
          v-model.number="quantity"
          type="number"
          :min="actionType === 'reserve' ? 1 : undefined"
          class="form-input"
          :placeholder="actionType === 'adjust' ? t('inventory.adjustQuantityHint') : ''"
        />
        <small v-if="actionType === 'adjust'" class="form-hint">
          {{ t("inventory.adjustQuantityHint") }}
        </small>
      </div>

      <!-- Reason (for adjust only) -->
      <div v-if="actionType === 'adjust'" class="form-group">
        <label for="bulk-reason" class="form-label">{{ t("inventory.reason") }}</label>
        <input
          id="bulk-reason"
          v-model="reason"
          type="text"
          class="form-input"
          :placeholder="t('inventory.reasonPlaceholder')"
        />
      </div>
    </template>

    <!-- Footer -->
    <template #footer>
      <button class="btn btn--secondary" @click="handleCancel" :disabled="loading">
        {{ t("common.cancel") }}
      </button>
      <button
        class="btn"
        :class="actionType === 'reserve' ? 'btn--primary' : 'btn--warning'"
        :disabled="!isValid || loading"
        @click="handleConfirm"
      >
        <span v-if="loading" class="spinner"></span>
        {{ actionButtonText }}
      </button>
    </template>
  </BaseModal>
</template>

<style scoped>
/* Selected Summary */
.selected-summary {
  background: var(--color-surface);
  padding: 0.75rem 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  border: 1px solid var(--color-border);
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
  background: var(--color-surface);
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

.spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid currentColor;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 0.5rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>

