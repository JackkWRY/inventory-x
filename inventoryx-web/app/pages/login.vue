<template>
  <div class="login-page">
    <div class="login-container">
      <!-- Background Pattern -->
      <div class="login-bg"></div>

      <!-- Login Form Component -->
      <LoginForm :loading="loading" :error="error" @submit="handleLogin" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAuthStore } from "~/stores/auth";
import LoginForm from "~/components/auth/LoginForm.vue";
import type { LoginCommand } from "~/types/auth";

definePageMeta({
  layout: "auth",
});

const authStore = useAuthStore();
const loading = ref(false);
const error = ref("");

async function handleLogin(data: LoginCommand) {
  loading.value = true;
  error.value = "";

  const result = await authStore.login({
    username: data.username,
    password: data.password
  });

  if (result.success) {
    if (useRoute().query.redirect) {
      navigateTo(useRoute().query.redirect as string);
    } else {
      navigateTo("/");
    }
  } else {
    error.value = result.error || "Authentication failed";
  }
  loading.value = false;
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-background-alt);
  padding: 1rem;
}

.login-container {
  position: relative;
  width: 100%;
  max-width: 420px;
}

/* Background Pattern */
.login-bg {
  position: fixed;
  inset: 0;
  background:
    radial-gradient(
      circle at 20% 80%,
      var(--color-primary-light) 0%,
      transparent 50%
    ),
    radial-gradient(
      circle at 80% 20%,
      var(--color-info-light) 0%,
      transparent 50%
    ),
    var(--color-background-alt);
  z-index: -1;
}
</style>
