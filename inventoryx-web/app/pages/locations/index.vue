<script setup lang="ts">
import { useLocationStore } from "~/stores/location";
import type { Location, CreateLocationCommand, UpdateLocationCommand } from "~/types/location";

definePageMeta({
  title: "Location Management",
  middleware: ["auth"], // Or 'admin' if restricted
});

const { t } = useI18n();
const locationStore = useLocationStore();
const { locations, loading, error, pagination } = storeToRefs(locationStore);

// Modal state
const showDialog = ref(false);
const selectedLocation = ref<Location | null>(null);

// Fetch data
onMounted(async () => {
    await locationStore.fetchLocations();
});

const handleCreate = () => {
    selectedLocation.value = null;
    showDialog.value = true;
};

const handleEdit = (location: Location) => {
    selectedLocation.value = location;
    showDialog.value = true;
};

const handleSubmit = async (payload: CreateLocationCommand | UpdateLocationCommand) => {
    try {
        if (selectedLocation.value) {
            await locationStore.updateLocation(selectedLocation.value.id, payload as UpdateLocationCommand);
            /* i18n needed */
        } else {
            await locationStore.createLocation(payload as CreateLocationCommand);
        }
        showDialog.value = false;
    } catch (e) {
        // Error handled in store
    }
};

const handlePageChange = (page: number) => {
    locationStore.changePage(page);
};

const handlePageSizeChange = (size: number) => {
    locationStore.changePageSize(size);
};

const handleSearch = (query: string) => {
    locationStore.fetchLocations(0, pagination.value.pageSize, query);
};
</script>

<template>
  <div class="location-page">
    <header class="page-header">
      <div class="page-header__content">
        <h1 class="page-header__title">{{ t("locations.title") }}</h1>
        <p class="page-header__subtitle">{{ t("locations.subtitle") }}</p>
      </div>
      <div class="page-header__actions">
        <NuxtLink to="/" class="btn btn--ghost">
          ← {{ t("common.back") }}
        </NuxtLink>
      </div>
    </header>

    <CommonErrorBanner v-if="error" :message="error" />

    <LocationList
      :locations="locations"
      :loading="loading"
      @create="handleCreate"
      @edit="handleEdit"
      @search="handleSearch"
    />

    <CommonPagination
      :current-page="pagination.currentPage"
      :total-pages="pagination.totalPages"
      :total-items="pagination.totalElements"
      :page-size="pagination.pageSize"
      :is-first="pagination.isFirst"
      :is-last="pagination.isLast"
      @page-change="handlePageChange"
      @page-size-change="handlePageSizeChange"
    />

    <LocationDialog
      :open="showDialog"
      :location="selectedLocation"
      :loading="loading"
      :error="error"
      @close="showDialog = false"
      @submit="handleSubmit"
    />
  </div>
</template>

<style scoped>
.location-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.5rem;
  gap: 1rem;
  flex-wrap: wrap;
}

.page-header__content {
  flex: 1;
}

.page-header__title {
  font-size: 1.75rem;
  font-weight: 500;
  color: var(--color-text-primary);
  margin: 0 0 0.25rem 0;
}

.page-header__subtitle {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  margin: 0;
}

.page-header__actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-shrink: 0;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  border: none;
  font-weight: 500;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
}

.btn--ghost {
  background: transparent;
  color: var(--color-text-secondary);
  border: 1px solid transparent;
}

.btn--ghost:hover {
  background: var(--color-surface-hover);
  color: var(--color-text-primary);
}
</style>
