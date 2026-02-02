<script setup lang="ts">
/**
 * BulkActionDialog Component - Dialog for bulk actions on multiple stocks.
 * Refactored to use standard styles (no missing utility classes).
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
      <div style="background: var(--color-surface); padding: 0.75rem 1rem; border-radius: 8px; margin-bottom: 1rem; border: 1px solid var(--color-border);">
        <span style="font-weight: 600; color: var(--color-primary);">
          {{ stocks.length }} {{ t("bulk.itemsSelected") }}
        </span>
      </div>

      <!-- Selected Items List -->
      <div style="max-height: 150px; overflow-y: auto; border: 1px solid var(--color-border); border-radius: 8px; margin-bottom: 1.5rem; background: var(--color-surface);">
        <div
          v-for="stock in stocks"
          :key="stock.id"
          style="display: flex; gap: 1rem; padding: 0.5rem 1rem; border-bottom: 1px solid var(--color-border); font-size: 0.875rem;"
        >
          <span style="font-family: monospace; font-weight: 500; color: var(--color-primary); min-width: 120px;">{{ stock.sku }}</span>
          <span style="color: var(--color-text-secondary); flex: 1;">{{ stock.locationId }}</span>
          <span style="color: var(--color-text-secondary);">
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
        <span v-if="loading" class="spinner spinner--sm spinner--light"></span>
        {{ actionButtonText }}
      </button>
    </template>
  </BaseModal>
</template>

<!-- Scoped CSS removed - using inline styles for component-specific layout to avoid global pollution -->
