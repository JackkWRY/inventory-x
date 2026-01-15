<template>
  <div class="home">
    <header class="home__header">
      <h1 class="home__title">{{ $t('common.appName') }}</h1>
      <p class="home__subtitle">{{ $t('common.welcome') }}</p>
      <div class="home__lang-switcher">
        <LanguageSwitcher />
      </div>
    </header>

    <nav class="home__nav">
      <NuxtLink to="/dashboard" class="nav-card nav-card--primary">
        <h2>{{ $t('navigation.dashboard') }}</h2>
        <p>{{ $t('dashboard.subtitle') }}</p>
      </NuxtLink>

      <NuxtLink to="/inventory" class="nav-card">
        <h2>{{ $t('navigation.inventory') }}</h2>
        <p>{{ $t('inventory.subtitle') }}</p>
      </NuxtLink>

      <NuxtLink to="/products" class="nav-card">
        <h2>{{ $t('navigation.products') }}</h2>
        <p>{{ $t('products.subtitle') }}</p>
      </NuxtLink>

      <NuxtLink to="/warehouses" class="nav-card">
        <h2>{{ $t('navigation.warehouses') }}</h2>
        <p>{{ $t('warehouses.subtitle') }}</p>
      </NuxtLink>
    </nav>
  </div>
</template>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 3rem 1.5rem;
}

.home__header {
  text-align: center;
  margin-bottom: 4rem;
}

.home__title {
  font-size: 3rem;
  font-weight: 500;
  letter-spacing: -0.02em;
  color: #1a1a1a;
  margin-bottom: 0.5rem;
}

.home__subtitle {
  font-size: 1.25rem;
  color: #5f6368;
}

.home__lang-switcher {
  margin-top: 1.5rem;
  display: flex;
  justify-content: center;
}


.home__nav {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.nav-card {
  padding: 2rem;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  text-decoration: none;
  color: inherit;
  transition: all 0.2s;
}

.nav-card:hover {
  border-color: #1a73e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-card h2 {
  font-size: 1.5rem;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 0.5rem;
}

.nav-card p {
  font-size: 0.875rem;
  color: #5f6368;
}

.nav-card--primary {
  background: linear-gradient(135deg, #e8f0fe 0%, #ffffff 100%);
  border-color: #1a73e8;
}

.nav-card--primary h2 {
  color: #1a73e8;
}
</style>
