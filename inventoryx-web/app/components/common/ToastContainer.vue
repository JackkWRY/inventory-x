<script setup lang="ts">
import { useToastStore, type Toast, type ToastType } from '~/stores/toast'

/**
 * ToastContainer Component
 *
 * Displays toast notifications in a stack.
 * Should be placed once in the app layout.
 *
 * BEST PRACTICES:
 * - Fixed position at top-right
 * - Stacked display with animations
 * - Icon + type-based styling
 * - Keyboard accessible (Escape to dismiss)
 * - i18n support for close button
 *
 * @example
 * ```vue
 * <!-- In app.vue or layout -->
 * <CommonToastContainer />
 * ```
 */

// i18n
const { t } = useI18n()

// Store
const toastStore = useToastStore()

// Computed
const toasts = computed(() => toastStore.visibleToasts)

// Icon mapping
const icons: Record<ToastType, string> = {
  success: '✓',
  error: '✕',
  warning: '⚠',
  info: 'ℹ'
}

// Dismiss toast
const dismiss = (id: string) => {
  toastStore.removeToast(id)
}

// Keyboard handler
const handleKeydown = (e: KeyboardEvent, id: string) => {
  if (e.key === 'Escape' || e.key === 'Enter') {
    dismiss(id)
  }
}
</script>

<template>
  <Teleport to="body">
    <div class="toast-container" aria-live="polite" aria-label="Notifications">
      <TransitionGroup name="toast">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          :class="['toast', `toast--${toast.type}`]"
          role="alert"
          tabindex="0"
          @keydown="handleKeydown($event, toast.id)"
        >
          <!-- Icon -->
          <span class="toast__icon">{{ icons[toast.type] }}</span>

          <!-- Content -->
          <div class="toast__content">
            <strong v-if="toast.title" class="toast__title">
              {{ toast.title }}
            </strong>
            <p class="toast__message">{{ toast.message }}</p>
          </div>

          <!-- Close Button -->
          <button
            v-if="toast.dismissible"
            class="toast__close"
            :aria-label="t('common.close')"
            @click="dismiss(toast.id)"
          >
            ✕
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style scoped>
.toast-container {
  position: fixed;
  top: 1rem;
  right: 1rem;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  max-width: 400px;
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  border-radius: var(--radius-lg);
  background: var(--glass-bg-strong);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  box-shadow: var(--shadow-lg);
  pointer-events: auto;
  animation: slideIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: 1px solid var(--glass-border);
}

.toast:focus {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

/* Type-specific styling with gradient borders */
.toast--success {
  border-left: 4px solid var(--color-success);
}

.toast--success .toast__icon {
  color: white;
  background: var(--gradient-success);
  box-shadow: var(--shadow-glow-success);
}

.toast--error {
  border-left: 4px solid var(--color-danger);
}

.toast--error .toast__icon {
  color: white;
  background: var(--gradient-danger);
  box-shadow: var(--shadow-glow-danger);
}

.toast--warning {
  border-left: 4px solid var(--color-warning);
}

.toast--warning .toast__icon {
  color: white;
  background: var(--gradient-warning);
  box-shadow: var(--shadow-glow-warning);
}

.toast--info {
  border-left: 4px solid var(--color-info);
}

.toast--info .toast__icon {
  color: white;
  background: var(--gradient-info);
  box-shadow: var(--shadow-glow-info);
}

.toast__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  font-weight: 600;
  flex-shrink: 0;
}

.toast__content {
  flex: 1;
  min-width: 0;
}

.toast__title {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 0.25rem;
}

.toast__message {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  margin: 0;
  word-wrap: break-word;
}

.toast__close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 1.5rem;
  height: 1.5rem;
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  font-size: 0.75rem;
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.toast__close:hover {
  background: var(--color-surface-hover);
  color: var(--color-text-primary);
}

/* Transition animations */
.toast-enter-active {
  animation: slideIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.toast-leave-active {
  animation: slideOut 0.2s ease-in;
}

@keyframes slideIn {
  from {
    transform: translateX(120%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideOut {
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(120%);
    opacity: 0;
  }
}

/* Responsive */
@media (max-width: 480px) {
  .toast-container {
    left: 1rem;
    right: 1rem;
    max-width: none;
  }
}
</style>
