<script setup lang="ts">
import { useDashboardStore } from "~/stores/dashboard";
import StatCard from "~/components/dashboard/StatCard.vue";
import RecentActivityList from "~/components/dashboard/RecentActivityList.vue";
import { storeToRefs } from "pinia";

definePageMeta({
  middleware: ["auth"],
});

const { t } = useI18n();
const dashboardStore = useDashboardStore();
const { data, loading, error } = storeToRefs(dashboardStore);

const currentDate = ref('');

onMounted(() => {
  currentDate.value = new Date().toLocaleDateString();
  dashboardStore.fetchDashboardData();
});

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD", // TODO: Get from config
  }).format(value);
};
</script>

<template>
  <div class="dashboard-page">
    <header class="page-header">
      <div>
        <h1 class="page-title">{{ t("dashboard.title") }}</h1>
        <p class="text-secondary">{{ currentDate }}</p>
      </div>
      <button class="btn btn-secondary" @click="dashboardStore.fetchDashboardData">
        Refresh
      </button>
    </header>

    <CommonErrorBanner v-if="error" :message="error" />

    <div v-if="loading && !data" class="loading-state">
      <span class="spinner"></span> {{ t("common.loading") }}
    </div>

    <div v-if="data" class="dashboard-content">
      <!-- KPI Cards -->
      <div class="stats-grid">
        <StatCard
          :title="t('dashboard.totalValue')"
          :value="formatCurrency(data.totalStockValue)"
          variant="primary"
        >
          <template #icon>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"></line><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg>
          </template>
        </StatCard>
        
        <StatCard
          :title="t('dashboard.lowStock')"
          :value="data.lowStockItems"
          variant="warning"
        >
          <template #icon>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
          </template>
        </StatCard>

        <StatCard
          :title="t('dashboard.totalItems')"
          :value="data.totalItems"
          variant="info"
        >
          <template #icon>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path><polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline><line x1="12" y1="22.08" x2="12" y2="12"></line></svg>
          </template>
        </StatCard>

        <StatCard
          :title="t('dashboard.totalLocations')"
          :value="data.totalLocations"
          variant="success"
        >
          <template #icon>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
          </template>
        </StatCard>
      </div>

      <!-- Recent Activity -->
      <div class="mt-lg">
        <RecentActivityList :activities="data.recentActivities" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-title {
  margin: 0;
  font-size: 1.875rem;
  color: var(--color-text-primary);
}

.text-secondary {
  color: var(--color-text-secondary);
  margin: 0.25rem 0 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.mt-lg {
  margin-top: 2rem;
}

.loading-state {
  text-align: center;
  padding: 3rem;
  color: var(--color-text-secondary);
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid var(--color-border);
  background: white;
}
</style>
