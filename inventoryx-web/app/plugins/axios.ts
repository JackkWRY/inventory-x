import axios from 'axios';
import { useAuthStore } from '~/stores/auth';

export default defineNuxtPlugin((nuxtApp) => {
    const config = useRuntimeConfig();
    const authStore = useAuthStore(); // Use the Pinia store

    const api = axios.create({
        baseURL: config.public.apiBaseUrl,
        headers: {
            'Content-Type': 'application/json'
        }
    });

    api.interceptors.request.use((config) => {
        if (authStore.token) {
            config.headers.Authorization = `Bearer ${authStore.token}`;
        }
        return config;
    });

    api.interceptors.response.use(
        (response) => response,
        async (error) => {
            const originalRequest = error.config;
            
            // Check if error is 401 and we haven't tried refreshing yet
            if (error.response?.status === 401 && !originalRequest._retry) {
                originalRequest._retry = true;

                try {
                    const refreshed = await authStore.refresh();
                    if (refreshed) {
                        // Update authorization header with new token
                        originalRequest.headers.Authorization = `Bearer ${authStore.token}`;
                        // Retry original request
                        return api(originalRequest);
                    }
                } catch (refreshError) {
                    // Logic already handled in store.refresh (logout)
                    return Promise.reject(refreshError);
                }
            }
            return Promise.reject(error);
        }
    );

    return {
        provide: {
            api: api
        }
    };
});
