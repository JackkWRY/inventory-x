<script setup lang="ts">
/**
 * Pagination Component
 *
 * Reusable pagination UI with page navigation and items per page selector.
 * Follows Best Practices:
 * - Props for data-in, emits for data-out
 * - i18n for all labels
 * - Keyboard accessible
 * - Responsive design
 *
 * @example
 * <CommonPagination
 *   :current-page="0"
 *   :total-pages="10"
 *   :total-items="95"
 *   :page-size="10"
 *   @page-change="handlePageChange"
 *   @page-size-change="handlePageSizeChange"
 * />
 */

// i18n
const { t } = useI18n()

// Props
interface Props {
  /** Current page (0-indexed) */
  currentPage: number
  /** Total number of pages */
  totalPages: number
  /** Total number of items */
  totalItems: number
  /** Current page size */
  pageSize: number
  /** Available page size options */
  pageSizeOptions?: number[]
  /** Whether first page */
  isFirst?: boolean
  /** Whether last page */
  isLast?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  pageSizeOptions: () => [10, 20, 50, 100],
  isFirst: false,
  isLast: false
})

// Emits
const emit = defineEmits<{
  pageChange: [page: number]
  pageSizeChange: [size: number]
}>()

// Computed
const startItem = computed(() => {
  if (props.totalItems === 0) return 0
  return props.currentPage * props.pageSize + 1
})

const endItem = computed(() => {
  const end = (props.currentPage + 1) * props.pageSize
  return Math.min(end, props.totalItems)
})

const canGoPrevious = computed(() => props.currentPage > 0)
const canGoNext = computed(() => props.currentPage < props.totalPages - 1)

// Page numbers to show (smart pagination)
const visiblePages = computed(() => {
  const pages: number[] = []
  const total = props.totalPages
  const current = props.currentPage

  if (total <= 7) {
    for (let i = 0; i < total; i++) pages.push(i)
  } else {
    // Always show first page
    pages.push(0)

    if (current > 3) pages.push(-1) // ellipsis

    // Show pages around current
    const start = Math.max(1, current - 1)
    const end = Math.min(total - 2, current + 1)

    for (let i = start; i <= end; i++) pages.push(i)

    if (current < total - 4) pages.push(-1) // ellipsis

    // Always show last page
    pages.push(total - 1)
  }

  return pages
})

// Methods
const goToPage = (page: number) => {
  if (page >= 0 && page < props.totalPages && page !== props.currentPage) {
    emit('pageChange', page)
  }
}

const goFirst = () => goToPage(0)
const goPrevious = () => goToPage(props.currentPage - 1)
const goNext = () => goToPage(props.currentPage + 1)
const goLast = () => goToPage(props.totalPages - 1)

const handlePageSizeChange = (event: Event) => {
  const target = event.target as HTMLSelectElement
  emit('pageSizeChange', parseInt(target.value, 10))
}
</script>

<template>
  <div class="pagination">
    <!-- Items Info -->
    <div class="pagination__info">
      <span v-if="totalItems > 0">
        {{ t('pagination.showing') }} {{ startItem }} {{ t('pagination.to') }} {{ endItem }}
        {{ t('pagination.of') }} {{ totalItems }} {{ t('pagination.items') }}
      </span>
      <span v-else>
        {{ t('messages.noData') }}
      </span>
    </div>

    <!-- Page Size Selector -->
    <div class="pagination__size">
      <label for="page-size">{{ t('pagination.perPage') }}:</label>
      <select
        id="page-size"
        :value="pageSize"
        class="pagination__select"
        @change="handlePageSizeChange"
      >
        <option v-for="size in pageSizeOptions" :key="size" :value="size">
          {{ size }}
        </option>
      </select>
    </div>

    <!-- Navigation -->
    <nav class="pagination__nav" aria-label="Pagination">
      <!-- First -->
      <button
        class="pagination__btn"
        :disabled="isFirst || !canGoPrevious"
        :aria-label="t('pagination.first')"
        @click="goFirst"
      >
        ⏮
      </button>

      <!-- Previous -->
      <button
        class="pagination__btn"
        :disabled="!canGoPrevious"
        :aria-label="t('pagination.previous')"
        @click="goPrevious"
      >
        ◀
      </button>

      <!-- Page Numbers -->
      <template v-for="(page, index) in visiblePages" :key="index">
        <span v-if="page === -1" class="pagination__ellipsis">...</span>
        <button
          v-else
          class="pagination__btn pagination__btn--page"
          :class="{ 'pagination__btn--active': page === currentPage }"
          :aria-current="page === currentPage ? 'page' : undefined"
          @click="goToPage(page)"
        >
          {{ page + 1 }}
        </button>
      </template>

      <!-- Next -->
      <button
        class="pagination__btn"
        :disabled="!canGoNext"
        :aria-label="t('pagination.next')"
        @click="goNext"
      >
        ▶
      </button>

      <!-- Last -->
      <button
        class="pagination__btn"
        :disabled="isLast || !canGoNext"
        :aria-label="t('pagination.last')"
        @click="goLast"
      >
        ⏭
      </button>
    </nav>
  </div>
</template>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.75rem 1.5rem;
  background: #f8f9fa;
  border-top: 1px solid #e0e0e0;
  font-size: 0.875rem;
  flex-wrap: wrap;
}

.pagination__info {
  color: #666;
  flex-shrink: 0;
}

.pagination__size {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
}

.pagination__select {
  padding: 0.25rem 0.5rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  font-size: 0.875rem;
  background: white;
  cursor: pointer;
}

.pagination__select:focus {
  outline: none;
  border-color: #1a73e8;
}

.pagination__nav {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.pagination__btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 2rem;
  height: 2rem;
  padding: 0 0.5rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  background: white;
  color: #333;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination__btn:hover:not(:disabled) {
  background: #e8f0fe;
  border-color: #1a73e8;
  color: #1a73e8;
}

.pagination__btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.pagination__btn--page {
  font-weight: 500;
}

.pagination__btn--active {
  background: #1a73e8;
  border-color: #1a73e8;
  color: white;
}

.pagination__btn--active:hover:not(:disabled) {
  background: #1557b0;
}

.pagination__ellipsis {
  padding: 0 0.25rem;
  color: #999;
}

/* Responsive */
@media (max-width: 768px) {
  .pagination {
    flex-direction: column;
    gap: 0.75rem;
  }

  .pagination__info,
  .pagination__size,
  .pagination__nav {
    width: 100%;
    justify-content: center;
  }
}
</style>
