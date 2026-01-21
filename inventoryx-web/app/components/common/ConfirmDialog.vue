<script setup lang="ts">
/**
 * ConfirmDialog Component
 *
 * Reusable confirmation modal for destructive actions.
 * Shows a warning message and requires user confirmation before proceeding.
 */

const { t } = useI18n()

interface Props {
  show: boolean
  title: string
  message: string
  type?: 'warning' | 'danger' | 'info'
  confirmText?: string
  cancelText?: string
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'warning',
  loading: false
})

const emit = defineEmits<{
  confirm: []
  cancel: []
}>()

const iconSvg = computed(() => {
  const icons = {
    warning: `<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>`,
    danger: `<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>`,
    info: `<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>`
  }
  return icons[props.type]
})

const handleBackdropClick = (e: MouseEvent) => {
  if ((e.target as HTMLElement).classList.contains('confirm-backdrop')) {
    emit('cancel')
  }
}

const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Escape' && props.show) {
    emit('cancel')
  }
}

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
          <div class="confirm-dialog__icon" :class="`icon--${type}`" v-html="iconSvg"></div>

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
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 1rem;
}

/* Dialog */
.confirm-dialog {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  padding: 2rem;
  width: 100%;
  max-width: 400px;
  text-align: center;
  box-shadow: var(--shadow-xl);
}

.confirm-dialog--warning {
  border-top: 4px solid var(--color-warning);
}

.confirm-dialog--danger {
  border-top: 4px solid var(--color-danger);
}

.confirm-dialog--info {
  border-top: 4px solid var(--color-info);
}

/* Icon */
.confirm-dialog__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin: 0 auto 1rem;
  border-radius: 50%;
}

.icon--warning {
  background: var(--color-warning-light);
  color: var(--color-warning);
}

.icon--danger {
  background: var(--color-danger-light);
  color: var(--color-danger);
}

.icon--info {
  background: var(--color-info-light);
  color: var(--color-info);
}

/* Title */
.confirm-dialog__title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 0.75rem;
}

/* Message */
.confirm-dialog__message {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  margin: 0 0 1.5rem;
  line-height: 1.6;
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
  border-radius: var(--radius-md);
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn:focus-visible {
  outline: none;
  box-shadow: var(--focus-ring);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn--secondary {
  background: var(--color-surface);
  color: var(--color-text-primary);
  border-color: var(--color-border);
}

.btn--secondary:hover:not(:disabled) {
  background: var(--color-surface-hover);
}

.btn--warning {
  background: var(--color-warning);
  color: white;
}

.btn--warning:hover:not(:disabled) {
  background: #d97706;
}

.btn--danger {
  background: var(--color-danger);
  color: white;
}

.btn--danger:hover:not(:disabled) {
  background: #dc2626;
}

.btn--primary {
  background: var(--color-primary);
  color: white;
}

.btn--primary:hover:not(:disabled) {
  background: var(--color-primary-hover);
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

/* Responsive */
@media (max-width: 480px) {
  .confirm-dialog {
    padding: 1.5rem;
  }
  
  .confirm-dialog__actions {
    flex-direction: column-reverse;
  }
  
  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
