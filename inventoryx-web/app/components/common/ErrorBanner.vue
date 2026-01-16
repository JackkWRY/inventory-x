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
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  animation: slideIn 0.3s ease-out;
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
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #dc2626;
}

/* Warning variant */
.error-banner--warning {
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.3);
  color: #d97706;
}

/* Info variant */
.error-banner--info {
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.3);
  color: #2563eb;
}

.error-banner__icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.error-banner__content {
  flex: 1;
  min-width: 0;
}

.error-banner__title {
  font-weight: 600;
  font-size: 0.875rem;
  margin: 0 0 4px 0;
}

.error-banner__message {
  font-size: 0.875rem;
  margin: 0;
  opacity: 0.9;
}

.error-banner__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.error-banner__btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  font-size: 0.8125rem;
  font-weight: 500;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.error-banner__btn--retry {
  background: rgba(255, 255, 255, 0.8);
  color: inherit;
}

.error-banner__btn--retry:hover {
  background: rgba(255, 255, 255, 1);
}

.error-banner__btn--dismiss {
  background: transparent;
  padding: 6px;
  color: inherit;
  opacity: 0.6;
}

.error-banner__btn--dismiss:hover {
  opacity: 1;
}

/* Dark mode */
@media (prefers-color-scheme: dark) {
  .error-banner--error {
    background: rgba(239, 68, 68, 0.15);
    color: #fca5a5;
  }

  .error-banner--warning {
    background: rgba(245, 158, 11, 0.15);
    color: #fcd34d;
  }

  .error-banner--info {
    background: rgba(59, 130, 246, 0.15);
    color: #93c5fd;
  }

  .error-banner__btn--retry {
    background: rgba(255, 255, 255, 0.15);
  }

  .error-banner__btn--retry:hover {
    background: rgba(255, 255, 255, 0.25);
  }
}
</style>
