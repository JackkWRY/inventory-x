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
  gap: 0.5rem;
  max-width: 400px;
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 1rem;
  border-radius: 8px;
  background: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  pointer-events: auto;
  animation: slideIn 0.3s ease-out;
}

.toast:focus {
  outline: 2px solid #1a73e8;
  outline-offset: 2px;
}

/* Type-specific styling */
.toast--success {
  border-left: 4px solid #10b981;
}

.toast--success .toast__icon {
  color: #10b981;
  background: #d1fae5;
}

.toast--error {
  border-left: 4px solid #ef4444;
}

.toast--error .toast__icon {
  color: #ef4444;
  background: #fee2e2;
}

.toast--warning {
  border-left: 4px solid #f59e0b;
}

.toast--warning .toast__icon {
  color: #f59e0b;
  background: #fef3c7;
}

.toast--info {
  border-left: 4px solid #3b82f6;
}

.toast--info .toast__icon {
  color: #3b82f6;
  background: #dbeafe;
}

.toast__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 50%;
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
  color: #111827;
  margin-bottom: 0.25rem;
}

.toast__message {
  font-size: 0.875rem;
  color: #4b5563;
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
  color: #9ca3af;
  font-size: 0.75rem;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
  flex-shrink: 0;
}

.toast__close:hover {
  background: #f3f4f6;
  color: #374151;
}

/* Transition animations */
.toast-enter-active {
  animation: slideIn 0.3s ease-out;
}

.toast-leave-active {
  animation: slideOut 0.2s ease-in;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
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
    transform: translateX(100%);
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
