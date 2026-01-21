<script setup lang="ts">
import type {
  Stock,
  ReceiveStockCommand,
  ReserveStockCommand,
  ReleaseReservationCommand,
  ConfirmReservationCommand,
  AdjustStockCommand,
  WithdrawStockCommand,
  QuickSaleCommand,
} from "~/types/inventory";
import { useInventoryStore } from "~/stores/inventory";
import { useLocationStore } from "~/stores/location";
import { useToastStore } from "~/stores/toast";

/**
 * Inventory Page
 *
 * Main page for managing stock inventory.
 * Integrates StockList, ReceiveStockDialog, and ReserveStockDialog components.
 */

// Page meta
definePageMeta({
  title: "Inventory Management",
});

// i18n
const { t } = useI18n();

// Stores
const store = useInventoryStore();
const locationStore = useLocationStore();
const toast = useToastStore();

// Modal states
const showReceiveDialog = ref(false);
const showReserveDialog = ref(false);
const showReleaseDialog = ref(false);
const showConfirmDialog = ref(false);
const showAdjustDialog = ref(false);
const showWithdrawDialog = ref(false);
const showQuickSaleDialog = ref(false);
const selectedStock = ref<Stock | null>(null);

// Confirmation modal states
const showAdjustConfirmation = ref(false);
const showConfirmReservationConfirmation = ref(false);
const pendingAdjustCommand = ref<AdjustStockCommand | null>(null);
const pendingConfirmCommand = ref<ConfirmReservationCommand | null>(null);

// Bulk action states
const showBulkDialog = ref(false);
const bulkActionType = ref<"reserve" | "adjust">("reserve");
const bulkSelectedStocks = ref<Stock[]>([]);
const bulkLoading = ref(false);

// Component refs
const stockListRef = ref<{
  focusSearch: () => void;
  clearSelection: () => void;
} | null>(null);

// Keyboard shortcuts
useKeyboardShortcuts({
  onSearch: () => stockListRef.value?.focusSearch(),
  onNew: () => handleOpenReceiveDialog(),
});

// Computed
const stocks = computed(() => store.stocks);
const loading = computed(() => store.loading);
const error = computed(() => store.error);
const pagination = computed(() => store.pagination);

const locationMap = computed(() => {
  const map: Record<string, string> = {};
  locationStore.locations.forEach((loc) => {
    map[loc.id] = loc.name;
  });
  return map;
});

// Fetch stocks on mount (with pagination)
onMounted(async () => {
  await Promise.all([
    store.fetchStocksPaged(),
    locationStore.fetchLocations(), // Fetch all locations for mapping
  ]);
});

// Event handlers
const handleOpenReceiveDialog = () => {
  store.clearError();
  showReceiveDialog.value = true;
};

const handleCloseReceiveDialog = () => {
  showReceiveDialog.value = false;
};

const handleReceiveSubmit = async (command: ReceiveStockCommand) => {
  try {
    await store.receiveStock(command);
    showReceiveDialog.value = false;
    toast.success(t("toast.stockReceived"));
    // Refresh with pagination
    await store.fetchStocksPaged();
  } catch {
    toast.error(t("toast.operationFailed"));
  }
};

const handleOpenReserveDialog = (stock: Stock) => {
  store.clearError();
  selectedStock.value = stock;
  showReserveDialog.value = true;
};

const handleCloseReserveDialog = () => {
  showReserveDialog.value = false;
  selectedStock.value = null;
};

const handleReserveSubmit = async (command: ReserveStockCommand) => {
  try {
    await store.reserveStock(command);
    showReserveDialog.value = false;
    selectedStock.value = null;
    toast.success(t("toast.stockReserved"));
    await store.fetchStocksPaged();
  } catch {
    toast.error(t("toast.operationFailed"));
  }
};

// Release Reservation handlers
const handleOpenReleaseDialog = (stock: Stock) => {
  store.clearError();
  selectedStock.value = stock;
  showReleaseDialog.value = true;
};

const handleCloseReleaseDialog = () => {
  showReleaseDialog.value = false;
  selectedStock.value = null;
};

