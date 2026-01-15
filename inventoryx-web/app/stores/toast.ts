import { defineStore } from 'pinia'

/**
 * Toast Types
 *
 * BEST PRACTICE: Use semantic types for different notification purposes
 */
export type ToastType = 'success' | 'error' | 'warning' | 'info'

/**
 * Toast Message Interface
 */
export interface Toast {
  /** Unique identifier */
  id: string
  /** Toast type determines styling and icon */
  type: ToastType
  /** Main message to display */
  message: string
  /** Optional title/heading */
  title?: string
  /** Auto-dismiss duration in ms (0 = no auto-dismiss) */
  duration?: number
  /** Whether the toast can be dismissed by user */
  dismissible?: boolean
}

/**
 * Toast Store
 *
 * Centralized state management for toast notifications.
 * Follows Best Practices:
 * - Single source of truth for all toasts
 * - Auto-dismiss with configurable duration
 * - Type-safe toast creation methods
 * - Queue management for multiple toasts
 *
 * @example
 * ```ts
 * const toast = useToastStore()
 * toast.success('Stock received successfully!')
 * toast.error('Failed to reserve stock')
 * ```
 */
export const useToastStore = defineStore('toast', {
  state: () => ({
    /** Active toasts queue */
    toasts: [] as Toast[],
    /** Default duration in ms */
    defaultDuration: 4000,
    /** Maximum toasts shown at once */
    maxToasts: 5
  }),

  getters: {
    /** Get visible toasts (limited by maxToasts) */
    visibleToasts: (state): Toast[] => {
      return state.toasts.slice(0, state.maxToasts)
    },

    /** Check if there are any toasts */
    hasToasts: (state): boolean => state.toasts.length > 0
  },

  actions: {
    /**
     * Generate unique ID for toast
     */
    generateId(): string {
      return `toast-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
    },

    /**
     * Add a new toast notification
     */
    addToast(toast: Omit<Toast, 'id'>): string {
      const id = this.generateId()
      const duration = toast.duration ?? this.defaultDuration
      const dismissible = toast.dismissible ?? true

      const newToast: Toast = {
        id,
        ...toast,
        duration,
        dismissible
      }

      // Add to queue
      this.toasts.push(newToast)

      // Auto-dismiss after duration (if duration > 0)
      if (duration > 0) {
        setTimeout(() => {
          this.removeToast(id)
        }, duration)
      }

      return id
    },

    /**
     * Remove a toast by ID
     */
    removeToast(id: string): void {
      const index = this.toasts.findIndex(t => t.id === id)
      if (index !== -1) {
        this.toasts.splice(index, 1)
      }
    },

    /**
     * Clear all toasts
     */
    clearAll(): void {
      this.toasts = []
    },

    // ============================================
    // Convenience Methods (BEST PRACTICE)
    // ============================================

    /**
     * Show success toast
     */
    success(message: string, title?: string): string {
      return this.addToast({ type: 'success', message, title })
    },

    /**
     * Show error toast
     */
    error(message: string, title?: string): string {
      return this.addToast({
        type: 'error',
        message,
        title,
        duration: 6000 // Errors stay longer
      })
    },

    /**
     * Show warning toast
     */
    warning(message: string, title?: string): string {
      return this.addToast({ type: 'warning', message, title })
    },

    /**
     * Show info toast
     */
    info(message: string, title?: string): string {
      return this.addToast({ type: 'info', message, title })
    }
  }
})
