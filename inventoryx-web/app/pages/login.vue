<template>
  <div class="login-container">
    <div class="login-card card">
      <h1 class="text-center mb-lg">InventoryX</h1>
      <h2 class="text-center text-secondary mb-xl" style="font-size: 1.25rem;">Sign in to your account</h2>

      <div v-if="error" class="alert alert-danger mb-md">
        {{ error }}
      </div>

      <form @submit.prevent="handleLogin">
        <div class="form-group mb-md">
          <label for="username" class="mb-sm">Username</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            class="form-control"
            placeholder="Enter your username"
            required
            autofocus
          />
        </div>

        <div class="form-group mb-lg">
          <label for="password" class="mb-sm">Password</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            class="form-control"
            placeholder="Enter your password"
            required
          />
        </div>

        <button type="submit" class="btn btn-primary w-100" :disabled="loading">
          {{ loading ? 'Signing in...' : 'Sign In' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useAuthStore } from '~/stores/auth';

definePageMeta({
  layout: 'auth' // Assuming we might make an auth layout, or empty. Defaults to default.
});

const authStore = useAuthStore();
const form = reactive({
  username: '',
  password: ''
});
const loading = ref(false);
const error = ref('');

async function handleLogin() {
  loading.value = true;
  error.value = '';
  
  const result = await authStore.login({
    username: form.username,
    password: form.password
  });

  if (result.success) {
    // Redirect is handled in store or we do it here. 
    // Store does router.push, but let's be safe.
    // If store didn't redirect:
    if (useRoute().query.redirect) {
       navigateTo(useRoute().query.redirect as string);
    } else {
       navigateTo('/');
    }
  } else {
    error.value = result.error || 'Authentication failed';
  }
  loading.value = false;
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: var(--color-background-alt);
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 2.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-control {
  padding: 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-base);
  transition: border-color 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.1);
}

.alert {
  padding: 0.75rem 1rem;
  border-radius: var(--radius-sm);
  font-size: 0.875rem;
}

.alert-danger {
  background-color: #fef2f2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.w-100 {
  width: 100%;
}

label {
  font-weight: 500;
  color: var(--color-text-secondary);
  display: block;
}
</style>