const handleReleaseSubmit = async (command: ReleaseReservationCommand) => {
  try {
    await store.releaseReservation(command);
    showReleaseDialog.value = false;
    selectedStock.value = null;
    toast.success(t("toast.stockReleased"));
    await store.fetchStocksPaged();
  } catch {
    toast.error(t("toast.operationFailed"));
  }
};

// Confirm Reservation handlers
const handleOpenConfirmDialog = (stock: Stock) => {
  store.clearError();
  selectedStock.value = stock;
  showConfirmDialog.value = true;
};

const handleCloseConfirmDialog = () => {
  showConfirmDialog.value = false;
  selectedStock.value = null;
};

// Confirm Reservation - with confirmation modal
const handleConfirmSubmit = async (command: ConfirmReservationCommand) => {
  // Store command and show confirmation
  pendingConfirmCommand.value = command;
  showConfirmReservationConfirmation.value = true;
};

const handleConfirmReservationConfirmed = async () => {
  if (!pendingConfirmCommand.value) return;

  try {
    await store.confirmReservation(pendingConfirmCommand.value);
    showConfirmDialog.value = false;
    showConfirmReservationConfirmation.value = false;
    selectedStock.value = null;
    pendingConfirmCommand.value = null;
    toast.success(t("toast.stockConfirmed"));
    await store.fetchStocksPaged();
  } catch {
    toast.error(t("toast.operationFailed"));
  }
};

const handleCancelConfirmReservation = () => {
  showConfirmReservationConfirmation.value = false;
  pendingConfirmCommand.value = null;
};

// Adjust Stock handlers
const handleOpenAdjustDialog = (stock: Stock) => {
  store.clearError();
  selectedStock.value = stock;
  showAdjustDialog.value = true;
};

const handleCloseAdjustDialog = () => {
  showAdjustDialog.value = false;
  selectedStock.value = null;
};

// Adjust Stock - with confirmation modal
const handleAdjustSubmit = async (command: AdjustStockCommand) => {
  // Store command and show confirmation
  pendingAdjustCommand.value = command;
  showAdjustConfirmation.value = true;
};

const handleAdjustConfirmed = async () => {
  if (!pendingAdjustCommand.value) return;

  try {
    await store.adjustStock(pendingAdjustCommand.value);
    showAdjustDialog.value = false;
    showAdjustConfirmation.value = false;
    selectedStock.value = null;
    pendingAdjustCommand.value = null;
    toast.success(t("toast.stockAdjusted"));
    await store.fetchStocksPaged();
  } catch {
    toast.error(t("toast.operationFailed"));
  }
};

const handleCancelAdjust = () => {
  showAdjustConfirmation.value = false;
  pendingAdjustCommand.value = null;
};

const handleViewStock = (stock: Stock) => {
  navigateTo(`/inventory/${stock.id}`);
};

// Withdraw Stock handlers
const handleOpenWithdrawDialog = (stock: Stock) => {
  store.clearError();
  selectedStock.value = stock;
  showWithdrawDialog.value = true;
};

const handleCloseWithdrawDialog = () => {
  showWithdrawDialog.value = false;
  selectedStock.value = null;
};

const handleWithdrawSubmit = async (command: WithdrawStockCommand) => {
  try {
    await store.withdrawStock(command);
    showWithdrawDialog.value = false;
    selectedStock.value = null;
    toast.success(t("toast.stockWithdrawn"));
    await store.fetchStocksPaged();
  } catch {
    toast.error(t("toast.operationFailed"));
  }
};

// Quick Sale handlers
const handleOpenQuickSaleDialog = (stock: Stock) => {
  store.clearError();
  selectedStock.value = stock;
  showQuickSaleDialog.value = true;
};

const handleCloseQuickSaleDialog = () => {
  showQuickSaleDialog.value = false;
  selectedStock.value = null;
};

const handleQuickSaleSubmit = async (command: QuickSaleCommand) => {
  try {
    await store.quickSale(command);
    showQuickSaleDialog.value = false;
    selectedStock.value = null;
    toast.success(t("toast.stockSold"));
    await store.fetchStocksPaged();
  } catch {
    toast.error(t("toast.operationFailed"));
  }
};

// Bulk action handlers
const handleBulkReserve = (stocks: Stock[]) => {
  bulkSelectedStocks.value = stocks;
  bulkActionType.value = "reserve";
  showBulkDialog.value = true;
};

