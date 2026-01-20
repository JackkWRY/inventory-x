<template>
  <div class="default-layout">
    <header class="navbar">
      <div class="container navbar-content">
        <div class="navbar-brand">
          <NuxtLink to="/" style="text-decoration: none; color: inherit;">
            <h3 style="margin: 0; color: var(--color-primary);">InventoryX</h3>
          </NuxtLink>
        </div>
        
        <div class="navbar-menu" v-if="authStore.isAuthenticated">
          <NuxtLink to="/dashboard" class="nav-link">{{ t('navigation.dashboard') }}</NuxtLink>
          <NuxtLink to="/products" class="nav-link">{{ t('navigation.products') }}</NuxtLink>
          <NuxtLink to="/inventory" class="nav-link">{{ t('navigation.inventory') }}</NuxtLink>
          <NuxtLink to="/locations" class="nav-link">{{ t('navigation.warehouses') }}</NuxtLink>
          <NuxtLink to="/users" class="nav-link" v-if="authStore.hasRole('ADMIN')">{{ t('navigation.users') }}</NuxtLink>
          
          <span class="user-info mr-md">
            {{ authStore.user?.firstName }} {{ authStore.user?.lastName }}
            <span class="badge" v-if="authStore.user?.roles?.length">
              {{ authStore.user.roles[0] }}
            </span>
          </span>
          <button @click="handleLogout" class="btn btn-secondary btn-sm">
            Logout
          </button>
        </div>
        <div v-else>
           <NuxtLink to="/login" class="btn btn-primary btn-sm">Sign In</NuxtLink>
        </div>
      </div>
    </header>

    <main class="container mt-xl">
      <slot />
    </main>
    
    <footer class="text-center mt-xl mb-lg text-secondary">
      <small>&copy; {{ new Date().getFullYear() }} InventoryX System</small>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth';

const { t } = useI18n();
const authStore = useAuthStore();
const router = useRouter();

function handleLogout() {
  authStore.logout();
}
</script>

<style scoped>
.navbar {
  background: var(--color-background);
  border-bottom: 1px solid var(--color-border);
  padding: 1rem 0;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navbar-menu {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-info {
  font-weight: 500;
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.badge {
  font-size: 0.75rem;
  background: var(--color-background-alt);
  border: 1px solid var(--color-border);
  padding: 0.125rem 0.375rem;
  border-radius: var(--radius-sm);
  color: var(--color-text-secondary);
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

.mr-md {
  margin-right: var(--spacing-md);
}

.nav-link {
  color: var(--color-text-secondary);
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem;
  border-radius: 4px;
  transition: all 0.2s;
}

.nav-link:hover {
  color: var(--color-primary);
  background: var(--color-surface-hover);
}

.router-link-active {
  color: var(--color-primary);
  background: var(--color-surface-selected); /* Need to define or use hover */
  font-weight: 600;
}
</style>
