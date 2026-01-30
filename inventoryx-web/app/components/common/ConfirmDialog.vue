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
/* Backdrop with Enhanced Blur */
.confirm-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 1rem;
}

/* Dialog with Glassmorphism */
.confirm-dialog {
  background: var(--glass-bg-strong);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  padding: 2rem;
  width: 100%;
  max-width: 400px;
  text-align: center;
  box-shadow: var(--shadow-xl);
  position: relative;
  overflow: hidden;
}

/* Gradient border accent */
.confirm-dialog::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  opacity: 1;
}

.confirm-dialog--warning::before {
  background: var(--gradient-warning);
}

.confirm-dialog--danger::before {
  background: var(--gradient-danger);
}

.confirm-dialog--info::before {
  background: var(--gradient-info);
}

/* Icon with gradient background */
.confirm-dialog__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 4rem;
  height: 4rem;
  margin: 0 auto 1.25rem;
  border-radius: var(--radius-lg);
  color: white;
}

.icon--warning {
  background: var(--gradient-warning);
  box-shadow: var(--shadow-glow-warning);
}

.icon--danger {
  background: var(--gradient-danger);
  box-shadow: var(--shadow-glow-danger);
}

.icon--info {
  background: var(--gradient-info);
  box-shadow: var(--shadow-glow-info);
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
  font-size: 0.9375rem;
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
  padding: 0.75rem 1.5rem;
  font-size: 0.875rem;
  font-weight: 600;
  border-radius: var(--radius-md);
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  position: relative;
  overflow: hidden;
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
  transform: translateY(-1px);
}

.btn--warning {
  background: var(--gradient-warning);
  color: white;
  box-shadow: var(--shadow-glow-warning);
}

.btn--warning:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md), var(--shadow-glow-warning);
}

.btn--danger {
  background: var(--gradient-danger);
  color: white;
  box-shadow: var(--shadow-glow-danger);
}

.btn--danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md), var(--shadow-glow-danger);
}

.btn--primary {
  background: var(--gradient-primary-vivid);
  color: white;
  box-shadow: var(--shadow-glow-primary);
}

.btn--primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md), var(--shadow-glow-primary);
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

.confirm-fade-enter-active .confirm-dialog {
  animation: dialog-in 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.confirm-fade-leave-active .confirm-dialog {
  animation: dialog-out 0.2s ease-in;
}

@keyframes dialog-in {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes dialog-out {
  from {
    opacity: 1;
    transform: scale(1);
  }
  to {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
  }
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
