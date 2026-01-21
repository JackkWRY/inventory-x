import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import type { LoginCommand, AuthResponse } from "../types/auth";
import axios from "axios";

export const useAuthStore = defineStore("auth", () => {
  const user = ref<AuthResponse | null>(null);
  const token = useCookie<string | null>("auth_token", {
    default: () => null,
    maxAge: 60 * 60 * 24,
  }); // 1 day
  const refreshToken = useCookie<string | null>("refresh_token", {
    default: () => null,
    maxAge: 60 * 60 * 24 * 7,
  }); // 7 days
  const roleCookie = useCookie<string[] | null>("auth_roles", {
    default: () => null,
    maxAge: 60 * 60 * 24 * 7,
  }); // 7 days
  const isAuthenticated = computed(() => !!token.value);
  const router = useRouter();

  // Axios instance (ideally this should be imported from a plugin, but for circular deps avoidance, we might set it up there.
  // However, store actions often call API. We can use global axios or a configured instance.
  // For now, I will assume a base URL from runtime config.
  const config = useRuntimeConfig();
  const apiBase = config.public.apiBaseUrl;

  const api = axios.create({
    baseURL: apiBase,
    headers: {
      "Content-Type": "application/json",
    },
  });

  // Interceptor to add token - moved to plugin usually, but doing it here for the store's internal instance
  api.interceptors.request.use((config) => {
    if (token.value) {
      config.headers.Authorization = `Bearer ${token.value}`;
    }
    return config;
  });

  async function login(payload: LoginCommand) {
    try {
      const response = await api.post<AuthResponse>("/auth/login", payload);
      setSession(response.data);
      return { success: true };
    } catch (error: any) {
      const msg = error.response?.data?.message || "Login failed";
      return { success: false, error: msg };
    }
  }

  function setSession(authData: AuthResponse) {
    user.value = authData;
    token.value = authData.accessToken;
    refreshToken.value = authData.refreshToken;
    roleCookie.value = authData.roles;
  }

  function logout() {
    user.value = null;
    token.value = null;
    refreshToken.value = null;
    roleCookie.value = null;
    router.push("/login");
  }

  async function refresh() {
    try {
      if (!refreshToken.value) {
        throw new Error("No refresh token available");
      }
      const response = await api.post<AuthResponse>("/auth/refresh", {
        refreshToken: refreshToken.value,
      });
      setSession(response.data);
      return true;
    } catch (error) {
      logout();
      return false;
    }
  }

  return {
    user,
    token,
    isAuthenticated,
    login,
    logout,
    refresh,
    hasRole: (role: string) => roleCookie.value?.includes(role) || false,
  };
});
