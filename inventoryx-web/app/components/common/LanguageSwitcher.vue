<template>
  <div class="language-switcher">
    <button 
      v-for="locale in availableLocales" 
      :key="locale.code"
      @click="setLocale(locale.code)"
      :class="{ active: currentLocale === locale.code }"
      class="lang-btn"
    >
      {{ locale.name }}
    </button>
  </div>
</template>

<script setup lang="ts">
// @ts-ignore - Nuxt auto-imports
const { locale, locales, setLocale: switchLocale } = useI18n()

const currentLocale = computed(() => locale.value)
const availableLocales = computed(() => locales.value)

const setLocale = (code: string) => {
  switchLocale(code as 'en' | 'th')
}
</script>

<style scoped>
.language-switcher {
  display: flex;
  gap: 0.5rem;
}

.lang-btn {
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  background: var(--color-background);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  transition: all 0.2s;
}

.lang-btn:hover {
  border-color: var(--color-primary);
  background: var(--color-background-alt);
}

.lang-btn.active {
  background: var(--color-primary);
  color: white;
  border-color: var(--color-primary);
}
</style>
