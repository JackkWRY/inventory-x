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
          <NuxtLink to="/dashboard" class="nav-link">
            {{ t('navigation.dashboard') }}
          </NuxtLink>
          <NuxtLink to="/products" class="nav-link">
            {{ t('navigation.products') }}
          </NuxtLink>
          <NuxtLink to="/inventory" class="nav-link">
            {{ t('navigation.inventory') }}
          </NuxtLink>
          <NuxtLink to="/locations" class="nav-link">
            {{ t('navigation.warehouses') }}
          </NuxtLink>
          <NuxtLink 
            to="/users" 
            class="nav-link" 
            v-if="authStore.hasRole('ADMIN')"
          >
            {{ t('navigation.users') }}
          </NuxtLink>
          
          <div class="navbar-divider"></div>
          
          <div class="user-section">
            <span class="user-info">
              <span class="user-name">
                {{ authStore.user?.firstName }} {{ authStore.user?.lastName }}
              </span>
              <span class="user-badge" v-if="authStore.user?.roles?.length">
                {{ authStore.user.roles[0] }}
              </span>
            </span>
            
            <div class="navbar-divider"></div>
            
            <button @click="handleLogout" class="btn-logout">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                <polyline points="16 17 21 12 16 7"></polyline>
                <line x1="21" y1="12" x2="9" y2="12"></line>
              </svg>
              <span>{{ t('common.logout') }}</span>
            </button>
            
            <div class="navbar-divider"></div>
            
            <div class="navbar-toggles">
              <CommonThemeToggle />
              <CommonLanguageSwitcher />
            </div>
          </div>
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
import { useAuthStore } from '~/stores/auth';

const { t } = useI18n();
const authStore = useAuthStore();

function handleLogout() {
  authStore.logout();
}
</script>

<style scoped>
.default-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Navbar */
.navbar {
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(8px);
  background: rgba(var(--color-surface), 0.95);
}

.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
}

/* Brand */
.brand-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
  transition: opacity var(--transition-fast);
}

.brand-link:hover {
  opacity: 0.8;
}

.brand-icon {
  font-size: 1.5rem;
}

.brand-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-primary);
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
  padding: 0.5rem 0.875rem;
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
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

/* Divider */
.navbar-divider {
  width: 1px;
  height: 24px;
  background: var(--color-border);
  margin: 0 0.75rem;
}

/* User Section */
.user-section {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* Navbar Toggles */
.navbar-toggles {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.user-name {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-primary);
}

.user-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.125rem 0.5rem;
  font-size: 0.625rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  background: var(--color-primary-light);
  color: var(--color-primary);
  border-radius: var(--radius-full);
}

/* Logout Button */
.btn-logout {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 0.875rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-secondary);
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-logout:hover {
  color: var(--color-danger);
  border-color: var(--color-danger);
  background: var(--color-danger-light);
}

.btn-logout svg {
  flex-shrink: 0;
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
  transition: all var(--transition-fast);
  text-decoration: none;
  border: none;
}

.btn-primary {
  background: var(--color-primary);
  color: white;
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-sm {
  padding: 0.375rem 0.75rem;
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
    padding: 0.5rem;
    font-size: 0.8125rem;
  }
  
  .navbar-divider {
    margin: 0 0.5rem;
  }
  
  .user-name {
    display: none;
  }
  
  .btn-logout span {
    display: none;
  }
  
  .btn-logout {
    padding: 0.5rem;
  }
}

@media (max-width: 640px) {
  .nav-link {
    padding: 0.375rem;
  }
  
  .navbar-divider {
    display: none;
  }
  
  .user-badge {
    display: none;
  }
}
</style>
