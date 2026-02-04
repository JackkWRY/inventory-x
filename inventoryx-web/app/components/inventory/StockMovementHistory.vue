<script setup lang="ts">
/**
 * StockMovementHistory Component
 * 
 * Displays movement history for a stock item in a modal dialog.
 * Uses BaseModal for consistent modal behavior.
 */
import { useDateFormat } from "@vueuse/core";
import { type StockMovement } from "~/types/inventory";
import BaseModal from "~/components/common/BaseModal.vue";
import { useInventoryApi } from "~/composables/api/useInventoryApi";
import { formatQuantity } from "~/utils/format";

const props = defineProps<{
  open: boolean;
  stockId: string;
  stockSku?: string;
}>();

const emit = defineEmits(["close"]);

const { t } = useI18n();
// const config = useRuntimeConfig(); // No longer needed
// const authStore = useAuthStore(); // No longer needed

const movements = ref<StockMovement[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);
const inventoryApi = useInventoryApi();

// Fetch movements when modal opens
watch(
  () => props.open,
  async (isOpen) => {
    if (isOpen && props.stockId) {
      await fetchMovements();
    }
  },
);

const fetchMovements = async () => {
  loading.value = true;
  error.value = null;
  try {
    movements.value = await inventoryApi.getStockMovements(props.stockId);
  } catch (err: unknown) {
    error.value = t("error.loadFailed");
    console.error("Failed to fetch movements", err);
  } finally {
    loading.value = false;
  }
};

const formatDate = (date: string) => {
  return useDateFormat(date, "DD/MM/YYYY HH:mm").value;
};

const getMovementTypeClass = (type: string) => {
  switch (type) {
    case "RECEIPT":
    case "CONFIRMATION":
    case "RELEASE":
      return "text-success";
    case "WITHDRAWAL":
    case "SALE":
    case "RESERVATION":
      return "text-warning";
    case "ADJUSTMENT":
      return "text-info";
    default:
      return "";
  }
};

const isNegative = (qty: string | number) => parseFloat(String(qty)) < 0;
const isPositive = (qty: string | number) => parseFloat(String(qty)) > 0;

const dialogTitle = computed(() => t("inventory.movementHistory"));
</script>

<template>
  <BaseModal
    :open="open"
    :title="dialogTitle"
    size="lg"
    @close="emit('close')"
  >
    <!-- Custom header with subtitle -->
    <template #header>
      <div class="dialog__header">
        <div>
          <h2 id="modal-title" class="dialog__title">{{ dialogTitle }}</h2>
          <p class="dialog__subtitle">{{ t("inventory.sku") }}: {{ stockSku }}</p>
        </div>
        <button class="dialog__close" @click="emit('close')" :aria-label="t('common.close')">
          &times;
        </button>
      </div>
    </template>

    <template #error>
      <CommonErrorBanner v-if="error" :message="error" />
    </template>

    <template #body>
      <div v-if="loading" class="loading-state">
        <span class="spinner"></span>
        {{ t("common.loading") }}
      </div>

      <div v-else-if="movements.length === 0" class="empty-state">
        {{ t("chart.noData") }}
      </div>

      <table v-else class="data-table data-table--compact">
        <thead>
          <tr>
            <th>{{ t("inventory.performedAt") }}</th>
            <th>{{ t("inventory.movementType") }}</th>
            <th class="text-right">{{ t("inventory.quantity") }}</th>
            <th>{{ t("inventory.reason") }}</th>
            <th>{{ t("inventory.performedBy") }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="movement in movements" :key="movement.id">
            <td>{{ formatDate(movement.performedAt) }}</td>
            <td>
              <span :class="getMovementTypeClass(movement.movementType)">
                {{ t(`movementTypes.${movement.movementType}`) }}
              </span>
            </td>
            <td class="text-right text-mono">
              <span
                :class="{
                  'text-danger': isNegative(movement.quantity),
                  'text-success': isPositive(movement.quantity),
                }"
              >
                {{ isPositive(movement.quantity) ? "+" : ""
                }}{{ formatQuantity(movement.quantity) }}
              </span>
            </td>
            <td>{{ movement.reason }}</td>
            <td>{{ movement.performedBy }}</td>
          </tr>
        </tbody>
      </table>
    </template>

    <template #footer>
      <button class="btn btn--secondary" @click="emit('close')">
        {{ t("common.close") }}
      </button>
    </template>
  </BaseModal>
</template>

<style scoped>
/* Subtitle for header */
.dialog__subtitle {
  margin: 0.25rem 0 0 0;
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

/* Loading and empty states */
.loading-state,
.empty-state {
  padding: 3rem;
  text-align: center;
  color: var(--color-text-secondary);
}

/* Text utilities */
.text-right {
  text-align: right;
}

.text-mono {
  font-family: "SF Mono", "Consolas", monospace;
}

.text-success {
  color: #10b981;
}

.text-danger {
  color: #ef4444;
}

.text-warning {
  color: #f59e0b;
}

.text-info {
  color: #3b82f6;
}

/* Compact table variant */
.data-table--compact th,
.data-table--compact td {
  padding: 0.75rem 1rem;
}
</style>
