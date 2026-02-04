<template>
  <div class="default-layout">
    <header class="navbar">
      <div class="container navbar-content">
        <div class="navbar-brand">
          <NuxtLink to="/" class="brand-link">
            <span class="brand-icon">📦</span>
            <span class="brand-text">InventoryX</span>
          </NuxtLink>
        </div>

        <nav class="navbar-menu" v-if="authStore.isAuthenticated">
          <NuxtLink 
            v-for="item in filteredNavigation"
            :key="item.to"
            :to="item.to" 
            class="nav-link"
          >
            {{ t(item.label) }}
          </NuxtLink>

          <div class="navbar-divider"></div>

          <CommonUserAvatarDropdown />
        </nav>

        <div v-else class="navbar-auth">
          <NuxtLink to="/login" class="btn btn-primary btn-sm">
            Sign In
          </NuxtLink>
        </div>
      </div>
    </header>

    <main class="main-content">
      <slot />
    </main>

    <footer class="footer">
      <div class="container">
        <small>&copy; {{ new Date().getFullYear() }} InventoryX System</small>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from "~/stores/auth";
import { navigationItems } from "~/config/navigation";

const { t } = useI18n();
const authStore = useAuthStore();

// Filter navigation items based on user roles
const filteredNavigation = computed(() => {
  if (!authStore.isAuthenticated) return [];
  
  return navigationItems.filter(item => {
    // If no roles defined, accessible by all authenticated users
    if (!item.roles || item.roles.length === 0) return true;
    
    // Check if user has at least one of the required roles
    return item.roles.some(role => authStore.hasRole(role));
  });
});
</script>

<style scoped>
.default-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Navbar with Glassmorphism */
.navbar {
  background: var(--glass-bg-strong);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border-bottom: 1px solid var(--glass-border);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
}

/* Brand with Hover Glow */
.brand-link {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  text-decoration: none;
  transition: all 0.3s ease;
  padding: 0.5rem;
  border-radius: var(--radius-md);
}

.brand-link:hover {
  background: var(--color-primary-light);
}

.brand-icon {
  font-size: 1.75rem;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.brand-text {
  font-size: 1.375rem;
  font-weight: 700;
  background: var(--gradient-primary-vivid);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.025em;
}

/* Navigation Menu */
.navbar-menu {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.nav-link {
  display: flex;
  align-items: center;
  padding: 0.5rem 1rem;
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: var(--radius-md);
  transition: all 0.2s ease;
  position: relative;
}

.nav-link::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: var(--gradient-primary-vivid);
  border-radius: var(--radius-full);
  transition: width 0.3s ease;
}

.nav-link:hover {
  color: var(--color-text-primary);
  background: var(--color-surface-hover);
}

.nav-link.router-link-active {
  color: var(--color-primary);
  background: var(--color-primary-light);
  font-weight: 600;
}

.nav-link.router-link-active::after {
  width: 60%;
}

/* Divider */
.navbar-divider {
  width: 1px;
  height: 24px;
  background: var(--color-border);
  margin: 0 0.75rem;
}

/* Auth Buttons */
.navbar-auth {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
  border: none;
}

.btn-primary {
  background: var(--gradient-primary-vivid);
  color: white;
  box-shadow: var(--shadow-glow-primary);
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md), var(--shadow-glow-primary);
}

.btn-sm {
  padding: 0.375rem 0.875rem;
  font-size: 0.8125rem;
}

/* Main Content */
.main-content {
  flex: 1;
  padding: var(--spacing-xl) 0;
}

.main-content > .container,
.main-content > :deep(.container) {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-lg);
}

/* Footer */
.footer {
  padding: var(--spacing-lg) 0;
  text-align: center;
  color: var(--color-text-muted);
  border-top: 1px solid var(--color-border);
  background: var(--color-surface);
}

/* Responsive */
@media (max-width: 768px) {
  .navbar-content {
    height: 56px;
  }

  .navbar-menu {
    gap: 0;
  }

  .nav-link {
    padding: 0.5rem 0.75rem;
    font-size: 0.8125rem;
  }

  .navbar-divider {
    margin: 0 0.5rem;
  }
}

@media (max-width: 640px) {
  .nav-link {
    padding: 0.375rem 0.5rem;
  }

  .navbar-divider {
    display: none;
  }

  .brand-text {
    font-size: 1.125rem;
  }
}
</style>
