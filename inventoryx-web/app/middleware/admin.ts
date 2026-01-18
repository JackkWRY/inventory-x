import { useAuthStore } from '~/stores/auth';

export default defineNuxtRouteMiddleware((to, from) => {
    const authStore = useAuthStore();

    if (!authStore.isAuthenticated) {
        return navigateTo('/login');
    }

    // Role check - using cookie based persistence
    if (!authStore.hasRole('ADMIN')) {
        // Redirect to home if not admin
        return navigateTo('/');
    }
});
