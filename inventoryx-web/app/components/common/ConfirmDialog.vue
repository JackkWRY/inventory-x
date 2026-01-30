<script setup lang="ts">
/**
 * ConfirmDialog Component - Reusable confirmation modal for destructive actions.
 */

import BaseModal from "./BaseModal.vue";

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
</script>

<template>
  <BaseModal
    :open="show"
    size="sm"
    :close-on-escape="!loading"
    :close-on-backdrop="!loading"
    @close="emit('cancel')"
  >
    <!-- Custom centered content (no header) -->
    <template #body>
      <div class="confirm-content" :class="`confirm-content--${type}`">
        <!-- Gradient border accent -->
        <div class="confirm-accent" :class="`accent--${type}`"></div>
        
        <!-- Icon -->
        <div class="confirm-icon" :class="`icon--${type}`" v-html="iconSvg"></div>

        <!-- Title -->
        <h3 class="confirm-title">{{ title }}</h3>

        <!-- Message -->
        <p class="confirm-message">{{ message }}</p>
      </div>
    </template>

    <!-- Footer Actions -->
    <template #footer>
      <div class="confirm-actions">
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
    </template>
  </BaseModal>
</template>

<style scoped>
/* Confirm dialog specific layout */
.confirm-content {
  text-align: center;
  padding: 0.5rem 0;
  position: relative;
}

/* Gradient border accent */
.confirm-accent {
  position: absolute;
  top: -1.5rem;
  left: -1.5rem;
  right: -1.5rem;
  height: 4px;
}

.accent--warning {
  background: var(--gradient-warning);
}

.accent--danger {
  background: var(--gradient-danger);
}

.accent--info {
  background: var(--gradient-info);
}

/* Icon with gradient background */
.confirm-icon {
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
.confirm-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 0.75rem;
}

/* Message */
.confirm-message {
  font-size: 0.9375rem;
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.6;
}

/* Actions - center aligned */
.confirm-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: center;
  width: 100%;
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

/* Responsive */
@media (max-width: 480px) {
  .confirm-actions {
    flex-direction: column-reverse;
  }
  
  .confirm-actions .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>

