<script setup lang="ts">
/**
 * ConfirmDialog Component
 *
 * Reusable confirmation modal for destructive actions.
 * Shows a warning message and requires user confirmation before proceeding.
 *
 * @example
 * <ConfirmDialog
 *   :show="showConfirm"
 *   :title="t('confirm.adjustStock')"
 *   :message="t('confirm.adjustWarning')"
 *   type="warning"
 *   @confirm="handleConfirm"
 *   @cancel="showConfirm = false"
 * />
 */

// i18n
const { t } = useI18n()

// Props
interface Props {
  /** Whether to show the dialog */
  show: boolean
  /** Dialog title */
  title: string
  /** Confirmation message */
  message: string
  /** Dialog type: 'warning' | 'danger' | 'info' */
  type?: 'warning' | 'danger' | 'info'
  /** Confirm button text (optional, defaults to common.confirm) */
  confirmText?: string
  /** Cancel button text (optional, defaults to common.cancel) */
  cancelText?: string
  /** Whether confirm action is in progress */
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'warning',
  loading: false
})

// Emits
const emit = defineEmits<{
  /** Triggered when user confirms */
  confirm: []
  /** Triggered when user cancels */
  cancel: []
}>()

// Computed icon based on type
const icon = computed(() => {
  const icons = {
    warning: '⚠️',
    danger: '🚨',
    info: 'ℹ️'
  }
  return icons[props.type]
})

// Handle backdrop click (close on outside click)
const handleBackdropClick = (e: MouseEvent) => {
  if ((e.target as HTMLElement).classList.contains('confirm-backdrop')) {
    emit('cancel')
  }
}

// Handle keyboard escape
const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Escape' && props.show) {
    emit('cancel')
  }
}

// Add/remove keyboard listener
onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <Teleport to="body">
    <Transition name="confirm-fade">
      <div
        v-if="show"
        class="confirm-backdrop"
        @click="handleBackdropClick"
      >
        <div class="confirm-dialog" :class="`confirm-dialog--${type}`">
          <!-- Icon -->
          <div class="confirm-dialog__icon">{{ icon }}</div>

          <!-- Title -->
          <h3 class="confirm-dialog__title">{{ title }}</h3>

          <!-- Message -->
          <p class="confirm-dialog__message">{{ message }}</p>

          <!-- Actions -->
          <div class="confirm-dialog__actions">
            <button
              class="btn btn--secondary"
              :disabled="loading"
              @click="emit('cancel')"
            >
              {{ cancelText || t('common.cancel') }}
            </button>
            <button
              class="btn"
              :class="{
                'btn--warning': type === 'warning',
                'btn--danger': type === 'danger',
                'btn--primary': type === 'info'
              }"
              :disabled="loading"
              @click="emit('confirm')"
            >
              <span v-if="loading" class="spinner"></span>
              {{ confirmText || t('common.confirm') }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
/* Backdrop */
.confirm-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 1rem;
}

/* Dialog */
.confirm-dialog {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  width: 100%;
  max-width: 400px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.confirm-dialog--warning {
  border-top: 4px solid #f59e0b;
}

.confirm-dialog--danger {
  border-top: 4px solid #ef4444;
}

.confirm-dialog--info {
  border-top: 4px solid #3b82f6;
}

/* Icon */
.confirm-dialog__icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

/* Title */
.confirm-dialog__title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 0.75rem;
}

/* Message */
.confirm-dialog__message {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0 0 1.5rem;
  line-height: 1.5;
}

/* Actions */
.confirm-dialog__actions {
  display: flex;
  gap: 0.75rem;
  justify-content: center;
}

/* Buttons */
.btn {
  padding: 0.625rem 1.25rem;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn--secondary {
  background: #f3f4f6;
  color: #374151;
}

.btn--secondary:hover:not(:disabled) {
  background: #e5e7eb;
}

.btn--warning {
  background: #f59e0b;
  color: white;
}

.btn--warning:hover:not(:disabled) {
  background: #d97706;
}

.btn--danger {
  background: #ef4444;
  color: white;
}

.btn--danger:hover:not(:disabled) {
  background: #dc2626;
}

.btn--primary {
  background: #3b82f6;
  color: white;
}

.btn--primary:hover:not(:disabled) {
  background: #2563eb;
}

/* Spinner */
.spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Transition */
.confirm-fade-enter-active,
.confirm-fade-leave-active {
  transition: opacity 0.2s ease;
}

.confirm-fade-enter-from,
.confirm-fade-leave-to {
  opacity: 0;
}

.confirm-fade-enter-active .confirm-dialog,
.confirm-fade-leave-active .confirm-dialog {
  transition: transform 0.2s ease;
}

.confirm-fade-enter-from .confirm-dialog,
.confirm-fade-leave-to .confirm-dialog {
  transform: scale(0.95);
}
</style>