const handleBulkAdjust = (stocks: Stock[]) => {
  bulkSelectedStocks.value = stocks;
  bulkActionType.value = "adjust";
  showBulkDialog.value = true;
};

const handleBulkConfirm = async (quantity: number, reason?: string) => {
  bulkLoading.value = true;
  let successCount = 0;
  let failCount = 0;

  for (const stock of bulkSelectedStocks.value) {
    try {
      if (bulkActionType.value === "reserve") {
        await store.reserveStock({
          sku: stock.sku,
          locationId: stock.locationId,
          quantity: quantity.toString(),
          orderId: `BULK-${Date.now()}-${Math.random()
            .toString(36)
            .slice(2, 7)}`,
        });
      } else {
        // For adjust, calculate new quantity
        const currentQty = parseFloat(stock.availableQuantity);
        const newQty = currentQty + quantity;
        await store.adjustStock({
          stockId: stock.id,
          newQuantity: newQty.toString(),
          reason: reason || "Bulk adjustment",
          performedBy: "User",
        });
      }
      successCount++;
    } catch {
      failCount++;
    }
  }

  bulkLoading.value = false;
  showBulkDialog.value = false;

  // Show result
  if (failCount === 0) {
    const msg =
      bulkActionType.value === "reserve"
        ? t("bulk.successReserve", { count: successCount })
        : t("bulk.successAdjust", { count: successCount });
    toast.success(msg);
  } else {
    toast.warning(
      t("bulk.errorPartial", { success: successCount, failed: failCount }),
    );
  }

  // Clear selection and refresh
  stockListRef.value?.clearSelection();
  await store.fetchStocksPaged();
};

const handleBulkCancel = () => {
  showBulkDialog.value = false;
  bulkSelectedStocks.value = [];
};

// Pagination handlers
const handlePageChange = async (page: number) => {
  await store.changePage(page);
};

const handlePageSizeChange = async (size: number) => {
  await store.changePageSize(size);
};
</script>

