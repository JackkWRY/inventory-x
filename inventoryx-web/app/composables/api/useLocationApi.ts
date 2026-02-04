import type { Location, CreateLocationCommand, UpdateLocationCommand } from '~/types/location'

/**
 * Location API Composable
 * 
 * Centralizes all Location-related API calls.
 */
export const useLocationApi = () => {
  const { $api } = useNuxtApp()

  return {
    /**
     * Get locations with pagination and search
     */
    async getLocations(params: { page?: number; size?: number; search?: string; sort?: string }) {
        const queryParams: any = {
            page: params.page ?? 0,
            size: params.size ?? 10,
            sort: params.sort ?? 'name,asc'
        }
        if (params.search) queryParams.search = params.search

        const response = await $api.get<{
            content: Location[];
            totalPages: number;
            totalElements: number;
            first: boolean;
            last: boolean;
            number: number;
            size: number;
        }>('/locations', { params: queryParams })
        return response.data
    },

    /**
     * Get all active locations
     */
    async getActiveLocations(): Promise<Location[]> {
        const response = await $api.get<Location[]>('/locations/active')
        return response.data
    },

    /**
     * Create a new location
     */
    async createLocation(command: CreateLocationCommand): Promise<void> {
        await $api.post('/locations', command)
    },

    /**
     * Update an existing location
     */
    async updateLocation(id: string, command: UpdateLocationCommand): Promise<void> {
        await $api.put(`/locations/${id}`, command)
    }
  }
}
