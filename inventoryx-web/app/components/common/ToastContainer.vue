<script setup lang="ts">
/**
 * ToastContainer Component
 * 
 * Displays toast notifications in a stack (top-right).
 * Uses global toast styles from main.css.
 */
import { useToastStore, type ToastType } from '~/stores/toast'

const { t } = useI18n()
const toastStore = useToastStore()

const toasts = computed(() => toastStore.visibleToasts)

const icons: Record<ToastType, string> = {
  success: '✓',
  error: '✕',
  warning: '⚠',
  info: 'ℹ'
}

const dismiss = (id: string) => {
  toastStore.removeToast(id)
}

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

<!-- No scoped styles - uses global toast classes from main.css -->
