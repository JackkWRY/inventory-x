<script setup lang="ts">
import type {
  Stock,
  StockMovement,
  ReceiveStockCommand,
} from "~/types/inventory";
import { useInventoryStore } from "~/stores/inventory";
import { useInventoryApi } from "~/composables/api/useInventoryApi";
import { useToastStore } from "~/stores/toast";

/**
 * Dashboard Overview Page
 *
 * Displays KPIs and summary information:
 * - Total SKUs
 * - Total Available Quantity
 * - Low Stock Count (threshold: 10)
 * - Recent Movements
 *
 * @route /dashboard
 */

// Page Meta
definePageMeta({
  title: "Dashboard",
});

// i18n
const { t } = useI18n();

// Store
const inventoryStore = useInventoryStore();
const toast = useToastStore();

// State
const loading = ref(true);
const error = ref<string | null>(null);
const stocks = ref<Stock[]>([]);
const recentMovements = ref<StockMovement[]>([]);

// Receive Stock Dialog State
const showReceiveDialog = ref(false);
const receiveLoading = ref(false);
const receiveError = ref<string | null>(null);

// Configuration - with localStorage persistence
const DEFAULT_THRESHOLD = 10;
const lowStockThreshold = ref(DEFAULT_THRESHOLD);
const showSettings = ref(false);
const thresholdInput = ref(DEFAULT_THRESHOLD.toString());

// Load threshold from localStorage on mount
onMounted(async () => {
  loadThresholdFromStorage();
  await fetchDashboardData();
});

/**
 * Load threshold from localStorage
 */
function loadThresholdFromStorage() {
  if (import.meta.client) {
    const saved = localStorage.getItem("inventoryx_low_stock_threshold");
    if (saved) {
      const parsed = parseFloat(saved);
      if (!isNaN(parsed) && parsed > 0) {
        lowStockThreshold.value = parsed;
        thresholdInput.value = parsed.toString();
      }
    }
  }
}

/**
 * Save threshold to localStorage
 */
function saveThreshold() {
  const value = parseFloat(thresholdInput.value);
  if (!isNaN(value) && value > 0) {
    lowStockThreshold.value = value;
    if (import.meta.client) {
      localStorage.setItem("inventoryx_low_stock_threshold", value.toString());
    }
    showSettings.value = false;
  }
}

/**
 * Reset threshold to default
 */
function resetThreshold() {
  lowStockThreshold.value = DEFAULT_THRESHOLD;
  thresholdInput.value = DEFAULT_THRESHOLD.toString();
  if (import.meta.client) {
    localStorage.removeItem("inventoryx_low_stock_threshold");
  }
  showSettings.value = false;
}

/**
 * Fetch dashboard data
 */
async function fetchDashboardData() {
  loading.value = true;
  error.value = null;

  try {
    // Fetch all stocks for KPIs
    await inventoryStore.fetchStocks();
    stocks.value = inventoryStore.stocks;

    // Fetch recent movements (mock for now - will use real API when available)
    recentMovements.value = await fetchRecentMovements();
  } catch (e) {
    error.value = e instanceof Error ? e.message : t("messages.loadError");
  } finally {
    loading.value = false;
  }
}

/**
 * Fetch recent movements from multiple stocks
 */
async function fetchRecentMovements(): Promise<StockMovement[]> {
  const api = useInventoryApi();
  const allMovements: StockMovement[] = [];

  // Get movements from first 5 stocks
  const stocksToCheck = stocks.value.slice(0, 5);

  for (const stock of stocksToCheck) {
    try {
      const movements = await api.getStockMovements(stock.id);
      allMovements.push(...movements.slice(0, 2));
    } catch {
      // Skip if no movements
    }
  }

  // Sort by date (newest first) and take top 5
  return allMovements
    .sort(
      (a, b) =>
        new Date(b.performedAt).getTime() - new Date(a.performedAt).getTime()
    )
    .slice(0, 5);
}

// Computed KPIs
const totalSKUs = computed(() => stocks.value.length);

const totalAvailableQty = computed(() => {
  return stocks.value.reduce((sum, stock) => {
    return sum + parseFloat(stock.availableQuantity || "0");
  }, 0);
});

