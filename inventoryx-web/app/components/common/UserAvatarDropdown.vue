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
  gap: 0.375rem;
  padding: 0.25rem;
  background: transparent;
  border: none;
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all 0.2s ease;
}

.avatar-button:hover {
  background: var(--color-primary-light);
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.5rem;
  height: 2.5rem;
  background: var(--gradient-primary-vivid);
  color: white;
  font-size: 0.9375rem;
  font-weight: 600;
  border-radius: 50%;
  text-transform: uppercase;
  box-shadow: var(--shadow-glow-primary);
  transition: transform 0.2s ease;
}

.avatar-button:hover .avatar {
  transform: scale(1.05);
}

.chevron {
  color: var(--color-text-secondary);
  transition: transform 0.2s ease;
}

.chevron--open {
  transform: rotate(180deg);
}

/* Dropdown Menu with Glassmorphism */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 0.75rem);
  right: 0;
  min-width: 280px;
  background: var(--glass-bg-strong);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  z-index: 1000;
  overflow: hidden;
}

/* Dropdown Animation */
.dropdown-enter-active {
  animation: dropdown-in 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.dropdown-leave-active {
  animation: dropdown-out 0.15s ease-in;
}

@keyframes dropdown-in {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes dropdown-out {
  from {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateY(-5px) scale(0.98);
  }
}

/* Header */
.dropdown-header {
  display: flex;
  align-items: center;
  gap: 0.875rem;
  padding: 1.25rem;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.08) 0%, rgba(168, 85, 247, 0.05) 100%);
}

.header-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 3rem;
  height: 3rem;
  background: var(--gradient-primary-vivid);
  color: white;
  font-size: 1.125rem;
  font-weight: 600;
  border-radius: var(--radius-lg);
  text-transform: uppercase;
  box-shadow: var(--shadow-glow-primary);
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.header-name {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.header-role {
  font-size: 0.75rem;
  font-weight: 600;
  background: var(--gradient-primary-vivid);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
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
  padding: 0.75rem;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
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
  gap: 0.75rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-primary);
}

.setting-icon {
  font-size: 1.125rem;
}

/* Toggle Switch with Gradient */
.toggle-switch {
  position: relative;
  display: inline-block;
  width: 44px;
  height: 24px;
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
  transition: 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 24px;
}

.toggle-slider::before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 50%;
  box-shadow: var(--shadow-sm);
}

.toggle-switch input:checked + .toggle-slider {
  background: var(--gradient-primary-vivid);
}

.toggle-switch input:checked + .toggle-slider::before {
  transform: translateX(20px);
}

/* Language Select */
.language-select {
  padding: 0.375rem 0.75rem;
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--color-text-primary);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  outline: none;
  transition: all 0.2s ease;
}

.language-select:hover,
.language-select:focus {
  border-color: var(--color-primary);
  box-shadow: var(--focus-ring);
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
  padding: 0.75rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-danger);
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
}

.logout-button:hover {
  background: var(--color-danger-light);
  transform: translateY(-1px);
}

.logout-button svg {
  flex-shrink: 0;
}
</style>
