<template>
  <div class="products-page">
    <header class="page-header">
      <h1>Product Catalog</h1>
      <p class="text-secondary">Manage products and SKUs</p>
    </header>

    <div class="page-content">
      <p>Product features coming soon...</p>
      <NuxtLink to="/" class="btn btn-secondary">← Back to Home</NuxtLink>
    </div>
  </div>
</template>

<style scoped>
.products-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 3rem 1.5rem;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 0.5rem;
}

.page-content {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 2rem;
}
</style>
