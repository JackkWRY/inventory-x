<script setup lang="ts">
/**
 * BulkActionBar Component
 * 
 * Reusable floating action bar for bulk operations on selected items.
 */
const { t } = useI18n();

defineProps<{
  selectedCount: number;
}>();

const emit = defineEmits<{
  clear: [];
  bulkReserve: [];
  bulkAdjust: [];
}>();
</script>

<template>
  <div v-if="selectedCount > 0" class="bulk-action-bar">
    <div class="bulk-action-bar__info">
      <span class="bulk-action-bar__count">
        {{ selectedCount }} {{ t("bulk.selected") }}
      </span>
      <button class="btn btn--ghost btn--small" @click="emit('clear')">
        {{ t("bulk.clearSelection") }}
      </button>
    </div>
    <div class="bulk-action-bar__actions">
      <button class="btn btn--secondary btn--small" @click="emit('bulkReserve')">
        📦 {{ t("bulk.reserveSelected") }}
      </button>
      <button class="btn btn--warning btn--small" @click="emit('bulkAdjust')">
        ✏️ {{ t("bulk.adjustSelected") }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.bulk-action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background: var(--color-primary);
  color: white;
  border-radius: 8px;
  margin-bottom: 1rem;
  gap: 1rem;
  flex-wrap: wrap;
}

.bulk-action-bar__info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.bulk-action-bar__count {
  font-weight: 600;
}

.bulk-action-bar__actions {
  display: flex;
  gap: 0.5rem;
}

.bulk-action-bar .btn--ghost {
  color: white;
  border-color: rgba(255, 255, 255, 0.3);
}

.bulk-action-bar .btn--ghost:hover {
  background: rgba(255, 255, 255, 0.1);
}
</style>
