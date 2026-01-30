<script setup lang="ts">
import type { User } from "~/types/user";

const props = defineProps<{
  users: User[];
  loading: boolean;
  search?: string;
}>();

const emit = defineEmits(["edit", "toggle", "update:search", "create"]);
const { t } = useI18n();

const formatDate = (dateArr: string | number[]) => {
  if (!dateArr) return "-";
  if (Array.isArray(dateArr) && dateArr.length >= 5) {
    return new Date(
      dateArr[0]!,
      dateArr[1]! - 1,
      dateArr[2]!,
      dateArr[3]!,
      dateArr[4]!
    ).toLocaleString();
  }
  return new Date(dateArr as string).toLocaleString();
};
</script>

<template>
  <div class="user-list">
    <!-- Header with Search -->
    <div class="list-header">
      <div class="list-search">
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
              id="search-user"
              type="text"
              class="input with-icon"
              :value="search"
              :placeholder="t('common.search') + '...'"
              @input="$emit('update:search', ($event.target as HTMLInputElement).value)"
            />
          </div>
        </div>
      </div>
      <div class="list-actions">
        <button class="btn btn--primary" @click="$emit('create')">
          + {{ t("users.createUser") }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-wrapper">
      <div v-if="loading && !users.length" class="loading-state">
        {{ t("common.loading") }}
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th>{{ t("users.username") }}</th>
            <th>{{ t("users.name") }}</th>
            <th>{{ t("users.email") }}</th>
            <th>{{ t("users.role") }}</th>
            <th>{{ t("users.status") }}</th>
            <th>{{ t("users.createdAt") }}</th>
            <th>{{ t("users.actions") || t("common.actions") }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.username }}</td>
            <td>{{ user.firstName }} {{ user.lastName }}</td>
            <td>{{ user.email }}</td>
            <td>
              <span
                v-for="role in user.roles"
                :key="role"
                class="badge badge--info"
                >{{ t("users.roles." + role) }}</span
              >
            </td>
            <td>
              <span
                :class="[
                  'badge',
                  user.isActive ? 'badge--success' : 'badge--danger',
                ]"
              >
                {{ user.isActive ? t("users.active") : t("users.inactive") }}
              </span>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td class="actions-cell">
              <button
                class="btn btn--small btn--ghost"
                @click="$emit('edit', user)"
              >
                {{ t("common.edit") }}
              </button>
              <button
                class="btn btn--small btn--ghost"
                @click="$emit('toggle', user.id)"
              >
                {{ user.isActive ? t("users.disable") : t("users.enable") }}
              </button>
            </td>
          </tr>
          <tr v-if="users.length === 0">
            <td colspan="7" class="empty-state">{{ t("messages.noData") }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.user-list {
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
  transition: var(--theme-transition);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  gap: 1rem;
  flex-wrap: wrap;
}

.list-search {
  display: flex;
  gap: 1rem;
  align-items: flex-end;
  flex-wrap: wrap;
}

.search-field {
  display: flex;
  flex-direction: column;
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
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 0.875rem;
  min-width: 280px;
  transition: border-color 0.2s, background-color 0.3s;
  background: var(--color-surface);
  color: var(--color-text-primary);
}

.input.with-icon {
  padding-left: 2.5rem;
}

.input:focus {
  outline: none;
  border-color: #4285f4;
}

.list-actions {
  flex-shrink: 0;
}

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.data-table th,
.data-table td {
  padding: 1rem;
  border-bottom: 1px solid var(--color-border);
  color: var(--color-text-primary);
  transition: all 0.2s ease;
}

.data-table th {
  background: var(--glass-bg-strong);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  font-weight: 600;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-text-secondary);
}

.data-table tbody tr {
  transition: all 0.2s ease;
}

.data-table tbody tr:hover {
  background: var(--color-surface-hover);
}

.data-table tbody tr:hover td:first-child {
  box-shadow: inset 3px 0 0 var(--color-primary);
}

.badge {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}
.badge--success {
  background: #d1fae5;
  color: #065f46;
}
.badge--danger {
  background: #fee2e2;
  color: #991b1b;
}
.badge--status-info {
  background: #dbeafe;
  color: #1e40af;
  margin-right: 4px;
}

.loading-state,
.empty-state {
  padding: 2rem;
  text-align: center;
  color: var(--color-text-secondary);
}
</style>
