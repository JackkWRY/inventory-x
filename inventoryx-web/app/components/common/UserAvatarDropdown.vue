<template>
  <div class="dropdown" ref="dropdownRef">
    <button
      class="dropdown__trigger"
      @click="toggleDropdown"
      :aria-expanded="isOpen"
      aria-haspopup="true"
    >
      <div class="avatar avatar--md">
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
      <div v-if="isOpen" class="dropdown__menu">
        <!-- Header: User Info -->
        <div class="dropdown__header">
          <div class="avatar avatar--lg">
            {{ userInitial }}
          </div>
          <div class="header-info">
            <span class="header-name">{{ fullName }}</span>
            <span class="header-role">{{ userRole }}</span>
          </div>
        </div>

        <div class="dropdown__divider"></div>

        <!-- Settings Section -->
        <div class="dropdown__section">
          <!-- Dark Mode Toggle -->
          <div class="dropdown__item dropdown__item--setting">
            <div class="setting-label">
              <span class="setting-icon">{{ isDark ? "🌙" : "☀️" }}</span>
              <span>{{ t("theme.darkMode") }}</span>
            </div>
            <label class="toggle-switch">
              <input type="checkbox" :checked="isDark" @change="toggleTheme" />
              <span class="toggle-switch__slider"></span>
            </label>
          </div>

          <!-- Language Selector -->
          <div class="dropdown__item dropdown__item--setting">
            <div class="setting-label">
              <span class="setting-icon">🌐</span>
              <span>{{ t("settings.language") }}</span>
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

        <div class="dropdown__divider"></div>

        <!-- Footer: Logout -->
        <div class="dropdown__footer">
          <button class="logout-button" @click="handleLogout">
            <svg
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
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
              <polyline points="16 17 21 12 16 7"></polyline>
              <line x1="21" y1="12" x2="9" y2="12"></line>
            </svg>
            <span>{{ t("common.logout") }}</span>
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
/**
 * UserAvatarDropdown Component
 *
 * User profile dropdown with theme toggle, language selector, and logout.
 * Uses global CSS classes for dropdown, avatar, and toggle-switch patterns.
 */
import { useAuthStore } from "~/stores/auth";

const { t, locale, setLocale } = useI18n();
const { isDark, toggleTheme } = useTheme();
const authStore = useAuthStore();

const isOpen = ref(false);
const dropdownRef = ref<HTMLElement | null>(null);

const userInitial = computed(() => {
  const firstName = authStore.userData.firstName;
  return firstName ? firstName.charAt(0).toUpperCase() : "U";
});

const fullName = computed(() => {
  const { firstName, lastName } = authStore.userData;
  if (firstName || lastName) {
    return `${firstName || ""} ${lastName || ""}`.trim();
  }
  return "User";
});

const userRole = computed(() => {
  const roles = authStore.userData.roles;
  return roles?.length ? roles[0] : "";
});

function toggleDropdown() {
  isOpen.value = !isOpen.value;
}

function handleLanguageChange(event: Event) {
  const target = event.target as HTMLSelectElement;
  setLocale(target.value as "en" | "th");
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
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>

<style scoped>
/* Component-specific styles only - base styles from global main.css */

/* Hover effect on avatar */
.dropdown__trigger:hover .avatar {
  transform: scale(1.05);
}

/* Header info layout */
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

/* Setting item label */
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

/* Logout button */
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
