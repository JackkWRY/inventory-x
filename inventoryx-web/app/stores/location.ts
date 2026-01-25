import { defineStore } from "pinia";
import type { Location, CreateLocationCommand, UpdateLocationCommand } from "~/types/location";

export const useLocationStore = defineStore("location", () => {
  const { $api } = useNuxtApp();
  
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
      const response = await $api.get<{
        content: Location[];
        totalPages: number;
        totalElements: number;
        first: boolean;
        last: boolean;
        number: number;
        size: number;
      }>("/locations", {
        params: { page, size, sort: "name,asc", search },
      });
      
      locations.value = response.data.content;
      pagination.value = {
        currentPage: response.data.number,
        pageSize: response.data.size,
        totalElements: response.data.totalElements,
        totalPages: response.data.totalPages,
        isFirst: response.data.first,
        isLast: response.data.last,
      };
    } catch (err: any) {
      error.value = err.message || "Failed to fetch locations";
    } finally {
      loading.value = false;
    }
  }

  async function fetchActiveLocations() {
    loading.value = true;
    try {
      const response = await $api.get<Location[]>("/locations/active");
      activeLocations.value = response.data;
    } catch (err: any) {
      console.error("Failed to fetch active locations", err);
    } finally {
      loading.value = false;
    }
  }

  async function createLocation(location: CreateLocationCommand) {
    loading.value = true;
    error.value = null;
    try {
      await $api.post("/locations", location);
      await fetchLocations(pagination.value.currentPage, pagination.value.pageSize);
    } catch (err: any) {
      error.value = err.message || "Failed to create location";
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function updateLocation(id: string, location: UpdateLocationCommand) {
    loading.value = true;
    error.value = null;
    try {
      await $api.put(`/locations/${id}`, location);
      await fetchLocations(pagination.value.currentPage, pagination.value.pageSize);
    } catch (err: any) {
      error.value = err.message || "Failed to update location";
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
