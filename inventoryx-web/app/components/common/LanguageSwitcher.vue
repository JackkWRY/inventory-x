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
  border: 1px solid #e0e0e0;
  background: #f8f9fa;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  color: #666;
  transition: all 0.2s;
}

.lang-btn:hover {
  border-color: #1a73e8;
  background: #e8f0fe;
}

.lang-btn.active {
  background: #1a73e8;
  color: white;
  border-color: #1a73e8;
}
</style>
