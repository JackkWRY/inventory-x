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
    <div v-if="error" class="login-alert">
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
        <div class="input-wrapper">
          <svg
            class="input-icon"
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
            class="form-input"
            placeholder="Enter your username"
            required
            autofocus
          />
        </div>
      </div>

      <div class="form-group">
        <label for="password" class="form-label">Password</label>
        <div class="input-wrapper">
          <svg
            class="input-icon"
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
            class="form-input"
            placeholder="Enter your password"
            required
          />
        </div>
      </div>

      <button type="submit" class="btn-submit" :disabled="loading">
        <span v-if="loading" class="spinner"></span>
        {{ loading ? "Signing in..." : "Sign In" }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
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
/* Login Card with Glassmorphism */
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

/* Alert */
.login-alert {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: var(--color-danger-light);
  color: var(--color-danger);
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
  border: 1px solid rgba(239, 68, 68, 0.2);
}

.login-alert svg {
  flex-shrink: 0;
}

/* Form */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-secondary);
  margin-bottom: 0.5rem;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 0.875rem;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-muted);
  pointer-events: none;
  transition: color 0.2s ease;
}

.input-wrapper:focus-within .input-icon {
  color: var(--color-primary);
}

.form-input {
  width: 100%;
  padding: 0.875rem 1rem 0.875rem 2.75rem;
  font-size: 0.9375rem;
  color: var(--color-text-primary);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  transition: all 0.2s ease;
}

.form-input::placeholder {
  color: var(--color-text-muted);
}

.form-input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: var(--focus-ring), var(--shadow-glow-primary);
}

.form-input:hover:not(:focus) {
  border-color: var(--color-text-muted);
}

/* Submit Button with Gradient */
.btn-submit {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  width: 100%;
  padding: 0.9375rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  color: white;
  background: var(--gradient-primary-vivid);
  border: none;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 0.5rem;
  box-shadow: var(--shadow-glow-primary);
  position: relative;
  overflow: hidden;
}

/* Shimmer effect */
.btn-submit::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: left 0.5s ease;
}

.btn-submit:hover:not(:disabled)::before {
  left: 100%;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md), var(--shadow-glow-primary);
}

.btn-submit:focus-visible {
  outline: none;
  box-shadow: var(--focus-ring), var(--shadow-glow-primary);
}

.btn-submit:active:not(:disabled) {
  transform: translateY(0);
}

.btn-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

/* Spinner */
.spinner {
  width: 1.25rem;
  height: 1.25rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Responsive */
@media (max-width: 480px) {
  .login-card {
    padding: 1.5rem;
  }

  .logo-text {
    font-size: 1.5rem;
  }

  .form-input {
    padding: 0.75rem 0.875rem 0.75rem 2.5rem;
  }
}
</style>
