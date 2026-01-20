import { defineStore } from "pinia";
import type { DashboardData } from "~/types/dashboard";
import { useAuthStore } from "./auth";

export const useDashboardStore = defineStore("dashboard", () => {
  const config = useRuntimeConfig();
  const authStore = useAuthStore();

  const data = ref<DashboardData | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const fetchDashboardData = async () => {
    loading.value = true;
    error.value = null;
    try {
      const response = await $fetch<DashboardData>("/dashboard", {
        baseURL: config.public.apiBaseUrl,
        headers: {
          Authorization: `Bearer ${authStore.token}`,
        },
      });
      data.value = response;
    } catch (err: any) {
      console.error("Failed to fetch dashboard data:", err);
      error.value = err.message || "Failed to load dashboard data";
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