const totalReservedQty = computed(() => {
  return stocks.value.reduce((sum, stock) => {
    return sum + parseFloat(stock.reservedQuantity || "0");
  }, 0);
});

const lowStockItems = computed(() => {
  return stocks.value.filter((stock) => {
    const qty = parseFloat(stock.availableQuantity || "0");
    return qty > 0 && qty <= lowStockThreshold.value;
  });
});

const outOfStockItems = computed(() => {
  return stocks.value.filter((stock) => {
    const qty = parseFloat(stock.availableQuantity || "0");
    return qty <= 0;
  });
});

// Helpers
const formatNumber = (value: number): string => {
  return value.toLocaleString("en-US", { maximumFractionDigits: 2 });
};

const formatDate = (isoDate: string): string => {
  const date = new Date(isoDate);
  return (
    date.toLocaleDateString() +
    " " +
    date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
  );
};

const getMovementIcon = (type: string): string => {
  const icons: Record<string, string> = {
    RECEIPT: "📥",
    RESERVATION: "🔒",
    RELEASE: "🔓",
    CONFIRMATION: "✅",
    SALE: "💰",
    ADJUSTMENT: "🔧",
    RETURN: "↩️",
  };
  return icons[type] || "📋";
};

// Receive Stock handlers
const handleOpenReceiveDialog = () => {
  receiveError.value = null;
  showReceiveDialog.value = true;
};

const handleCloseReceiveDialog = () => {
  showReceiveDialog.value = false;
};

const handleReceiveSubmit = async (command: ReceiveStockCommand) => {
  receiveLoading.value = true;
  receiveError.value = null;

  try {
    await inventoryStore.receiveStock(command);
    showReceiveDialog.value = false;
    toast.success(t("toast.stockReceived"));
    // Refresh dashboard data
    await fetchDashboardData();
  } catch (e) {
    receiveError.value =
      e instanceof Error ? e.message : t("toast.operationFailed");
    toast.error(t("toast.operationFailed"));
  } finally {
    receiveLoading.value = false;
  }
};
</script>

