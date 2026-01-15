<script setup lang="ts">
/**
 * ThemeToggle Component
 *
 * Toggle button for switching between light and dark themes.
 * Uses sun/moon icons to indicate current theme.
 */

const { isDark, toggleTheme } = useTheme()
const { t } = useI18n()
</script>

<template>
  <button
    class="theme-toggle"
    :title="isDark ? t('theme.switchLight') : t('theme.switchDark')"
    @click="toggleTheme"
  >
    <span v-if="isDark" class="theme-toggle__icon">🌙</span>
    <span v-else class="theme-toggle__icon">☀️</span>
  </button>
</template>

<style scoped>
.theme-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.5rem;
  height: 2.5rem;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.theme-toggle:hover {
  background: var(--color-surface-hover);
  transform: scale(1.05);
}

.theme-toggle__icon {
  font-size: 1.25rem;
}
</style>
