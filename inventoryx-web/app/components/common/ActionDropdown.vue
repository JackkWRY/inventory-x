<script setup lang="ts">
import { onClickOutside, useWindowScroll, useEventListener } from '@vueuse/core';

/**
 * ActionDropdown Component
 *
 * Reusable dropdown menu for consolidating multiple action buttons.
 * Uses Teleport to render at body level, preventing overflow cutoff.
 *
 * BEST PRACTICE: Single responsibility - handles dropdown UI only
 *
 * @example
 * <ActionDropdown>
 *   <ActionDropdownItem @click="handleEdit">Edit</ActionDropdownItem>
 *   <ActionDropdownItem danger @click="handleDelete">Delete</ActionDropdownItem>
 * </ActionDropdown>
 */

// State
const isOpen = ref(false);
const triggerRef = ref<HTMLElement | null>(null);
const menuRef = ref<HTMLElement | null>(null); // New ref for the menu
const menuStyle = ref({ top: '0px', left: '0px' });

// Toggle dropdown
const toggle = () => {
  if (!isOpen.value) {
    updatePosition();
  }
  isOpen.value = !isOpen.value;
};

// Update menu position based on trigger location
const updatePosition = () => {
  if (!triggerRef.value) return;
  
  const rect = triggerRef.value.getBoundingClientRect();
  const viewportHeight = window.innerHeight;
  const menuHeight = 180; // Approximate menu height
  const menuWidth = 180;
  
  // Check if should show above
  const spaceBelow = viewportHeight - rect.bottom;
  const showAbove = spaceBelow < menuHeight;
  
  // Calculate position
  const top = showAbove 
    ? rect.top - menuHeight - 4 + window.scrollY
    : rect.bottom + 4 + window.scrollY;
  const left = rect.right - menuWidth + window.scrollX;
  
  menuStyle.value = {
    top: `${top}px`,
    left: `${Math.max(8, left)}px`
  };
};

// Close dropdown
const close = () => {
  isOpen.value = false;
};

// Handle simple close
onClickOutside(menuRef, (event) => {
  // If the click is on the trigger, let the toggle handler take care of it
  if (triggerRef.value && triggerRef.value.contains(event.target as Node)) {
      return;
  }
  close();
});

// Handle escape key
useEventListener(document, 'keydown', (e: KeyboardEvent) => {
    if (e.key === 'Escape') close();
});

// Handle scroll/resize - close dropdown
const { x, y } = useWindowScroll();
watch([x, y], () => {
    if(isOpen.value) close();
});

useEventListener(window, 'resize', () => {
    if(isOpen.value) close();
});

// Expose close for child items
provide("closeDropdown", close);
</script>

<template>
  <div ref="triggerRef" class="action-dropdown">
    <!-- Trigger Button -->
    <button
      class="action-dropdown__trigger"
      :class="{ 'action-dropdown__trigger--active': isOpen }"
      @click.stop="toggle"
      :aria-expanded="isOpen"
      aria-haspopup="true"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="currentColor"
        width="20"
        height="20"
      >
        <circle cx="12" cy="5" r="2"></circle>
        <circle cx="12" cy="12" r="2"></circle>
        <circle cx="12" cy="19" r="2"></circle>
      </svg>
    </button>

    <!-- Dropdown Menu - Teleported to body for proper positioning -->
    <Teleport to="body">
      <Transition name="dropdown">
        <div
          v-if="isOpen"
          ref="menuRef"
          class="action-dropdown__menu action-dropdown__menu--teleported"
          :style="menuStyle"
          @click.stop
        >
          <slot></slot>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<style scoped>
.action-dropdown {
  position: relative;
  display: inline-block;
}

.action-dropdown__trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  padding: 0;
  background: transparent;
  border: 1px solid transparent;
  border-radius: var(--radius-md);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-dropdown__trigger:hover {
  background: var(--color-surface-hover);
  color: var(--color-text-primary);
  transform: scale(1.05);
}

.action-dropdown__trigger--active {
  background: var(--color-primary-light);
  color: var(--color-primary);
}
</style>

<style>
/* Global styles for teleported menu */
.action-dropdown__menu--teleported {
  position: absolute;
  z-index: 9999;
  min-width: 180px;
  padding: 0.375rem;
  background: var(--glass-bg-strong, rgba(255, 255, 255, 0.9));
  backdrop-filter: var(--glass-blur, blur(12px));
  -webkit-backdrop-filter: var(--glass-blur, blur(12px));
  border: 1px solid var(--glass-border, rgba(255, 255, 255, 0.3));
  border-radius: var(--radius-lg, 12px);
  box-shadow: var(--shadow-lg, 0 8px 32px rgba(0, 0, 0, 0.15));
}

/* Animation */
.dropdown-enter-active {
  animation: dropdown-in 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.dropdown-leave-active {
  animation: dropdown-out 0.15s ease-in;
}

@keyframes dropdown-in {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes dropdown-out {
  from {
    opacity: 1;
    transform: scale(1);
  }
  to {
    opacity: 0;
    transform: scale(0.95);
  }
}
</style>