<template>
  <div class="inventory-page">
    <!-- Page Header -->
    <header class="page-header">
      <div class="page-header__content">
        <h1 class="page-header__title">{{ t("inventory.title") }}</h1>
        <p class="page-header__subtitle">{{ t("inventory.subtitle") }}</p>
      </div>
      <div class="page-header__actions">
        <NuxtLink to="/" class="btn btn--ghost">
          ← {{ t("common.back") }}
        </NuxtLink>
      </div>
    </header>

    <!-- Error Banner with retry functionality -->
    <CommonErrorBanner
      v-if="
        error &&
        !showReceiveDialog &&
        !showReserveDialog &&
        !showReleaseDialog &&
        !showConfirmDialog &&
        !showAdjustDialog &&
        !showWithdrawDialog &&
        !showQuickSaleDialog
      "
      :message="error"
      :title="t('error.loadFailed')"
      retryable
      dismissible
      @retry="store.fetchStocksPaged()"
      @dismiss="store.clearError()"
    />

    <!-- Stock List -->
    <InventoryStockList
      ref="stockListRef"
      :stocks="stocks"
      :loading="loading"
      :location-map="locationMap"
      @receive="handleOpenReceiveDialog"
      @withdraw="handleOpenWithdrawDialog"
      @reserve="handleOpenReserveDialog"
      @quick-sale="handleOpenQuickSaleDialog"
      @release="handleOpenReleaseDialog"
      @confirm="handleOpenConfirmDialog"
      @adjust="handleOpenAdjustDialog"
      @view="handleViewStock"
      @bulk-reserve="handleBulkReserve"
      @bulk-adjust="handleBulkAdjust"
    />

    <!-- Pagination -->
    <CommonPagination
      :current-page="pagination.currentPage"
      :total-pages="pagination.totalPages"
      :total-items="pagination.totalElements"
      :page-size="pagination.pageSize"
      :is-first="pagination.isFirst"
      :is-last="pagination.isLast"
      @page-change="handlePageChange"
      @page-size-change="handlePageSizeChange"
    />

    <!-- Receive Stock Dialog -->
    <InventoryReceiveStockDialog
      :open="showReceiveDialog"
      :loading="loading"
      :error="error"
      @submit="handleReceiveSubmit"
      @close="handleCloseReceiveDialog"
    />

    <!-- Reserve Stock Dialog -->
    <InventoryReserveStockDialog
      :open="showReserveDialog"
      :stock="selectedStock"
      :loading="loading"
      :error="error"
      @submit="handleReserveSubmit"
      @close="handleCloseReserveDialog"
    />

    <!-- Release Reservation Dialog -->
    <InventoryReleaseReservationDialog
      :open="showReleaseDialog"
      :stock="selectedStock"
      :loading="loading"
      :error="error"
      @submit="handleReleaseSubmit"
      @close="handleCloseReleaseDialog"
    />

    <!-- Confirm Reservation Dialog -->
    <InventoryConfirmReservationDialog
      :open="showConfirmDialog"
      :stock="selectedStock"
      :loading="loading"
      :error="error"
      @submit="handleConfirmSubmit"
      @close="handleCloseConfirmDialog"
    />

    <!-- Adjust Stock Dialog -->
    <InventoryAdjustStockDialog
      :open="showAdjustDialog"
      :stock="selectedStock"
      :loading="loading"
      :error="error"
      @submit="handleAdjustSubmit"
      @close="handleCloseAdjustDialog"
    />

    <!-- Withdraw Stock Dialog -->
    <InventoryWithdrawStockDialog
      :open="showWithdrawDialog"
      :stock="selectedStock"
      :loading="loading"
      :error="error"
      @submit="handleWithdrawSubmit"
      @close="handleCloseWithdrawDialog"
    />

    <!-- Quick Sale Dialog -->
    <InventoryQuickSaleDialog
      :open="showQuickSaleDialog"
      :stock="selectedStock"
      :loading="loading"
      :error="error"
      @submit="handleQuickSaleSubmit"
      @close="handleCloseQuickSaleDialog"
    />

    <!-- Adjust Stock Confirmation Modal -->
    <CommonConfirmDialog
      :show="showAdjustConfirmation"
      :title="t('confirm.adjustStockTitle')"
      :message="t('confirm.adjustStockMessage')"
      :confirm-text="t('confirm.proceedAction')"
      type="warning"
      :loading="loading"
      @confirm="handleAdjustConfirmed"
      @cancel="handleCancelAdjust"
    />

    <!-- Confirm Reservation Confirmation Modal -->
    <CommonConfirmDialog
      :show="showConfirmReservationConfirmation"
      :title="t('confirm.confirmReservationTitle')"
      :message="t('confirm.confirmReservationMessage')"
      :confirm-text="t('confirm.proceedAction')"
      type="danger"
      :loading="loading"
      @confirm="handleConfirmReservationConfirmed"
      @cancel="handleCancelConfirmReservation"
    />

    <!-- Bulk Action Dialog -->
    <InventoryBulkActionDialog
      v-model="showBulkDialog"
      :action-type="bulkActionType"
      :stocks="bulkSelectedStocks"
      :loading="bulkLoading"
      @confirm="handleBulkConfirm"
      @cancel="handleBulkCancel"
    />
  </div>
</template>

<style scoped>
.inventory-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.5rem;
  gap: 1rem;
  flex-wrap: wrap;
}

.page-header__content {
  flex: 1;
}

.page-header__title {
  font-size: 1.75rem;
  font-weight: 500;
  color: var(--color-text-primary);
  margin: 0 0 0.25rem 0;
}

.page-header__subtitle {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  margin: 0;
}

.page-header__actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-shrink: 0;
}

.error-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 6px;
  color: #dc2626;
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
}

.error-banner__close {
  background: none;
  border: none;
  color: #dc2626;
  cursor: pointer;
  padding: 0.25rem;
  font-size: 1rem;
  line-height: 1;
  border-radius: 4px;
  transition: background 0.2s;
}

.error-banner__close:hover {
  background: rgba(220, 38, 38, 0.1);
}

/* Button Styles */
.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.btn--ghost {
  background: transparent;
  color: var(--color-text-secondary);
}

.btn--ghost:hover {
  background: var(--color-surface-hover);
  color: var(--color-text-primary);
}
</style>
