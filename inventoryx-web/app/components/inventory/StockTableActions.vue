<script setup lang="ts">
/**
 * StockTableActions Component
 * 
 * Renders action buttons for a stock row: Withdraw, Quick Sale, Reserve,
 * and a dropdown for additional actions (Release, Confirm, Adjust, History).
 */
import type { Stock } from "~/types/inventory";
import IconWithdraw from "~/components/icons/IconWithdraw.vue";
import IconSale from "~/components/icons/IconSale.vue";
import IconLock from "~/components/icons/IconLock.vue";
import IconUnlock from "~/components/icons/IconUnlock.vue";
import IconCheck from "~/components/icons/IconCheck.vue";
import IconEdit from "~/components/icons/IconEdit.vue";
import IconHistory from "~/components/icons/IconHistory.vue";

const { t } = useI18n();

const props = defineProps<{
  stock: Stock;
}>();

const emit = defineEmits<{
  withdraw: [stock: Stock];
  quickSale: [stock: Stock];
  reserve: [stock: Stock];
  release: [stock: Stock];
  confirm: [stock: Stock];
  adjust: [stock: Stock];
  viewHistory: [stock: Stock];
}>();

const availableQty = computed(() => parseFloat(props.stock.availableQuantity || "0"));
const reservedQty = computed(() => parseFloat(props.stock.reservedQuantity || "0"));
</script>

<template>
  <div class="action-group">
    <!-- Primary Actions -->
    <button
      class="action-btn action-btn--primary"
      :disabled="availableQty <= 0"
      :title="t('inventory.withdrawStock')"
      @click="emit('withdraw', stock)"
    >
      <IconWithdraw />
      {{ t("inventory.withdrawStock") }}
    </button>

    <button
      class="action-btn action-btn--success"
      :disabled="availableQty <= 0"
      :title="t('inventory.quickSale')"
      @click="emit('quickSale', stock)"
    >
      <IconSale />
      {{ t("inventory.quickSale") }}
    </button>

    <button
      class="action-btn action-btn--warning"
      :disabled="availableQty <= 0"
      :title="t('inventory.reserveStock')"
      @click="emit('reserve', stock)"
    >
      <IconLock />
      {{ t("inventory.reserveStock") }}
    </button>

    <!-- More Actions Dropdown -->
    <CommonActionDropdown>
      <CommonActionDropdownItem
        :disabled="reservedQty <= 0"
        @click="emit('release', stock)"
      >
        <template #icon>
          <IconUnlock />
        </template>
        {{ t("inventory.releaseReservation") }}
      </CommonActionDropdownItem>

      <CommonActionDropdownItem
        :disabled="reservedQty <= 0"
        @click="emit('confirm', stock)"
      >
        <template #icon>
          <IconCheck />
        </template>
        {{ t("inventory.confirmReservation") }}
      </CommonActionDropdownItem>

      <div class="dropdown-divider"></div>

      <CommonActionDropdownItem @click="emit('adjust', stock)">
        <template #icon>
          <IconEdit />
        </template>
        {{ t("inventory.adjustStock") }}
      </CommonActionDropdownItem>

      <div class="dropdown-divider"></div>

      <CommonActionDropdownItem @click="emit('viewHistory', stock)">
        <template #icon>
          <IconHistory />
        </template>
        {{ t("inventory.movementHistory") }}
      </CommonActionDropdownItem>
    </CommonActionDropdown>
  </div>
</template>

<style scoped>
.action-group {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  font-size: 0.75rem;
  font-weight: 500;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.action-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.action-btn--primary {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.action-btn--primary:hover:not(:disabled) {
  background: rgba(59, 130, 246, 0.2);
}

.action-btn--success {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.action-btn--success:hover:not(:disabled) {
  background: rgba(16, 185, 129, 0.2);
}

.action-btn--warning {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.action-btn--warning:hover:not(:disabled) {
  background: rgba(245, 158, 11, 0.2);
}

.dropdown-divider {
  height: 1px;
  margin: 6px 0;
  background: var(--color-border);
}
</style>
