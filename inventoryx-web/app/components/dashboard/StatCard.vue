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
  <div class="stat-card">
    <div class="stat-card__content">
      <div class="stat-card__header">
        <span class="stat-card__title">{{ title }}</span>
        <div v-if="icon" class="stat-card__icon" :class="`bg-${variant}`">
          <slot name="icon" />
        </div>
      </div>
      <div class="stat-card__value">
        {{ value }}
      </div>
      <div v-if="trend" class="stat-card__trend" :class="trendUp ? 'text-success' : 'text-danger'">
        <span v-if="trendUp">▲</span><span v-else>▼</span> {{ trend }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.stat-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.stat-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}

.stat-card__title {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-card__icon {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.bg-primary { background-color: #3b82f6; }
.bg-success { background-color: #10b981; }
.bg-warning { background-color: #f59e0b; }
.bg-danger { background-color: #ef4444; }
.bg-info { background-color: #6366f1; }

.stat-card__value {
  font-size: 1.875rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 0.25rem;
}

.stat-card__trend {
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.text-success { color: #10b981; }
.text-danger { color: #ef4444; }
</style>
