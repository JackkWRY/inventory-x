<script setup lang="ts">
/**
 * BaseModal Component
 *
 * A reusable modal wrapper that handles:
 * - Teleport to body
 * - Backdrop with blur
 * - Escape key to close
 * - Click outside to close
 * - Accessible dialog role
 *
 * @example
 * <BaseModal :open="isOpen" @close="handleClose" title="My Dialog">
 *   <template #body>
 *     Modal content here
 *   </template>
 *   <template #footer>
 *     <button @click="handleClose">Close</button>
 *   </template>
 * </BaseModal>
 */

interface Props {
  /** Whether the modal is open */
  open: boolean
  /** Dialog title */
  title?: string
  /** Size variant: sm (380px), md (480px), lg (600px) */
  size?: 'sm' | 'md' | 'lg'
  /** Show close button in header */
  showClose?: boolean
  /** Allow closing by clicking backdrop */
  closeOnBackdrop?: boolean
  /** Allow closing by pressing Escape */
  closeOnEscape?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  size: 'md',
  showClose: true,
  closeOnBackdrop: true,
  closeOnEscape: true
})

const emit = defineEmits<{
  (e: 'close'): void
}>()

// Handle backdrop click
function handleBackdropClick(event: MouseEvent) {
  if (props.closeOnBackdrop && (event.target as HTMLElement).classList.contains('dialog-backdrop')) {
    emit('close')
  }
}

// Handle Escape key
function handleKeydown(event: KeyboardEvent) {
  if (props.closeOnEscape && event.key === 'Escape' && props.open) {
    emit('close')
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})

// Size class mapping
const sizeClass = computed(() => `dialog--${props.size}`)
</script>

<template>
  <Teleport to="body">
    <Transition name="dialog-fade">
      <div v-if="open" class="dialog-backdrop" @click="handleBackdropClick">
        <div
          class="dialog"
          :class="sizeClass"
          role="dialog"
          aria-modal="true"
          :aria-labelledby="title ? 'modal-title' : undefined"
        >
          <!-- Header (optional) -->
          <div v-if="title || showClose || $slots.header" class="dialog__header">
            <slot name="header">
              <h2 v-if="title" id="modal-title" class="dialog__title">{{ title }}</h2>
            </slot>
            <button
              v-if="showClose"
              type="button"
              class="dialog__close"
              aria-label="Close"
              @click="emit('close')"
            >
              ✕
            </button>
          </div>

          <!-- Error slot -->
          <slot name="error" />

          <!-- Info slot (stock info, etc) -->
          <slot name="info" />

          <!-- Body -->
          <div v-if="$slots.body || $slots.default" class="dialog__body">
            <slot name="body">
              <slot />
            </slot>
          </div>

          <!-- Footer (optional) -->
          <div v-if="$slots.footer" class="dialog__footer">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
/* All styles come from global main.css */
/* Only transition animations are scoped */

.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: opacity 0.2s ease;
}

.dialog-fade-enter-from,
.dialog-fade-leave-to {
  opacity: 0;
}

.dialog-fade-enter-active .dialog,
.dialog-fade-leave-active .dialog {
  transition: transform 0.2s ease;
}

.dialog-fade-enter-from .dialog,
.dialog-fade-leave-to .dialog {
  transform: scale(0.95);
}
</style>
