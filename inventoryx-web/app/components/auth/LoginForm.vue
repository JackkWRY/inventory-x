<template>
  <div class="login-card">
    <!-- Header -->
    <div class="login-header">
      <div class="login-logo">
        <span class="logo-icon">📦</span>
        <h1 class="logo-text">InventoryX</h1>
      </div>
      <p class="login-subtitle">Sign in to your account</p>
    </div>

    <!-- Error Alert -->
    <div v-if="error" class="alert alert-danger login-alert">
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
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="15" y1="9" x2="9" y2="15"></line>
        <line x1="9" y1="9" x2="15" y2="15"></line>
      </svg>
      <span>{{ error }}</span>
    </div>

    <!-- Form -->
    <form @submit.prevent="handleSubmit" class="login-form">
      <div class="form-group">
        <label for="username" class="form-label">Username</label>
        <div class="input-with-icon">
          <svg
            class="input-with-icon__icon"
            xmlns="http://www.w3.org/2000/svg"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          <input
            id="username"
            v-model="form.username"
            type="text"
            class="form-input input-with-icon__input"
            placeholder="Enter your username"
            required
            autofocus
          />
        </div>
      </div>

      <div class="form-group">
        <label for="password" class="form-label">Password</label>
        <div class="input-with-icon">
          <svg
            class="input-with-icon__icon"
            xmlns="http://www.w3.org/2000/svg"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
          </svg>
          <input
            id="password"
            v-model="form.password"
            type="password"
            class="form-input input-with-icon__input"
            placeholder="Enter your password"
            required
          />
        </div>
      </div>

      <button type="submit" class="btn btn--primary btn--lg btn--full" :disabled="loading">
        <span v-if="loading" class="spinner spinner--sm spinner--light"></span>
        {{ loading ? "Signing in..." : "Sign In" }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
/**
 * LoginForm Component
 * 
 * Authentication form with username and password fields.
 * Uses global form and button classes for consistent styling.
 */
import { reactive } from "vue";
import type { LoginCommand } from "~/types/auth";

const props = defineProps<{
  loading: boolean;
  error?: string;
}>();

const emit = defineEmits<{
  submit: [data: LoginCommand];
}>();

const form = reactive({
  username: "",
  password: "",
});

const handleSubmit = () => {
  emit("submit", {
    username: form.username,
    password: form.password,
  });
};
</script>

<style scoped>
/* Login Card - unique glassmorphism styling */
.login-card {
  background: var(--glass-bg-strong);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  padding: 2.5rem;
  box-shadow: var(--shadow-xl);
  position: relative;
  overflow: hidden;
}

/* Subtle gradient border effect */
.login-card::before {
  content: "";
  position: absolute;
  inset: 0;
  padding: 1px;
  border-radius: inherit;
  background: linear-gradient(
    135deg,
    rgba(99, 102, 241, 0.3),
    transparent 50%,
    rgba(168, 85, 247, 0.2)
  );
  -webkit-mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  mask-composite: exclude;
  pointer-events: none;
}

/* Header */
.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.625rem;
  margin-bottom: 0.75rem;
}

.logo-icon {
  font-size: 2.25rem;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.logo-text {
  font-size: 1.875rem;
  font-weight: 700;
  background: var(--gradient-primary-vivid);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.025em;
  margin: 0;
}

.login-subtitle {
  color: var(--color-text-secondary);
  font-size: 0.9375rem;
  margin: 0;
}

/* Alert override for login */
.login-alert {
  margin-bottom: 1.5rem;
}

/* Form layout */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

/* Input with icon pattern */
.input-with-icon {
  position: relative;
}

.input-with-icon__icon {
  position: absolute;
  left: 0.875rem;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-muted);
  pointer-events: none;
  transition: color 0.2s ease;
}

.input-with-icon:focus-within .input-with-icon__icon {
  color: var(--color-primary);
}

.input-with-icon__input {
  padding-left: 2.75rem;
}

/* Responsive */
@media (max-width: 480px) {
  .login-card {
    padding: 1.5rem;
  }

  .logo-text {
    font-size: 1.5rem;
  }
}
</style>
