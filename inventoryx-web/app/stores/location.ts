import { defineStore } from "pinia";
import type { Location, CreateLocationCommand, UpdateLocationCommand } from "~/types/location";
import { useLocationApi } from "~/composables/api/useLocationApi";

export const useLocationStore = defineStore("location", () => {
  const locations = ref<Location[]>([]);
  const activeLocations = ref<Location[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  
  // Pagination state
  const pagination = ref({
    currentPage: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0,
    isFirst: true,
    isLast: true,
  });

  async function fetchLocations(page = 0, size = 10, search = "") {
    loading.value = true;
    error.value = null;
    try {
      const api = useLocationApi();
      const response = await api.getLocations({ 
        page, 
        size, 
        search,
        sort: "name,asc" 
      });
      
      locations.value = response.content;
      pagination.value = {
        currentPage: response.number,
        pageSize: response.size,
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        isFirst: response.first,
        isLast: response.last,
      };
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : "Failed to fetch locations";
    } finally {
      loading.value = false;
    }
  }

  async function fetchActiveLocations() {
    loading.value = true;
    try {
      const api = useLocationApi();
      activeLocations.value = await api.getActiveLocations();
    } catch (err: unknown) {
      console.error("Failed to fetch active locations", err);
    } finally {
      loading.value = false;
    }
  }

  async function createLocation(location: CreateLocationCommand) {
    loading.value = true;
    error.value = null;
    try {
      const api = useLocationApi();
      await api.createLocation(location);
      await fetchLocations(pagination.value.currentPage, pagination.value.pageSize);
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : "Failed to create location";
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function updateLocation(id: string, location: UpdateLocationCommand) {
    loading.value = true;
    error.value = null;
    try {
      const api = useLocationApi();
      await api.updateLocation(id, location);
      await fetchLocations(pagination.value.currentPage, pagination.value.pageSize);
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : "Failed to update location";
      throw err;
    } finally {
      loading.value = false;
    }
  }
  
  function changePage(page: number, search = "") {
    fetchLocations(page, pagination.value.pageSize, search);
  }

  function changePageSize(size: number, search = "") {
    fetchLocations(0, size, search);
  }

  return {
    locations,
    activeLocations,
    loading,
    error,
    pagination,
    fetchLocations,
    fetchActiveLocations,
    createLocation,
    updateLocation,
    changePage,
    changePageSize
  };
});