<template>
  <div class="dashboard-page">
    <!-- Header -->
    <header class="page-header">
      <div class="page-header__nav">
        <h1 class="page-header__title">{{ t("dashboard.title") }}</h1>
        <div class="header-controls">
          <CommonThemeToggle />
          <CommonLanguageSwitcher />
          <NuxtLink to="/" class="btn btn--ghost">
            ← {{ t("common.back") }}
          </NuxtLink>
        </div>
      </div>
      <p class="page-header__subtitle">{{ t("dashboard.subtitle") }}</p>
    </header>

    <!-- Loading -->
    <div v-if="loading" class="loading-state">
      <span class="spinner"></span>
      {{ t("common.loading") }}
    </div>

    <!-- Error -->
    <div v-else-if="error" class="error-state">
      <span class="error-icon">⚠️</span>
      <p>{{ error }}</p>
      <button class="btn btn--primary" @click="fetchDashboardData">
        {{ t("common.submit") }}
      </button>
    </div>

    <!-- Dashboard Content -->
    <template v-else>
      <!-- KPI Cards -->
      <div class="kpi-grid">
        <!-- Total SKUs -->
        <div class="kpi-card">
          <div class="kpi-card__icon">📦</div>
          <div class="kpi-card__content">
            <span class="kpi-card__value">{{ formatNumber(totalSKUs) }}</span>
            <span class="kpi-card__label">{{ t("dashboard.totalSKUs") }}</span>
          </div>
        </div>

        <!-- Total Available -->
        <div class="kpi-card kpi-card--success">
          <div class="kpi-card__icon">✅</div>
          <div class="kpi-card__content">
            <span class="kpi-card__value">{{
              formatNumber(totalAvailableQty)
            }}</span>
            <span class="kpi-card__label">{{
              t("dashboard.totalAvailable")
            }}</span>
          </div>
        </div>

        <!-- Total Reserved -->
        <div class="kpi-card kpi-card--warning">
          <div class="kpi-card__icon">🔒</div>
          <div class="kpi-card__content">
            <span class="kpi-card__value">{{
              formatNumber(totalReservedQty)
            }}</span>
            <span class="kpi-card__label">{{
              t("dashboard.totalReserved")
            }}</span>
          </div>
        </div>

        <!-- Low Stock -->
        <div
          class="kpi-card"
          :class="{ 'kpi-card--danger': lowStockItems.length > 0 }"
        >
          <div class="kpi-card__icon">⚠️</div>
          <div class="kpi-card__content">
            <span class="kpi-card__value">{{ lowStockItems.length }}</span>
            <span class="kpi-card__label">{{ t("dashboard.lowStock") }}</span>
          </div>
        </div>
      </div>

      <!-- Two Column Layout -->
      <div class="dashboard-grid">
        <!-- Low Stock Alerts -->
        <section class="dashboard-section">
          <div class="dashboard-section__header">
            <h2 class="dashboard-section__title">
              ⚠️ {{ t("dashboard.lowStockAlerts") }}
            </h2>
            <button class="settings-btn" @click="showSettings = !showSettings">
              ⚙️
            </button>
          </div>

          <!-- Settings Panel -->
          <div v-if="showSettings" class="settings-panel">
            <div class="settings-panel__row">
              <label class="settings-panel__label">{{
                t("dashboard.thresholdLabel")
              }}</label>
              <input
                v-model="thresholdInput"
                type="number"
                min="1"
                step="1"
                class="settings-panel__input"
              />
            </div>
            <div class="settings-panel__actions">
              <button class="btn btn--small btn--ghost" @click="resetThreshold">
                {{ t("dashboard.resetDefault") }}
              </button>
              <button
                class="btn btn--small btn--primary"
                @click="saveThreshold"
              >
                {{ t("common.save") }}
              </button>
            </div>
          </div>

          <div
            v-if="lowStockItems.length === 0"
            class="dashboard-section__empty"
          >
            {{ t("dashboard.noLowStock") }}
          </div>

          <div v-else class="alert-list">
            <NuxtLink
              v-for="stock in lowStockItems.slice(0, 5)"
              :key="stock.id"
              :to="`/inventory/${stock.id}`"
              class="alert-item"
            >
              <div class="alert-item__info">
                <span class="alert-item__sku">{{ stock.sku }}</span>
                <span class="alert-item__location">{{ stock.locationId }}</span>
              </div>
              <span class="alert-item__qty">{{
                formatNumber(parseFloat(stock.availableQuantity))
              }}</span>
            </NuxtLink>
          </div>
        </section>

        <!-- Recent Movements -->
        <section class="dashboard-section">
          <h2 class="dashboard-section__title">
            📋 {{ t("dashboard.recentMovements") }}
          </h2>

          <div
            v-if="recentMovements.length === 0"
            class="dashboard-section__empty"
          >
            {{ t("messages.noData") }}
          </div>

          <div v-else class="movement-list">
            <div
              v-for="movement in recentMovements"
              :key="movement.id"
              class="movement-item"
            >
              <span class="movement-item__icon">{{
                getMovementIcon(movement.movementType)
              }}</span>
              <div class="movement-item__content">
                <span class="movement-item__type">{{
                  t(`movementTypes.${movement.movementType}`)
                }}</span>
                <span
                  class="movement-item__qty"
                  :class="{ positive: movement.quantity.startsWith('+') }"
                >
                  {{ movement.quantity }}
                </span>
              </div>
              <span class="movement-item__date">{{
                formatDate(movement.performedAt)
              }}</span>
            </div>
          </div>
        </section>
      </div>

      <!-- Quick Actions -->
      <div class="quick-actions">
        <button
          class="btn btn--success btn--lg"
          @click="handleOpenReceiveDialog"
        >
          📥 {{ t("inventory.receiveStock") }}
        </button>
        <NuxtLink to="/inventory" class="btn btn--primary btn--lg">
          📦 {{ t("dashboard.viewInventory") }}
        </NuxtLink>
      </div>

      <!-- Receive Stock Dialog -->
      <InventoryReceiveStockDialog
        :open="showReceiveDialog"
        :loading="receiveLoading"
        :error="receiveError"
        @submit="handleReceiveSubmit"
        @close="handleCloseReceiveDialog"
      />
    </template>
  </div>
</template>

<style scoped>
.dashboard-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}

/* Header */
.page-header {
  margin-bottom: 2rem;
}

