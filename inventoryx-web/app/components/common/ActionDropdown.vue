<script setup lang="ts">
/**
 * ActionDropdown Component
 *
 * Reusable dropdown menu for consolidating multiple action buttons.
 * Uses teleport to prevent z-index issues.
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

// Toggle dropdown
const toggle = () => {
  isOpen.value = !isOpen.value;
};

// Close dropdown
const close = () => {
  isOpen.value = false;
};

// Close on click outside
const handleClickOutside = (event: MouseEvent) => {
  if (triggerRef.value && !triggerRef.value.contains(event.target as Node)) {
    close();
  }
};

// Handle escape key
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === "Escape") {
    close();
  }
};

// Setup event listeners
onMounted(() => {
  document.addEventListener("click", handleClickOutside);
  document.addEventListener("keydown", handleKeydown);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
  document.removeEventListener("keydown", handleKeydown);
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

    <!-- Dropdown Menu -->
    <Transition name="dropdown">
      <div v-if="isOpen" class="action-dropdown__menu">
        <slot></slot>
      </div>
    </Transition>
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

.action-dropdown__menu {
  position: absolute;
  top: 100%;
  right: 0;
  z-index: 100;
  min-width: 180px;
  margin-top: 4px;
  padding: 6px;
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  box-shadow: 0 8px 24px -4px rgba(0, 0, 0, 0.15);
}

/* Animation */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.15s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}
</style>
