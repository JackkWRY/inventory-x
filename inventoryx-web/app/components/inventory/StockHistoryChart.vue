<script setup lang="ts">
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js'
import type { StockMovement } from '~/types/inventory'

/**
 * StockHistoryChart Component
 *
 * Displays a line chart showing stock level changes over time
 * based on movement history data.
 */

// Register Chart.js components
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
)

// i18n
const { t } = useI18n()

// Props
interface Props {
  /** Movement history data */
  movements: StockMovement[]
  /** Current stock quantity */
  currentQuantity: number
  /** Time filter in days (7, 30, or 0 for all) */
  filterDays?: number
}

const props = withDefaults(defineProps<Props>(), {
  filterDays: 30,
})

// Local filter state
const selectedFilter = ref(props.filterDays)

// Filter options
const filterOptions = [
  { value: 7, label: '7 days' },
  { value: 30, label: '30 days' },
  { value: 0, label: 'All time' },
]

// Computed: Filter movements by time
const filteredMovements = computed(() => {
  if (selectedFilter.value === 0) {
    return [...props.movements].sort(
      (a, b) => new Date(a.performedAt).getTime() - new Date(b.performedAt).getTime()
    )
  }

  const cutoff = new Date()
  cutoff.setDate(cutoff.getDate() - selectedFilter.value)

  return props.movements
    .filter((m) => new Date(m.performedAt) >= cutoff)
    .sort((a, b) => new Date(a.performedAt).getTime() - new Date(b.performedAt).getTime())
})

// Use useTheme composable or simple robust color mapping
const chartColors = {
  primary: 'rgb(14, 165, 233)', // Sky 500
  primaryBg: 'rgba(14, 165, 233, 0.1)',
  grid: 'rgba(255, 255, 255, 0.1)',
  text: '#9ca3af'
}

// Computed: Build chart data from movements
const chartData = computed(() => {
  const movements = filteredMovements.value

  if (movements.length === 0) {
    return {
      labels: [t('chart.now')],
      datasets: [
        {
          label: t('chart.stockLevel'),
          data: [props.currentQuantity],
          borderColor: chartColors.primary,
          backgroundColor: chartColors.primaryBg,
          fill: true,
          tension: 0.3,
          pointRadius: 4,
          pointHoverRadius: 6,
        },
      ],
    }
  }

  // Calculate stock levels going backwards from current
  const dataPoints: { date: string; quantity: number }[] = []
  let runningQuantity = props.currentQuantity

  // Add current point
  dataPoints.unshift({
    date: t('chart.now'),
    quantity: runningQuantity,
  })

  // Work backwards through movements
  for (let i = movements.length - 1; i >= 0; i--) {
    const movement = movements[i]
    if (!movement) continue

    const qty = parseFloat(movement.quantity)

    // Reverse the movement effect to get previous state
    switch (movement.movementType) {
      case 'RECEIPT':
      case 'RELEASE':
        runningQuantity -= qty
        break
      case 'RESERVATION':
      case 'ADJUSTMENT':
      case 'CONFIRMATION':
        // These reduce available, so add back
        runningQuantity += qty
        break
    }

    const date = new Date(movement.performedAt)
    const label = date.toLocaleDateString(undefined, {
      month: 'short',
      day: 'numeric',
    })

    dataPoints.unshift({
      date: label,
      quantity: Math.max(0, runningQuantity),
    })
  }

  return {
    labels: dataPoints.map((d) => d.date),
    datasets: [
      {
        label: t('chart.stockLevel'),
        data: dataPoints.map((d) => d.quantity),
        borderColor: chartColors.primary,
        backgroundColor: chartColors.primaryBg,
        fill: true,
        tension: 0.3,
        pointRadius: 4,
        pointHoverRadius: 6,
      },
    ],
  }
})

// Chart options
const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false,
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      titleColor: '#fff',
      bodyColor: '#fff',
      padding: 12,
      displayColors: false,
    },
  },
  scales: {
    x: {
      grid: {
        color: chartColors.grid,
      },
      ticks: {
        color: chartColors.text,
      },
    },
    y: {
      beginAtZero: true,
      grid: {
        color: chartColors.grid,
      },
      ticks: {
        color: chartColors.text,
      },
    },
  },
}))

// Has enough data for chart
const hasData = computed(() => props.movements.length > 0 || props.currentQuantity > 0)
</script>

<template>
  <div class="stock-chart">
    <div class="stock-chart__header">
      <h3 class="stock-chart__title">{{ t('chart.title') }}</h3>
      <div class="stock-chart__filters">
        <button
          v-for="option in filterOptions"
          :key="option.value"
          class="filter-btn"
          :class="{ 'filter-btn--active': selectedFilter === option.value }"
          @click="selectedFilter = option.value"
        >
          {{ option.label }}
        </button>
      </div>
    </div>

    <div v-if="hasData" class="stock-chart__container">
      <Line :data="chartData" :options="chartOptions" />
    </div>

    <div v-else class="stock-chart__empty">
      <p>{{ t('chart.noData') }}</p>
    </div>
  </div>
</template>

<style scoped>
.stock-chart {
  background: var(--color-card);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
}

.stock-chart__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.stock-chart__title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.stock-chart__filters {
  display: flex;
  gap: 0.5rem;
}

.filter-btn {
  padding: 0.4rem 0.75rem;
  font-size: 0.75rem;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover {
  background: var(--color-surface-hover);
  color: var(--color-text-primary);
}

.filter-btn--active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
}

.stock-chart__container {
  height: 250px;
  position: relative;
}

.stock-chart__empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 150px;
  color: var(--color-text-secondary);
}
</style>
