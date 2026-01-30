<script setup lang="ts">
/**
 * Pagination Component
 *
 * Reusable pagination UI with page navigation and items per page selector.
 * Styles are defined in global main.css for consistency.
 */

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

<!-- No scoped styles - uses global .pagination classes from main.css -->
