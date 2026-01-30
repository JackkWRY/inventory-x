<script setup lang="ts">
/**
 * Landing Page
 *
 * Main entry point with navigation cards.
 * BEST PRACTICE: Clean, organized layout with proper visual hierarchy.
 */
definePageMeta({
  title: "InventoryX - Home",
});
</script>

<template>
  <div class="home">
    <!-- Header -->
    <header class="home__header">
      <h1 class="home__title">{{ $t("common.appName") }}</h1>
      <p class="home__subtitle">{{ $t("common.welcome") }}</p>
    </header>

    <!-- Navigation Cards - 2x2 Grid -->
    <nav class="home__nav">
      <!-- Dashboard Card -->
      <NuxtLink to="/dashboard" class="nav-card nav-card--dashboard">
        <div class="nav-card__icon">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <rect x="3" y="3" width="7" height="7"></rect>
            <rect x="14" y="3" width="7" height="7"></rect>
            <rect x="14" y="14" width="7" height="7"></rect>
            <rect x="3" y="14" width="7" height="7"></rect>
          </svg>
        </div>
        <div class="nav-card__content">
          <h2 class="nav-card__title">{{ $t("navigation.dashboard") }}</h2>
          <p class="nav-card__description">{{ $t("dashboard.subtitle") }}</p>
        </div>
      </NuxtLink>

      <!-- Inventory Card -->
      <NuxtLink to="/inventory" class="nav-card nav-card--inventory">
        <div class="nav-card__icon">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path
              d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
            ></path>
          </svg>
        </div>
        <div class="nav-card__content">
          <h2 class="nav-card__title">{{ $t("navigation.inventory") }}</h2>
          <p class="nav-card__description">{{ $t("inventory.subtitle") }}</p>
        </div>
      </NuxtLink>

      <!-- Products Card -->
      <NuxtLink to="/products" class="nav-card nav-card--products">
        <div class="nav-card__icon">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path
              d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"
            ></path>
            <line x1="7" y1="7" x2="7.01" y2="7"></line>
          </svg>
        </div>
        <div class="nav-card__content">
          <h2 class="nav-card__title">{{ $t("navigation.products") }}</h2>
          <p class="nav-card__description">{{ $t("products.subtitle") }}</p>
        </div>
      </NuxtLink>

      <!-- Locations Card -->
      <NuxtLink to="/locations" class="nav-card nav-card--warehouses">
        <div class="nav-card__icon">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
            <polyline points="9 22 9 12 15 12 15 22"></polyline>
          </svg>
        </div>
        <div class="nav-card__content">
          <h2 class="nav-card__title">{{ $t("locations.title") }}</h2>
          <p class="nav-card__description">{{ $t("locations.subtitle") }}</p>
        </div>
      </NuxtLink>
    </nav>
  </div>
</template>

<style scoped>
/**
 * Landing Page Styles - Modern Premium
 * Gradient icons, glassmorphism cards, shine animations
 */

.home {
  max-width: 900px;
  margin: 0 auto;
  padding: 3rem 1.5rem;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* ===== Header ===== */
.home__header {
  position: relative;
  text-align: center;
  margin-bottom: 3.5rem;
}

.home__controls {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  gap: 0.5rem;
}

.home__title {
  font-size: 3.5rem;
  font-weight: 700;
  letter-spacing: -0.04em;
  background: var(--gradient-primary-vivid);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.75rem;
}

.home__subtitle {
  font-size: 1.25rem;
  color: var(--color-text-secondary);
  font-weight: 500;
}

/* ===== Navigation Grid ===== */
.home__nav {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}

/* ===== Navigation Card with Glassmorphism ===== */
.nav-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.25rem;
  padding: 2.5rem 2rem;
  background: var(--glass-bg-strong);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  text-decoration: none;
  color: inherit;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

/* Shine overlay */
.nav-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -150%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.15),
    transparent
  );
  transform: skewX(-25deg);
  transition: left 0.6s ease;
}

.nav-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-lg);
}

.nav-card:hover::before {
  left: 150%;
}

/* Icon Container with Gradient */
.nav-card__icon {
  width: 4rem;
  height: 4rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  transition: all 0.3s ease;
  color: white;
}

.nav-card:hover .nav-card__icon {
  transform: scale(1.1);
}

.nav-card__icon svg {
  width: 2rem;
  height: 2rem;
}

/* Content */
.nav-card__content {
  text-align: center;
}

.nav-card__title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 0.375rem;
}

.nav-card__description {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

/* ===== Card Variants with Gradients ===== */

/* Dashboard - Blue */
.nav-card--dashboard .nav-card__icon {
  background: var(--gradient-info);
  box-shadow: var(--shadow-glow-info);
}
.nav-card--dashboard:hover {
  border-color: var(--color-info);
  box-shadow: var(--shadow-lg), var(--shadow-glow-info);
}

/* Inventory - Green */
.nav-card--inventory .nav-card__icon {
  background: var(--gradient-success);
  box-shadow: var(--shadow-glow-success);
}
.nav-card--inventory:hover {
  border-color: var(--color-success);
  box-shadow: var(--shadow-lg), var(--shadow-glow-success);
}

/* Products - Purple */
.nav-card--products .nav-card__icon {
  background: var(--gradient-primary);
  box-shadow: var(--shadow-glow-primary);
}
.nav-card--products:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-lg), var(--shadow-glow-primary);
}

/* Warehouses - Orange */
.nav-card--warehouses .nav-card__icon {
  background: var(--gradient-warning);
  box-shadow: var(--shadow-glow-warning);
}
.nav-card--warehouses:hover {
  border-color: var(--color-warning);
  box-shadow: var(--shadow-lg), var(--shadow-glow-warning);
}

/* ===== Responsive: Mobile ===== */
@media (max-width: 640px) {
  .home {
    padding: 2rem 1rem;
  }

  .home__title {
    font-size: 2.25rem;
  }

  .home__subtitle {
    font-size: 1rem;
  }

  .home__nav {
    grid-template-columns: 1fr;
  }

  .nav-card {
    padding: 1.75rem 1.5rem;
  }

  .nav-card__icon {
    width: 3.5rem;
    height: 3.5rem;
  }

  .nav-card__icon svg {
    width: 1.75rem;
    height: 1.75rem;
  }
}
</style>
