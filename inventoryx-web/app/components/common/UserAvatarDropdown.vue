<template>
  <div class="user-avatar-dropdown" ref="dropdownRef">
    <button 
      class="avatar-button" 
      @click="toggleDropdown"
      :aria-expanded="isOpen"
      aria-haspopup="true"
    >
      <div class="avatar">
        {{ userInitial }}
      </div>
      <svg 
        class="chevron" 
        :class="{ 'chevron--open': isOpen }"
        xmlns="http://www.w3.org/2000/svg" 
        width="16" 
        height="16" 
        viewBox="0 0 24 24" 
        fill="none" 
        stroke="currentColor" 
        stroke-width="2" 
        stroke-linecap="round" 
        stroke-linejoin="round"
      >
        <polyline points="6 9 12 15 18 9"></polyline>
      </svg>
    </button>

    <Transition name="dropdown">
      <div v-if="isOpen" class="dropdown-menu">
        <!-- Header: User Info -->
        <div class="dropdown-header">
          <div class="header-avatar">
            {{ userInitial }}
          </div>
          <div class="header-info">
            <span class="header-name">{{ fullName }}</span>
            <span class="header-role">{{ userRole }}</span>
          </div>
        </div>

        <div class="dropdown-divider"></div>

        <!-- Settings Section -->
        <div class="dropdown-section">
          <!-- Dark Mode Toggle -->
          <div class="dropdown-item dropdown-item--setting">
            <div class="setting-label">
              <span class="setting-icon">{{ isDark ? '🌙' : '☀️' }}</span>
              <span>{{ t('theme.darkMode') }}</span>
            </div>
            <label class="toggle-switch">
              <input 
                type="checkbox" 
                :checked="isDark" 
                @change="toggleTheme"
              />
              <span class="toggle-slider"></span>
            </label>
          </div>

          <!-- Language Selector -->
          <div class="dropdown-item dropdown-item--setting">
            <div class="setting-label">
              <span class="setting-icon">🌐</span>
              <span>{{ t('settings.language') }}</span>
            </div>
            <select 
              class="language-select" 
              :value="locale" 
              @change="handleLanguageChange"
            >
              <option value="en">English</option>
              <option value="th">ภาษาไทย</option>
            </select>
          </div>
        </div>

        <div class="dropdown-divider"></div>

        <!-- Footer: Logout -->
        <div class="dropdown-footer">
          <button class="logout-button" @click="handleLogout">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
              <polyline points="16 17 21 12 16 7"></polyline>
              <line x1="21" y1="12" x2="9" y2="12"></line>
            </svg>
            <span>{{ t('common.logout') }}</span>
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth';

const { t, locale, setLocale } = useI18n();
const { isDark, toggleTheme } = useTheme();
const authStore = useAuthStore();

const isOpen = ref(false);
const dropdownRef = ref<HTMLElement | null>(null);

const userInitial = computed(() => {
  const firstName = authStore.userData.firstName;
  return firstName ? firstName.charAt(0).toUpperCase() : 'U';
});

const fullName = computed(() => {
  const { firstName, lastName } = authStore.userData;
  if (firstName || lastName) {
    return `${firstName || ''} ${lastName || ''}`.trim();
  }
  return 'User';
});

const userRole = computed(() => {
  const roles = authStore.userData.roles;
  return roles?.length ? roles[0] : '';
});

function toggleDropdown() {
  isOpen.value = !isOpen.value;
}

function handleLanguageChange(event: Event) {
  const target = event.target as HTMLSelectElement;
  setLocale(target.value as 'en' | 'th');
}

function handleLogout() {
  isOpen.value = false;
  authStore.logout();
}

function handleClickOutside(event: MouseEvent) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    isOpen.value = false;
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.user-avatar-dropdown {
  position: relative;
}

.avatar-button {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem;
  background: transparent;
  border: none;
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.avatar-button:hover {
  background: var(--color-surface-hover);
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  background: var(--color-primary);
  color: white;
  font-size: 0.875rem;
  font-weight: 600;
  border-radius: 50%;
  text-transform: uppercase;
}

.chevron {
  color: var(--color-text-secondary);
  transition: transform var(--transition-fast);
}

.chevron--open {
  transform: rotate(180deg);
}

/* Dropdown Menu */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 0.5rem);
  right: 0;
  min-width: 260px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  overflow: hidden;
}

/* Dropdown Animation */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* Header */
.dropdown-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
}

.header-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.75rem;
  height: 2.75rem;
  background: var(--color-primary);
  color: white;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 50%;
  text-transform: uppercase;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.header-name {
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.header-role {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--color-primary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Divider */
.dropdown-divider {
  height: 1px;
  background: var(--color-border);
  margin: 0;
}

/* Section */
.dropdown-section {
  padding: 0.5rem;
}

/* Dropdown Item */
.dropdown-item {
  display: flex;
  align-items: center;
  padding: 0.625rem 0.75rem;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background var(--transition-fast);
}

.dropdown-item:hover {
  background: var(--color-surface-hover);
}

.dropdown-item--setting {
  justify-content: space-between;
}

.setting-label {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  font-size: 0.875rem;
  color: var(--color-text-primary);
}

.setting-icon {
  font-size: 1rem;
}

/* Toggle Switch */
.toggle-switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 22px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--color-border);
  transition: 0.3s;
  border-radius: 22px;
}

.toggle-slider::before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

.toggle-switch input:checked + .toggle-slider {
  background-color: var(--color-primary);
}

.toggle-switch input:checked + .toggle-slider::before {
  transform: translateX(18px);
}

/* Language Select */
.language-select {
  padding: 0.375rem 0.625rem;
  font-size: 0.8125rem;
  color: var(--color-text-primary);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  outline: none;
  transition: border-color var(--transition-fast);
}

.language-select:hover,
.language-select:focus {
  border-color: var(--color-primary);
}

/* Footer */
.dropdown-footer {
  padding: 0.5rem;
}

.logout-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  width: 100%;
  padding: 0.625rem 0.75rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-danger);
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background var(--transition-fast);
}

.logout-button:hover {
  background: var(--color-danger-light);
}

.logout-button svg {
  flex-shrink: 0;
}
</style>
