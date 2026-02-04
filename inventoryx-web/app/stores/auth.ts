import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import type { LoginCommand, AuthResponse } from "~/types/auth";
import { useAuthApi } from "~/composables/api/useAuthApi";

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
  const firstNameCookie = useCookie<string | null>("auth_firstName", {
    default: () => null,
    maxAge: 60 * 60 * 24 * 7,
  }); // 7 days
  const lastNameCookie = useCookie<string | null>("auth_lastName", {
    default: () => null,
    maxAge: 60 * 60 * 24 * 7,
  }); // 7 days
  const isAuthenticated = computed(() => !!token.value);
  
  // Computed user data that falls back to cookies
  const userData = computed(() => {
    if (user.value) {
      return {
        firstName: user.value.firstName,
        lastName: user.value.lastName,
        roles: user.value.roles,
      };
    }
    return {
      firstName: firstNameCookie.value || '',
      lastName: lastNameCookie.value || '',
      roles: roleCookie.value || [],
    };
  });
  const router = useRouter();

  async function login(payload: LoginCommand) {
    try {
      const api = useAuthApi();
      const response = await api.login(payload);
      setSession(response);
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
    firstNameCookie.value = authData.firstName;
    lastNameCookie.value = authData.lastName;
  }

  function logout() {
    user.value = null;
    token.value = null;
    refreshToken.value = null;
    roleCookie.value = null;
    firstNameCookie.value = null;
    lastNameCookie.value = null;
    router.push("/login");
  }

  async function refresh() {
    try {
      if (!refreshToken.value) {
        throw new Error("No refresh token available");
      }
      const api = useAuthApi();
      const response = await api.refresh(refreshToken.value);
      setSession(response);
      return true;
    } catch (error) {
      logout();
      return false;
    }
  }

  return {
    user,
    userData,
    token,
    isAuthenticated,
    login,
    logout,
    refresh,
    hasRole: (role: string) => roleCookie.value?.includes(role) || false,
  };
});