.page-header__nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.page-header__title {
  font-size: 1.75rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.page-header__subtitle {
  color: var(--color-text-secondary);
  margin: 0;
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* Loading & Error States */
.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  color: #6b7280;
}

.spinner {
  display: inline-block;
  width: 2rem;
  height: 2rem;
  border: 3px solid #e5e7eb;
  border-top-color: #1a73e8;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.error-state p {
  margin: 0 0 1rem;
  color: #ef4444;
}

/* KPI Grid */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.kpi-card {
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: var(--theme-transition);
}

.kpi-card--success {
  border-color: #10b981;
}

.kpi-card--warning {
  border-color: #f59e0b;
}

.kpi-card--danger {
  border-color: #ef4444;
}

.kpi-card__icon {
  font-size: 2rem;
}

.kpi-card__content {
  display: flex;
  flex-direction: column;
}

.kpi-card__value {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-text-primary);
  font-family: "SF Mono", "Consolas", monospace;
}

.kpi-card__label {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

/* Dashboard Grid */
.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.dashboard-section {
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  overflow: hidden;
  transition: var(--theme-transition);
}

.dashboard-section__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  padding-right: 1rem;
}

.dashboard-section__title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-text-primary);
  padding: 1rem 1.5rem;
  margin: 0;
}

.settings-btn {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
  transition: all 0.2s;
}

.settings-btn:hover {
  background: var(--color-surface-hover);
}

/* Settings Panel */
.settings-panel {
  padding: 1rem 1.5rem;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
}

.settings-panel__row {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.settings-panel__label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-primary);
}

.settings-panel__input {
  width: 80px;
  padding: 0.5rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.875rem;
  text-align: center;
  background: var(--color-card);
  color: var(--color-text-primary);
}

.settings-panel__input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.1);
}

.settings-panel__actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.btn--small {
  padding: 0.375rem 0.75rem;
  font-size: 0.75rem;
}

.dashboard-section__empty {
  padding: 2rem;
  text-align: center;
  color: var(--color-text-secondary);
}

/* Alert List */
.alert-list {
  padding: 0.5rem 0;
}

.alert-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1.5rem;
  text-decoration: none;
  border-bottom: 1px solid var(--color-border);
  transition: background 0.2s;
}

.alert-item:last-child {
  border-bottom: none;
}

.alert-item:hover {
  background: var(--color-surface-hover);
}

.alert-item__info {
  display: flex;
  flex-direction: column;
}

.alert-item__sku {
  font-family: "SF Mono", "Consolas", monospace;
  font-weight: 500;
  color: var(--color-primary);
}

.alert-item__location {
  font-size: 0.75rem;
  color: var(--color-text-secondary);
}

.alert-item__qty {
  font-family: "SF Mono", "Consolas", monospace;
  font-weight: 600;
  color: #ef4444;
}

/* Movement List */
.movement-list {
  padding: 0.5rem 0;
}

.movement-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

.movement-item:last-child {
  border-bottom: none;
}

.movement-item__icon {
  font-size: 1.25rem;
}

.movement-item__content {
  flex: 1;
  display: flex;
  justify-content: space-between;
}

.movement-item__type {
  font-weight: 500;
  color: var(--color-text-primary);
}

.movement-item__qty {
  font-family: "SF Mono", "Consolas", monospace;
  font-weight: 600;
  color: #ef4444;
}

.movement-item__qty.positive {
  color: #10b981;
}

.movement-item__date {
  font-size: 0.75rem;
  color: var(--color-text-secondary);
}

/* Quick Actions */
.quick-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

/* Buttons */
.btn {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
  border: none;
}

.btn--primary {
  background: #1a73e8;
  color: white;
}

.btn--primary:hover {
  background: #1557b0;
}

.btn--success {
  background: #10b981;
  color: white;
}

.btn--success:hover {
  background: #059669;
}

.btn--lg {
  padding: 1rem 1.5rem;
  font-size: 1rem;
}

.btn--ghost {
  background: transparent;
  color: var(--color-text-secondary);
}

.btn--ghost:hover {
  background: var(--color-surface-hover);
  color: var(--color-text-primary);
}

/* Responsive */
@media (max-width: 1024px) {
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .kpi-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>
