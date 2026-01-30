<script setup lang="ts">
/**
 * ErrorBanner - Displays persistent error messages with retry functionality.
 *
 * USE CASE: API failures, network errors, server unavailable
 *
 * PROPS:
 * - message: Error message to display
 * - title: Optional error title
 * - retryable: Show retry button
 * - dismissible: Allow user to dismiss
 *
 * EVENTS:
 * - @retry: Emitted when user clicks retry
 * - @dismiss: Emitted when user dismisses
 *
 * @example
 * ```vue
 * <ErrorBanner
 *   v-if="error"
 *   :message="error"
 *   retryable
 *   @retry="fetchData"
 *   @dismiss="clearError"
 * />
 * ```
 */

interface Props {
  title?: string;
  message: string;
  retryable?: boolean;
  dismissible?: boolean;
  type?: "error" | "warning" | "info";
}

withDefaults(defineProps<Props>(), {
  title: "เกิดข้อผิดพลาด",
  retryable: true,
  dismissible: true,
  type: "error",
});

const emit = defineEmits<{
  retry: [];
  dismiss: [];
}>();
</script>

<template>
  <div
    class="error-banner"
    :class="`error-banner--${type}`"
    role="alert"
    aria-live="polite"
  >
    <div class="error-banner__icon">
      <!-- Error Icon -->
      <svg
        v-if="type === 'error'"
        xmlns="http://www.w3.org/2000/svg"
        width="24"
        height="24"
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
        width="24"
        height="24"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path
          d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"
        />
        <line x1="12" y1="9" x2="12" y2="13" />
        <line x1="12" y1="17" x2="12.01" y2="17" />
      </svg>
      <!-- Info Icon -->
      <svg
        v-else
        xmlns="http://www.w3.org/2000/svg"
        width="24"
        height="24"
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

    <div class="error-banner__content">
      <p v-if="title" class="error-banner__title">{{ title }}</p>
      <p class="error-banner__message">{{ message }}</p>
    </div>

    <div class="error-banner__actions">
      <button
        v-if="retryable"
        class="error-banner__btn error-banner__btn--retry"
        @click="emit('retry')"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <polyline points="23 4 23 10 17 10" />
          <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10" />
        </svg>
        ลองอีกครั้ง
      </button>

      <button
        v-if="dismissible"
        class="error-banner__btn error-banner__btn--dismiss"
        aria-label="ปิด"
        @click="emit('dismiss')"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <line x1="18" y1="6" x2="6" y2="18" />
          <line x1="6" y1="6" x2="18" y2="18" />
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.error-banner {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 1.25rem;
  border-radius: var(--radius-lg);
  margin-bottom: 1rem;
  animation: slideIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Error variant */
.error-banner--error {
  background: var(--color-danger-light);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: var(--color-danger);
}

/* Warning variant */
.error-banner--warning {
  background: var(--color-warning-light);
  border: 1px solid rgba(245, 158, 11, 0.3);
  color: var(--color-warning);
}

/* Info variant */
.error-banner--info {
  background: var(--color-info-light);
  border: 1px solid rgba(59, 130, 246, 0.3);
  color: var(--color-info);
}

.error-banner__icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.5rem;
  height: 2.5rem;
  border-radius: var(--radius-md);
}

.error-banner--error .error-banner__icon {
  background: var(--gradient-danger);
  color: white;
  box-shadow: var(--shadow-glow-danger);
}

.error-banner--warning .error-banner__icon {
  background: var(--gradient-warning);
  color: white;
  box-shadow: var(--shadow-glow-warning);
}

.error-banner--info .error-banner__icon {
  background: var(--gradient-info);
  color: white;
  box-shadow: var(--shadow-glow-info);
}

.error-banner__content {
  flex: 1;
  min-width: 0;
}

.error-banner__title {
  font-weight: 600;
  font-size: 0.9375rem;
  margin: 0 0 0.25rem 0;
  color: var(--color-text-primary);
}

.error-banner__message {
  font-size: 0.875rem;
  margin: 0;
  color: var(--color-text-secondary);
}

.error-banner__actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-shrink: 0;
}

.error-banner__btn {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1rem;
  font-size: 0.8125rem;
  font-weight: 500;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
}

.error-banner__btn--retry {
  background: var(--glass-bg-strong);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid var(--glass-border);
  color: var(--color-text-primary);
}

.error-banner__btn--retry:hover {
  background: var(--color-surface);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.error-banner__btn--dismiss {
  background: transparent;
  padding: 0.375rem;
  color: var(--color-text-muted);
}

.error-banner__btn--dismiss:hover {
  color: var(--color-text-primary);
}
</style>
