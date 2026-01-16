<script setup lang="ts">
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

// Close on click outside
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as Node;
  if (triggerRef.value && !triggerRef.value.contains(target)) {
    // Check if click is inside menu (teleported)
    const menu = document.querySelector('.action-dropdown__menu--teleported');
    if (menu && menu.contains(target)) return;
    close();
  }
};

// Handle escape key
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === "Escape") {
    close();
  }
};

// Handle scroll/resize - close dropdown
const handleScrollResize = () => {
  if (isOpen.value) {
    close();
  }
};

// Setup event listeners
onMounted(() => {
  document.addEventListener("click", handleClickOutside);
  document.addEventListener("keydown", handleKeydown);
  window.addEventListener("scroll", handleScrollResize, true);
  window.addEventListener("resize", handleScrollResize);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
  document.removeEventListener("keydown", handleKeydown);
  window.removeEventListener("scroll", handleScrollResize, true);
  window.removeEventListener("resize", handleScrollResize);
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
  width: 36px;
  height: 36px;
  padding: 0;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 8px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-dropdown__trigger:hover {
  background: var(--color-surface-hover);
  color: var(--color-text-primary);
}

.action-dropdown__trigger--active {
  background: var(--color-surface-hover);
  color: var(--color-primary);
}
</style>

<style>
/* Global styles for teleported menu */
.action-dropdown__menu--teleported {
  position: absolute;
  z-index: 9999;
  min-width: 180px;
  padding: 6px;
  background: var(--color-card, #1e1e3f);
  border: 1px solid var(--color-border, rgba(255, 255, 255, 0.1));
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

/* Animation */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.15s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
</style>


