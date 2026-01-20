<script setup lang="ts">
import { useDateFormat } from "@vueuse/core";
import { type StockMovement } from "~/types/inventory";

const props = defineProps<{
  open: boolean;
  stockId: string;
  stockSku?: string;
}>();

const emit = defineEmits(["close"]);

const { t } = useI18n();
const config = useRuntimeConfig();
const authStore = useAuthStore();

const movements = ref<StockMovement[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);

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
    const response = await $fetch<StockMovement[]>(
      `/stocks/${props.stockId}/movements`,
      {
        baseURL: config.public.apiBaseUrl,
        headers: {
          Authorization: `Bearer ${authStore.token}`,
        },
      },
    );
    movements.value = response;
  } catch (err: any) {
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
      return "text-warning"; // Reservation is warning/pending
    case "ADJUSTMENT":
      return "text-info";
    default:
      return "";
  }
};

const formatQuantity = (qty: string | number) => {
  const value = typeof qty === "string" ? parseFloat(qty) : qty;
  return new Intl.NumberFormat("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value);
};

const isNegative = (qty: string) => parseFloat(qty) < 0;
const isPositive = (qty: string) => parseFloat(qty) > 0;
</script>

<template>
  <div v-if="open" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal">
      <div class="modal-header">
        <div>
          <h3>{{ t("inventory.movementHistory") }}</h3>
          <p class="subtitle">{{ t("inventory.sku") }}: {{ stockSku }}</p>
        </div>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <div class="modal-body">
        <CommonErrorBanner v-if="error" :message="error" />

        <div v-if="loading" class="loading-state">
          <span class="spinner"></span>
          {{ t("common.loading") }}
        </div>

        <div v-else-if="movements.length === 0" class="empty-state">
          {{ t("chart.noData") }}
        </div>

        <table v-else class="history-table">
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
              <td class="text-right font-mono">
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
      </div>

      <div class="modal-actions">
        <button class="btn btn--secondary" @click="$emit('close')">
          {{ t("common.close") }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  width: 100%;
  max-width: 800px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  max-height: 90vh;
}

.modal-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.125rem;
  color: #111827;
}

.subtitle {
  margin: 0.25rem 0 0 0;
  font-size: 0.875rem;
  color: #6b7280;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6b7280;
  padding: 0;
  margin-left: 1rem;
}

.modal-body {
  padding: 0;
  overflow-y: auto;
  flex: 1;
}

.loading-state,
.empty-state {
  padding: 3rem;
  text-align: center;
  color: #6b7280;
}

.history-table {
  width: 100%;
  border-collapse: collapse;
}

.history-table th {
  padding: 0.75rem 1.5rem;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  color: #6b7280;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.history-table td {
  padding: 0.75rem 1.5rem;
  font-size: 0.875rem;
  color: #374151;
  border-bottom: 1px solid #f3f4f6;
}

.text-right {
  text-align: right;
}

.font-mono {
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

.modal-actions {
  padding: 1rem 1.5rem;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  border: none;
}

.btn--secondary {
  background: white;
  border: 1px solid #d1d5db;
  color: #374151;
}

.btn--secondary:hover {
  background: #f9fafb;
}

.spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 0.5rem;
  vertical-align: middle;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
