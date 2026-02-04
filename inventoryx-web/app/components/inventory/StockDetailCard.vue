<template>
  <div class="stock-card">
    <div class="stock-card__header">
      <span class="stock-card__sku">{{ stock.sku }}</span>
      <span class="stock-card__location">📍 {{ stock.locationId }}</span>
    </div>

    <div class="stock-card__body">
      <div class="quantity-grid">
        <div class="quantity-item quantity-item--available">
          <span class="quantity-item__label">{{
            t("inventory.availableQuantity")
          }}</span>
          <span class="quantity-item__value">{{
            formatQuantity(stock.availableQuantity)
          }}</span>
        </div>
        <div class="quantity-item quantity-item--reserved">
          <span class="quantity-item__label">{{
            t("inventory.reservedQuantity")
          }}</span>
          <span class="quantity-item__value">{{
            formatQuantity(stock.reservedQuantity)
          }}</span>
        </div>
        <div class="quantity-item">
          <span class="quantity-item__label">{{
            t("inventory.unitOfMeasure")
          }}</span>
          <span class="quantity-item__value">{{ stock.unitOfMeasure }}</span>
        </div>
      </div>
    </div>

    <div class="stock-card__actions">
      <button
        class="btn btn--secondary"
        :disabled="parseFloat(stock.availableQuantity) <= 0"
        @click="$emit('withdraw', stock)"
      >
        📦 {{ t("inventory.withdrawStock") }}
      </button>
      <button
        class="btn btn--secondary"
        :disabled="parseFloat(stock.availableQuantity) <= 0"
        @click="$emit('reserve', stock)"
      >
        🔒 {{ t("inventory.reserveStock") }}
      </button>
      <button
        class="btn btn--secondary"
        :disabled="parseFloat(stock.availableQuantity) <= 0"
        @click="$emit('quick-sale', stock)"
      >
        💰 {{ t("inventory.quickSale") }}
      </button>
      <button class="btn btn--ghost" @click="$emit('adjust', stock)">
        📝 {{ t("inventory.adjustStock") }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Stock } from "~/types/inventory";

const props = defineProps<{
  stock: Stock;
}>();

const emit = defineEmits<{
  withdraw: [stock: Stock];
  reserve: [stock: Stock];
  "quick-sale": [stock: Stock];
  adjust: [stock: Stock];
}>();

const { t } = useI18n();

import { formatQuantity } from "~/utils/format";
</script>

<style scoped>
/* Stock Card */
.stock-card {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: 2rem;
  transition: var(--theme-transition);
  box-shadow: var(--shadow-md);
}

.stock-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid var(--glass-border);
}

.stock-card__sku {
  font-family: monospace;
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-primary);
}

.stock-card__location {
  color: var(--color-text-secondary);
  font-size: 0.9375rem;
}

.stock-card__body {
  padding: 1.5rem;
}

.quantity-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}

.quantity-item {
  text-align: center;
  padding: 1rem;
  background: var(--color-surface);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

.quantity-item__label {
  display: block;
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--color-text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.5rem;
}

.quantity-item__value {
  display: block;
  font-size: 1.5rem;
  font-weight: 600;
  font-family: monospace;
  color: var(--color-text-primary);
}

.quantity-item--available .quantity-item__value {
  color: var(--color-success);
}

.quantity-item--reserved .quantity-item__value {
  color: var(--color-warning);
}

.stock-card__actions {
  display: flex;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid var(--glass-border);
  background: rgba(255, 255, 255, 0.05);
}

/* Responsive */
@media (max-width: 640px) {
  .quantity-grid {
    grid-template-columns: 1fr;
  }

  .stock-card__header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .stock-card__actions {
    flex-direction: column;
  }
}
</style>
