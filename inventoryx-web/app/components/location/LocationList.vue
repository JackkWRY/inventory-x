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
  <div class="location-list">
    <!-- Header with Actions -->
    <div class="location-list__header">
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
          <button v-if="searchQuery" class="clear-btn" @click="searchQuery = ''; handleSearch()">✕</button>
        </div>
      </div>
      <div class="location-list__actions">
        <button class="btn btn--primary" @click="$emit('create')">
          <span class="icon">+</span>
          {{ t("locations.create") }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-container">
      <table class="location-table">
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
             <td colspan="5" class="text-center py-8">
               <div class="spinner"></div>
             </td>
          </tr>
          
          <!-- Empty State -->
          <tr v-else-if="locations.length === 0">
            <td colspan="5" class="text-center py-8 text-muted">
              {{ t("common.noData") }}
            </td>
          </tr>

          <!-- Data Rows -->
          <tr v-else v-for="location in locations" :key="location.id" class="hover-row">
            <td class="font-medium location-name">{{ location.name }}</td>
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
              <button class="action-btn" @click="$emit('edit', location)">
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
.location-list {
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: var(--theme-transition);
}

.location-list__header {
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 1rem;
  flex-wrap: wrap;
}

.search-field {
  display: flex;
  flex-direction: column;
  flex: 1;
  max-width: 300px;
}

.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  color: var(--color-text-secondary);
  pointer-events: none;
}

.input {
  width: 100%;
  padding: 0.625rem 0.875rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
  background: var(--color-surface);
  color: var(--color-text-primary);
  padding-right: 2rem; 
}

.input.with-icon {
  padding-left: 2.5rem;
}

.input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.clear-btn {
  position: absolute;
  right: 0.5rem;
  background: none;
  border: none;
  color: var(--color-text-tertiary);
  cursor: pointer;
  padding: 0.25rem;
  font-size: 0.75rem;
}

.clear-btn:hover {
  color: var(--color-text-secondary);
}

.location-list__actions {
  display: flex;
  gap: 0.75rem;
}

.table-container {
  overflow-x: auto;
}

.location-table {
  width: 100%;
  border-collapse: collapse;
}

.location-table th {
  padding: 0.875rem 1.5rem;
  text-align: left;
  background: var(--color-surface);
  font-weight: 600;
  color: var(--color-text-secondary);
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid var(--color-border);
}

.location-table td {
  padding: 1rem 1.5rem;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
  color: var(--color-text-primary);
  font-size: 0.875rem;
}

.location-table tr:last-child td {
  border-bottom: none;
}

.hover-row:hover {
  background-color: var(--color-surface-hover);
}

.location-name {
  font-weight: 600;
  color: var(--color-primary);
}

.text-right { text-align: right; }
.text-center { text-align: center; }
.text-muted { color: var(--color-text-secondary); }
.text-truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.py-8 { padding-top: 2rem; padding-bottom: 2rem; }

/* Spinner */
.spinner {
  display: inline-block;
  width: 1.5rem;
  height: 1.5rem;
  border: 2px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* Badge Styles */
.badge {
  display: inline-flex;
  padding: 0.25rem 0.625rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
  line-height: 1;
}
.badge--success { background: rgba(16, 185, 129, 0.1); color: #10b981; }
.badge--danger { background: rgba(239, 68, 68, 0.1); color: #ef4444; }
.badge--gray { background: var(--color-surface-hover); color: var(--color-text-secondary); border: 1px solid var(--color-border); }

/* Button Styles */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1rem;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.15s ease-in-out;
}
.btn--primary {
  background: var(--color-primary);
  color: white;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}
.btn--primary:hover {
  background: var(--color-primary-dark);
  transform: translateY(-1px);
}
.btn:active {
  transform: translateY(0);
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.375rem 0.75rem;
  border: 1px solid var(--color-border);
  background: transparent;
  border-radius: 6px;
  font-size: 0.75rem;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}
.action-btn:hover {
  background: var(--color-surface-hover);
  color: var(--color-primary);
  border-color: var(--color-primary);
}

.kbd-hint {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 1.25rem;
  height: 1.25rem;
  padding: 0 0.25rem;
  font-family: monospace;
  font-size: 0.7rem;
  font-weight: 500;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  color: var(--color-text-secondary);
  margin-left: 0.25rem;
}
</style>
