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
 * Landing Page Styles
 * 
 * DESIGN SYSTEM:
 * - 2x2 grid for balanced layout
 * - SVG icons for professional look
 * - Card hover animations
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
  margin-bottom: 3rem;
}

.home__controls {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  gap: 0.5rem;
}

.home__title {
  font-size: 3rem;
  font-weight: 600;
  letter-spacing: -0.03em;
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
}

.home__subtitle {
  font-size: 1.125rem;
  color: var(--color-text-secondary);
}

/* ===== Navigation Grid ===== */
.home__nav {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}

/* ===== Navigation Card ===== */
.nav-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 2rem;
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  text-decoration: none;
  color: inherit;
  transition: all 0.25s ease;
}

.nav-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px -8px rgba(0, 0, 0, 0.15);
}

/* Icon Container */
.nav-card__icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  transition: all 0.25s ease;
}

.nav-card__icon svg {
  width: 28px;
  height: 28px;
}

/* Content */
.nav-card__content {
  text-align: center;
}

.nav-card__title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 0.25rem;
}

.nav-card__description {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  line-height: 1.4;
}

/* ===== Card Variants (Color Themes) ===== */

/* Dashboard - Blue */
.nav-card--dashboard .nav-card__icon {
  background: var(--color-info-light);
  color: var(--color-info);
}
.nav-card--dashboard:hover {
  border-color: var(--color-info);
}

/* Inventory - Green */
.nav-card--inventory .nav-card__icon {
  background: var(--color-success-light);
  color: var(--color-success);
}
.nav-card--inventory:hover {
  border-color: var(--color-success);
}

/* Products - Purple */
.nav-card--products .nav-card__icon {
  background: var(--color-primary-light);
  color: var(--color-primary);
}
.nav-card--products:hover {
  border-color: var(--color-primary);
}

/* Warehouses - Orange */
.nav-card--warehouses .nav-card__icon {
  background: var(--color-warning-light);
  color: var(--color-warning);
}
.nav-card--warehouses:hover {
  border-color: var(--color-warning);
}

/* ===== Responsive: Mobile ===== */
@media (max-width: 640px) {
  .home {
    padding: 2rem 1rem;
  }

  .home__title {
    font-size: 2rem;
  }

  .home__nav {
    grid-template-columns: 1fr;
  }

  .nav-card {
    padding: 1.5rem;
  }
}
</style>
