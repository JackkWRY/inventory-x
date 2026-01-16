<script setup lang="ts">
/**
 * TableSkeleton - Skeleton loading state for data tables.
 *
 * USE CASE: Show table loading state before data arrives
 *
 * PROPS:
 * - rows: Number of skeleton rows to display
 * - columns: Number of columns per row
 * - showHeader: Show header skeleton
 *
 * @example
 * ```vue
 * <TableSkeleton :rows="5" :columns="6" show-header />
 * ```
 */

interface Props {
  rows?: number
  columns?: number
  showHeader?: boolean
}

withDefaults(defineProps<Props>(), {
  rows: 5,
  columns: 6,
  showHeader: true
})
</script>

<template>
  <div class="table-skeleton" role="status" aria-label="Loading...">
    <!-- Header skeleton -->
    <div v-if="showHeader" class="table-skeleton__header">
      <div
        v-for="col in columns"
        :key="`header-${col}`"
        class="table-skeleton__header-cell"
      >
        <CommonSkeletonLoader variant="line" width="80%" height="14px" />
      </div>
    </div>

    <!-- Rows skeleton -->
    <div
      v-for="row in rows"
      :key="`row-${row}`"
      class="table-skeleton__row"
    >
      <!-- Checkbox column -->
      <div class="table-skeleton__cell table-skeleton__cell--checkbox">
        <CommonSkeletonLoader variant="rectangle" width="18px" height="18px" />
      </div>

      <!-- Data columns -->
      <div
        v-for="col in columns - 1"
        :key="`cell-${row}-${col}`"
        class="table-skeleton__cell"
      >
        <CommonSkeletonLoader
          variant="line"
          :width="col === 1 ? '120px' : col === columns - 2 ? '60px' : '80px'"
          height="16px"
        />
      </div>

      <!-- Actions column -->
      <div class="table-skeleton__cell table-skeleton__cell--actions">
        <CommonSkeletonLoader variant="rectangle" width="70px" height="28px" />
        <CommonSkeletonLoader variant="rectangle" width="70px" height="28px" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.table-skeleton {
  background: var(--color-surface, #fff);
  border-radius: 8px;
  overflow: hidden;
}

.table-skeleton__header {
  display: grid;
  grid-template-columns: 40px repeat(5, 1fr) 180px;
  gap: 16px;
  padding: 14px 16px;
  background: var(--color-surface-elevated, #f8f9fa);
  border-bottom: 1px solid var(--color-border, #e5e7eb);
}

.table-skeleton__header-cell {
  display: flex;
  align-items: center;
}

.table-skeleton__row {
  display: grid;
  grid-template-columns: 40px repeat(5, 1fr) 180px;
  gap: 16px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--color-border, #e5e7eb);
}

.table-skeleton__row:last-child {
  border-bottom: none;
}

.table-skeleton__cell {
  display: flex;
  align-items: center;
}

.table-skeleton__cell--checkbox {
  justify-content: center;
}

.table-skeleton__cell--actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

/* Dark mode */
@media (prefers-color-scheme: dark) {
  .table-skeleton {
    --color-surface: #1a1a2e;
    --color-surface-elevated: #16213e;
    --color-border: rgba(255, 255, 255, 0.1);
  }
}

[data-theme="dark"] .table-skeleton {
  --color-surface: #1a1a2e;
  --color-surface-elevated: #16213e;
  --color-border: rgba(255, 255, 255, 0.1);
}
</style>
