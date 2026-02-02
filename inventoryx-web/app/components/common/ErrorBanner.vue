<script setup lang="ts">
/**
 * ErrorBanner - Displays persistent error/warning/info messages
 *
 * Features: Retry button, dismissible, multiple variants
 * Uses global alert and button styles for consistency.
 */

interface Props {
  title?: string;
  message: string;
  retryable?: boolean;
  dismissible?: boolean;
  type?: "error" | "warning" | "info";
}

const props = withDefaults(defineProps<Props>(), {
  title: "เกิดข้อผิดพลาด",
  retryable: true,
  dismissible: true,
  type: "error",
});

const emit = defineEmits<{
  retry: [];
  dismiss: [];
}>();

// Computed class for variant
const bannerClass = computed(() => ({
  'banner': true,
  'banner--error': props.type === 'error',
  'banner--warning': props.type === 'warning',
  'banner--info': props.type === 'info',
}));
</script>

<template>
  <div :class="bannerClass" role="alert" aria-live="polite">
    <div class="banner__icon">
      <!-- Error Icon -->
      <svg
        v-if="type === 'error'"
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <circle cx="12" cy="12" r="10" />
        <line x1="12" y1="8" x2="12" y2="12" />
        <line x1="12" y1="16" x2="12.01" y2="16" />
      </svg>
      <!-- Warning Icon -->
      <svg
        v-else-if="type === 'warning'"
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
        <line x1="12" y1="9" x2="12" y2="13" />
        <line x1="12" y1="17" x2="12.01" y2="17" />
      </svg>
      <!-- Info Icon -->
      <svg
        v-else
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <circle cx="12" cy="12" r="10" />
        <line x1="12" y1="16" x2="12" y2="12" />
        <line x1="12" y1="8" x2="12.01" y2="8" />
      </svg>
    </div>

    <div class="banner__content">
      <p v-if="title" class="banner__title">{{ title }}</p>
      <p class="banner__message">{{ message }}</p>
    </div>

    <div class="banner__actions">
      <button
        v-if="retryable"
        class="btn btn--secondary btn--small"
        @click="emit('retry')"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="23 4 23 10 17 10" />
          <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10" />
        </svg>
        ลองอีกครั้ง
      </button>

      <button
        v-if="dismissible"
        class="btn btn--ghost btn--icon"
        aria-label="ปิด"
        @click="emit('dismiss')"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="18" y1="6" x2="6" y2="18" />
          <line x1="6" y1="6" x2="18" y2="18" />
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
/* Banner base - extends global alert pattern */
.banner {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 1rem 1.25rem;
  border-radius: var(--radius-lg);
  margin-bottom: 1rem;
  animation: banner-slide-in 0.3s ease-out;
}

@keyframes banner-slide-in {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Variants */
.banner--error {
  background: var(--color-danger-light);
  border: 1px solid rgba(239, 68, 68, 0.25);
}

.banner--warning {
  background: var(--color-warning-light);
  border: 1px solid rgba(245, 158, 11, 0.25);
}

.banner--info {
  background: var(--color-info-light);
  border: 1px solid rgba(59, 130, 246, 0.25);
}

/* Icon */
.banner__icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: var(--radius-md);
  color: white;
}

.banner--error .banner__icon {
  background: var(--color-danger);
}

.banner--warning .banner__icon {
  background: var(--color-warning);
}

.banner--info .banner__icon {
  background: var(--color-info);
}

/* Content */
.banner__content {
  flex: 1;
  min-width: 0;
}

.banner__title {
  font-weight: 600;
  font-size: 0.875rem;
  margin: 0 0 0.25rem 0;
  color: var(--color-text-primary);
}

.banner__message {
  font-size: 0.8125rem;
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

/* Actions */
.banner__actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-shrink: 0;
}
</style>
