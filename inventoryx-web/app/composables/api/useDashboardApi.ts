import type { DashboardData } from '~/types/dashboard'

/**
 * Dashboard API Composable
 * 
 * Centralizes all Dashboard-related API calls.
 */
export const useDashboardApi = () => {
  const { $api } = useNuxtApp()

  return {
    /**
     * Get dashboard summary data
     */
    async getDashboardData(): Promise<DashboardData> {
        const response = await $api.get<DashboardData>('/dashboard')
        return response.data
    }
  }
}
