<script setup lang="ts">
import { type Location, LocationType, LocationStatus } from "~/types/location";
import { useDebounceFn } from "@vueuse/core";

const props = defineProps<{
  locations: Location[];
  loading: boolean;
}>();

const emit = defineEmits(["edit", "create", "search"]);

const { t } = useI18n();
const searchQuery = ref("");

const handleSearch = useDebounceFn(() => {
  emit("search", searchQuery.value);
}, 300);

const getStatusBadgeClass = (status: LocationStatus) => {
  return status === LocationStatus.ACTIVE ? "badge--success" : "badge--danger";
};
</script>

<template>
  <div class="list-container">
    <!-- Header with Actions -->
    <div class="list-header">
      <div class="search-field">
        <div class="search-input-wrapper">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="search-icon"
          >
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
          <input
            id="search-location"
            v-model="searchQuery"
            type="text"
            :placeholder="t('common.search') + '...'"
            class="input with-icon"
            @input="handleSearch"
          />
        </div>
      </div>
      <div class="list-header__actions">
        <button class="btn btn--primary" @click="$emit('create')">
          + {{ t("locations.create") }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>{{ t("locations.name") }}</th>
            <th>{{ t("locations.type") }}</th>
            <th>{{ t("locations.address") }}</th>
            <th>{{ t("locations.status") }}</th>
            <th class="text-right">{{ t("common.actions") }}</th>
          </tr>
        </thead>
        <tbody>
          <!-- Loading State -->
          <tr v-if="loading">
             <td colspan="5" class="loading-state">
               <div class="spinner"></div>
             </td>
          </tr>
          
          <!-- Empty State -->
          <tr v-else-if="locations.length === 0">
            <td colspan="5" class="empty-state">
              {{ t("common.noData") }}
            </td>
          </tr>

          <!-- Data Rows -->
          <tr v-else v-for="location in locations" :key="location.id">
            <td class="font-medium text-mono">{{ location.name }}</td>
            <td>
              <span class="badge badge--gray">{{ location.type }}</span>
            </td>
            <td class="text-truncate" style="max-width: 250px" :title="location.address">
                {{ location.address || '-' }}
            </td>
            <td>
              <span :class="['badge', getStatusBadgeClass(location.status)]">
                {{ location.status }}
              </span>
            </td>
            <td class="text-right">
              <button class="btn btn--small btn--ghost" @click="$emit('edit', location)">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
                {{ t("common.edit") }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
/* All base styles now come from global main.css */
/* Only component-specific overrides remain here */
.table-container {
  overflow-x: auto;
}
</style>
