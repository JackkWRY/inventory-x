<template>
  <button 
    class="lang-toggle"
    :title="currentLocaleName"
    @click="toggleLocale"
  >
    {{ currentLocaleCode }}
  </button>
</template>

<script setup lang="ts">
// @ts-ignore - Nuxt auto-imports
const { locale, locales, setLocale: switchLocale } = useI18n()

const currentLocale = computed(() => locale.value)
const currentLocaleCode = computed(() => locale.value.toUpperCase())
const currentLocaleName = computed(() => {
  const loc = locales.value.find((l: { code: string }) => l.code === locale.value)
  return loc?.name || locale.value
})

const toggleLocale = () => {
  // Toggle between 'en' and 'th'
  const newLocale = locale.value === 'en' ? 'th' : 'en'
  switchLocale(newLocale as 'en' | 'th')
}
</script>

<style scoped>
.lang-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 2.5rem;
  height: 2.5rem;
  padding: 0 0.5rem;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--color-text-primary);
  transition: all 0.2s;
}

.lang-toggle:hover {
  background: var(--color-surface-hover);
  transform: scale(1.05);
}
</style>
