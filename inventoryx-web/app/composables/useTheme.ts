/**
 * Theme Composable
 *
 * Manages application theme (light/dark mode) with localStorage persistence.
 * Uses CSS class on document.documentElement to apply theme globally.
 *
 * @example
 * const { theme, isDark, toggleTheme } = useTheme()
 */

type Theme = 'light' | 'dark'

const STORAGE_KEY = 'inventoryx_theme'

// Global reactive state (shared across all components)
const theme = ref<Theme>('light')
const isInitialized = ref(false)

/**
 * Initialize theme from localStorage or system preference
 */
function initTheme() {
  if (isInitialized.value) return
  
  if (import.meta.client) {
    // Try localStorage first
    const saved = localStorage.getItem(STORAGE_KEY) as Theme | null
    
    if (saved && (saved === 'light' || saved === 'dark')) {
      theme.value = saved
    } else {
      // Fallback to system preference
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      theme.value = prefersDark ? 'dark' : 'light'
    }
    
    applyTheme(theme.value)
    isInitialized.value = true
  }
}

/**
 * Apply theme to document
 */
function applyTheme(newTheme: Theme) {
  if (import.meta.client) {
    document.documentElement.classList.remove('light', 'dark')
    document.documentElement.classList.add(newTheme)
    document.documentElement.setAttribute('data-theme', newTheme)
  }
}

/**
 * Toggle between light and dark themes
 */
function toggleTheme() {
  const newTheme: Theme = theme.value === 'light' ? 'dark' : 'light'
  setTheme(newTheme)
}

/**
 * Set specific theme
 */
function setTheme(newTheme: Theme) {
  theme.value = newTheme
  applyTheme(newTheme)
  
  if (import.meta.client) {
    localStorage.setItem(STORAGE_KEY, newTheme)
  }
}

/**
 * Use theme composable
 */
export function useTheme() {
  // Initialize on first use
  onMounted(() => {
    initTheme()
  })
  
  const isDark = computed(() => theme.value === 'dark')
  const isLight = computed(() => theme.value === 'light')
  
  return {
    /** Current theme ('light' | 'dark') */
    theme: readonly(theme),
    /** Whether dark mode is active */
    isDark,
    /** Whether light mode is active */
    isLight,
    /** Toggle between light and dark */
    toggleTheme,
    /** Set specific theme */
    setTheme
  }
}
