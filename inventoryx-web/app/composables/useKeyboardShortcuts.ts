/**
 * Keyboard Shortcuts Composable
 *
 * Provides global keyboard shortcuts for improved UX.
 * Shortcuts are registered on mount and cleaned up on unmount.
 *
 * @example
 * // In a page component
 * useKeyboardShortcuts({
 *   onSearch: () => searchInput.value?.focus()
 * })
 */

interface ShortcutOptions {
  /** Callback when "/" is pressed to focus search */
  onSearch?: () => void
  /** Callback when "n" is pressed for new item */
  onNew?: () => void
  /** Whether shortcuts are enabled */
  enabled?: boolean
}

export function useKeyboardShortcuts(options: ShortcutOptions = {}) {
  const { onSearch, onNew, enabled = true } = options

  /**
   * Check if user is typing in an input field
   */
  function isTyping(): boolean {
    const activeElement = document.activeElement
    if (!activeElement) return false
    
    const tagName = activeElement.tagName.toLowerCase()
    const isInput = tagName === 'input' || tagName === 'textarea' || tagName === 'select'
    const isEditable = activeElement.hasAttribute('contenteditable')
    
    return isInput || isEditable
  }

  /**
   * Handle keydown events
   */
  function handleKeydown(event: KeyboardEvent) {
    if (!enabled) return
    
    // Skip if user is typing in an input
    if (isTyping()) return
    
    // "/" - Focus search
    if (event.key === '/' && onSearch) {
      event.preventDefault()
      onSearch()
    }
    
    // "n" - New item (with Ctrl/Cmd)
    if (event.key === 'n' && (event.ctrlKey || event.metaKey) && onNew) {
      event.preventDefault()
      onNew()
    }
  }

  // Register keyboard listener
  onMounted(() => {
    if (import.meta.client) {
      window.addEventListener('keydown', handleKeydown)
    }
  })

  // Clean up on unmount
  onUnmounted(() => {
    if (import.meta.client) {
      window.removeEventListener('keydown', handleKeydown)
    }
  })

  return {
    /** Manually trigger search focus */
    triggerSearch: onSearch,
    /** Manually trigger new action */
    triggerNew: onNew
  }
}
