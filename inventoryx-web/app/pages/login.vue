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
    password: data.password,
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
  position: relative;
  overflow: hidden;
}

.login-container {
  position: relative;
  width: 100%;
  max-width: 420px;
  z-index: 1;
}

/* Animated Background Pattern */
.login-bg {
  position: fixed;
  inset: 0;
  background:
    radial-gradient(
      ellipse 80% 50% at 20% 40%,
      rgba(99, 102, 241, 0.15) 0%,
      transparent 50%
    ),
    radial-gradient(
      ellipse 60% 40% at 80% 60%,
      rgba(168, 85, 247, 0.12) 0%,
      transparent 50%
    ),
    radial-gradient(
      ellipse 50% 50% at 50% 100%,
      rgba(59, 130, 246, 0.1) 0%,
      transparent 50%
    ),
    var(--color-background-alt);
  z-index: 0;
  animation: bgShift 20s ease-in-out infinite;
}

@keyframes bgShift {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

/* Decorative Floating Orb */
.login-bg::before {
  content: "";
  position: absolute;
  top: 20%;
  left: 10%;
  width: 300px;
  height: 300px;
  background: var(--gradient-primary);
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
  animation: float 8s ease-in-out infinite;
}

.login-bg::after {
  content: "";
  position: absolute;
  bottom: 20%;
  right: 10%;
  width: 250px;
  height: 250px;
  background: var(--gradient-info);
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.15;
  animation: float 6s ease-in-out infinite reverse;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.05);
  }
}
</style>
