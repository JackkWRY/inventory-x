<script setup lang="ts">
/**
 * ActionDropdownItem Component
 *
 * Individual menu item for ActionDropdown.
 * Supports variants: default, danger, disabled
 *
 * @example
 * <ActionDropdownItem icon="edit" @click="handleEdit">
 *   Edit Item
 * </ActionDropdownItem>
 */

interface Props {
  /** Makes the item appear as destructive action */
  danger?: boolean;
  /** Disables the item */
  disabled?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  danger: false,
  disabled: false,
});

// Get close function from parent
const closeDropdown = inject<() => void>("closeDropdown", () => {});

// Emit
const emit = defineEmits<{
  click: [];
}>();

// Handle click
const handleClick = () => {
  if (!props.disabled) {
    emit("click");
    closeDropdown();
  }
};
</script>

<template>
  <button
    class="dropdown-item"
    :class="{
      'dropdown-item--danger': danger,
      'dropdown-item--disabled': disabled,
    }"
    :disabled="disabled"
    @click="handleClick"
  >
    <slot name="icon"></slot>
    <span class="dropdown-item__label">
      <slot></slot>
    </span>
  </button>
</template>

<style scoped>
.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  background: transparent;
  border: none;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-primary);
  cursor: pointer;
  text-align: left;
  transition: all 0.15s ease;
}

.dropdown-item:hover {
  background: var(--color-surface-hover);
}

.dropdown-item--danger {
  color: var(--color-danger);
}

.dropdown-item--danger:hover {
  background: rgba(239, 68, 68, 0.1);
}

.dropdown-item--disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.dropdown-item--disabled:hover {
  background: transparent;
}

.dropdown-item__label {
  flex: 1;
}

/* Icon styling */
.dropdown-item :deep(svg) {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}
</style>
