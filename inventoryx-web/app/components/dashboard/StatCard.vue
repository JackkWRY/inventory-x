<script setup lang="ts">
defineProps<{
  title: string;
  value: string | number;
  icon?: string;
  variant?: "primary" | "success" | "warning" | "danger" | "info";
  trend?: string;
  trendUp?: boolean;
}>();
</script>

<template>
  <div class="stat-card" :class="`stat-card--${variant || 'primary'}`">
    <div class="stat-card__header">
      <span class="stat-card__title">{{ title }}</span>
      <div class="stat-card__icon">
        <slot name="icon" />
      </div>
    </div>
    <div class="stat-card__value">
      {{ value }}
    </div>
    <div v-if="trend" class="stat-card__trend" :class="trendUp ? 'trend--up' : 'trend--down'">
      <svg v-if="trendUp" xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <polyline points="18 15 12 9 6 15"></polyline>
      </svg>
      <svg v-else xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <polyline points="6 9 12 15 18 9"></polyline>
      </svg>
      {{ trend }}
    </div>
  </div>
</template>

<style scoped>
.stat-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 1.5rem;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  opacity: 0;
  transition: opacity var(--transition-base);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-card:hover::before {
  opacity: 1;
}

/* Variant Colors */
.stat-card--primary::before { background: var(--color-primary); }
.stat-card--success::before { background: var(--color-success); }
.stat-card--warning::before { background: var(--color-warning); }
.stat-card--danger::before { background: var(--color-danger); }
.stat-card--info::before { background: var(--color-info); }

.stat-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.75rem;
}

.stat-card__title {
  color: var(--color-text-secondary);
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-card__icon {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* Icon Variants */
.stat-card--primary .stat-card__icon {
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.stat-card--success .stat-card__icon {
  background: var(--color-success-light);
  color: var(--color-success);
}

.stat-card--warning .stat-card__icon {
  background: var(--color-warning-light);
  color: var(--color-warning);
}

.stat-card--danger .stat-card__icon {
  background: var(--color-danger-light);
  color: var(--color-danger);
}

.stat-card--info .stat-card__icon {
  background: var(--color-info-light);
  color: var(--color-info);
}

.stat-card__value {
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-text-primary);
  letter-spacing: -0.025em;
  line-height: 1.2;
}

.stat-card__trend {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  margin-top: 0.5rem;
  font-size: 0.8125rem;
  font-weight: 500;
}

.trend--up {
  color: var(--color-success);
}

.trend--down {
  color: var(--color-danger);
}

/* Responsive */
@media (max-width: 640px) {
  .stat-card {
    padding: 1.25rem;
  }
  
  .stat-card__value {
    font-size: 1.5rem;
  }
}
</style>
