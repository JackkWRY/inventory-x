<script setup lang="ts">
/**
 * StatCard Component
 * 
 * Dashboard stat card with variants, trends, and hover animations.
 * Uses global stat-card styles from main.css.
 */
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

<!-- No scoped styles - uses global stat-card classes from main.css -->
