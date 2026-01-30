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
  <div class="list-container">
    <!-- Header with Search -->
    <div class="list-header">
      <div class="list-header__search">
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
      <div class="list-header__actions">
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
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="mr-1">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
                {{ t("common.edit") }}
              </button>
              <button
                class="btn btn--small btn--ghost"
                @click="$emit('toggle', user.id)"
              >
                <svg v-if="user.isActive" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="mr-1 text-danger">
                  <circle cx="12" cy="12" r="10"></circle>
                  <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line>
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="mr-1 text-success">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                  <polyline points="22 4 12 14.01 9 11.01"></polyline>
                </svg>
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
/* All base styles now come from global main.css */
/* Only component-specific overrides remain here */
.table-wrapper {
  overflow-x: auto;
}
.actions-cell {
  display: flex;
  gap: 0.5rem;
}
</style>
