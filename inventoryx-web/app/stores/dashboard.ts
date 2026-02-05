import { defineStore } from "pinia";
import type { DashboardData } from "~/types/dashboard";
import { useDashboardApi } from "~/composables/api/useDashboardApi";

export const useDashboardStore = defineStore("dashboard", () => {
  // const config = useRuntimeConfig(); // No longer needed
  // const authStore = useAuthStore(); // No longer needed

  const data = ref<DashboardData | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const fetchDashboardData = async () => {
    loading.value = true;
    error.value = null;
    try {
      const api = useDashboardApi();
      data.value = await api.getDashboardData();
    } catch (err: unknown) {
      console.error("Failed to fetch dashboard data:", err);
      // Safer error handling
      const message = (err as any).response?.data?.message || (err instanceof Error ? err.message : "Failed to load dashboard data");
      error.value = message;
    } finally {
      loading.value = false;
    }
  };

  return {
    data,
    loading,
    error,
    fetchDashboardData,
  };
});
