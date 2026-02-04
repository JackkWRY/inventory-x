import axios from "axios";
import type { LoginCommand, AuthResponse } from "~/types/auth";

/**
 * Auth API Composable
 *
 * Handles all authentication-related API calls.
 * Uses a separate Axios instance/configuration to prevent circular dependencies
 * with the main interceptor (which depends on the store, which uses this api).
 */
export const useAuthApi = () => {
  const config = useRuntimeConfig();

  // Create a dedicated instance for Auth to avoid interceptor loops
  // especially for refresh token logical flow.
  const authApi = axios.create({
    baseURL: config.public.apiBaseUrl,
    headers: {
      "Content-Type": "application/json",
    },
  });

  return {
    /**
     * Authenticate user with credentials
     */
    async login(command: LoginCommand): Promise<AuthResponse> {
      const response = await authApi.post<AuthResponse>("/auth/login", command);
      return response.data;
    },

    /**
     * Refresh the access token using a refresh token
     */
    async refresh(refreshToken: string): Promise<AuthResponse> {
      const response = await authApi.post<AuthResponse>("/auth/refresh", {
        refreshToken,
      });
      return response.data;
    },

    /**
     * Logout (optional server-side call)
     */
    async logout() {
      // If the server has a logout endpoint, call it here.
      // For now, client-side cleanup is handled in the store.
      return Promise.resolve();
    },
  };
};
