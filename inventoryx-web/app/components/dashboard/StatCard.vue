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
    <div
      v-if="trend"
      class="stat-card__trend"
      :class="trendUp ? 'trend--up' : 'trend--down'"
    >
      <svg
        v-if="trendUp"
        xmlns="http://www.w3.org/2000/svg"
        width="14"
        height="14"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2.5"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <polyline points="18 15 12 9 6 15"></polyline>
      </svg>
      <svg
        v-else
        xmlns="http://www.w3.org/2000/svg"
        width="14"
        height="14"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2.5"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
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
  border-radius: var(--radius-xl);
  padding: 1.5rem;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

/* Shine animation overlay */
.stat-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: -150%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.15),
    transparent
  );
  transform: skewX(-25deg);
  transition: left 0.6s ease;
}

/* Colored accent line */
.stat-card::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-card:hover::before {
  left: 150%;
}

.stat-card:hover::after {
  opacity: 1;
}

/* Variant accent colors */
.stat-card--primary::after {
  background: var(--gradient-primary);
}
.stat-card--success::after {
  background: var(--gradient-success);
}
.stat-card--warning::after {
  background: var(--gradient-warning);
}
.stat-card--danger::after {
  background: var(--gradient-danger);
}
.stat-card--info::after {
  background: var(--gradient-info);
}

/* Variant hover shadows */
.stat-card--primary:hover {
  box-shadow: var(--shadow-lg), var(--shadow-glow-primary);
}
.stat-card--success:hover {
  box-shadow: var(--shadow-lg), var(--shadow-glow-success);
}
.stat-card--warning:hover {
  box-shadow: var(--shadow-lg), var(--shadow-glow-warning);
}
.stat-card--danger:hover {
  box-shadow: var(--shadow-lg), var(--shadow-glow-danger);
}
.stat-card--info:hover {
  box-shadow: var(--shadow-lg), var(--shadow-glow-info);
}

.stat-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.stat-card__title {
  color: var(--color-text-secondary);
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-card__icon {
  width: 3rem;
  height: 3rem;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.stat-card:hover .stat-card__icon {
  transform: scale(1.1);
}

/* Gradient Icon Backgrounds */
.stat-card--primary .stat-card__icon {
  background: var(--gradient-primary);
  color: white;
  box-shadow: var(--shadow-glow-primary);
}

.stat-card--success .stat-card__icon {
  background: var(--gradient-success);
  color: white;
  box-shadow: var(--shadow-glow-success);
}

.stat-card--warning .stat-card__icon {
  background: var(--gradient-warning);
  color: white;
  box-shadow: var(--shadow-glow-warning);
}

.stat-card--danger .stat-card__icon {
  background: var(--gradient-danger);
  color: white;
  box-shadow: var(--shadow-glow-danger);
}

.stat-card--info .stat-card__icon {
  background: var(--gradient-info);
  color: white;
  box-shadow: var(--shadow-glow-info);
}

.stat-card__value {
  font-size: 2.25rem;
  font-weight: 700;
  color: var(--color-text-primary);
  letter-spacing: -0.025em;
  line-height: 1.2;
}

.stat-card__trend {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  margin-top: 0.75rem;
  font-size: 0.8125rem;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
  border-radius: var(--radius-full);
}

.trend--up {
  color: var(--color-success);
  background: var(--color-success-light);
}

.trend--down {
  color: var(--color-danger);
  background: var(--color-danger-light);
}

/* Responsive */
@media (max-width: 640px) {
  .stat-card {
    padding: 1.25rem;
  }

  .stat-card__value {
    font-size: 1.75rem;
  }

  .stat-card__icon {
    width: 2.5rem;
    height: 2.5rem;
  }
}
</style>
