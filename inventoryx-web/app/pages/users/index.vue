<script setup lang="ts">
import { useUserStore } from "~/stores/users";
import { storeToRefs } from "pinia";
import type { User, CreateUserRequest, UpdateUserRequest } from "~/types/user";
import UserDialog from "~/components/UserDialog.vue";

definePageMeta({
  title: "User Management",
  middleware: ["auth", "admin"],
});

const userStore = useUserStore();
const { users, loading, totalRecords } = storeToRefs(userStore);

// i18n
const { t } = useI18n();

// Pagination state
const currentPage = ref(0);
const pageSize = ref(10);

// Dialog state
const isDialogOpen = ref(false);
const selectedUser = ref<User | null>(null);
const isSaving = ref(false);
const saveError = ref<string | null>(null);

// Confirm Dialog state
const isConfirmOpen = ref(false);
const userToToggle = ref<string | null>(null);
const isToggling = ref(false);
const confirmMessage = computed(() => {
  return t("users.confirmToggleMessage");
});

onMounted(() => {
  fetchData();
});

const fetchData = () => {
  userStore.fetchUsers(currentPage.value, pageSize.value);
};

const totalPages = computed(() =>
  Math.ceil(totalRecords.value / pageSize.value),
);

const handlePageChange = (page: number) => {
  currentPage.value = page;
  fetchData();
};

const handlePageSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 0;
  fetchData();
};

const handleToggleStatus = (id: string) => {
  userToToggle.value = id;
  isConfirmOpen.value = true;
};

const handleConfirmToggle = async () => {
  if (!userToToggle.value) return;

  isToggling.value = true;
  try {
    await userStore.toggleUser(userToToggle.value);
    isConfirmOpen.value = false;
    userToToggle.value = null;
  } finally {
    isToggling.value = false;
  }
};

const handleCreateUser = () => {
  selectedUser.value = null;
  saveError.value = null;
  isDialogOpen.value = true;
};

const handleEditUser = (user: User) => {
  selectedUser.value = user;
  saveError.value = null;
  isDialogOpen.value = true;
};

const handleUserSubmit = async (
  data: CreateUserRequest | UpdateUserRequest,
) => {
  isSaving.value = true;
  saveError.value = null;
  try {
    if (selectedUser.value) {
      await userStore.updateUser(selectedUser.value.id, data);
    } else {
      await userStore.createUser(data);
    }
    isDialogOpen.value = false;
    fetchData();
    // Maybe show toast success
  } catch (e: any) {
    saveError.value = e.response?.data?.message || "Failed to save user";
  } finally {
    isSaving.value = false;
  }
};

const formatDate = (dateArr: string | number[]) => {
  if (!dateArr) return "-";
  // Handle array format [2024, 1, 1, 12, 0]
  if (Array.isArray(dateArr) && dateArr.length >= 5) {
    return new Date(
      dateArr[0]!,
      dateArr[1]! - 1,
      dateArr[2]!,
      dateArr[3]!,
      dateArr[4]!,
    ).toLocaleString();
  }
  return new Date(dateArr as string).toLocaleString();
};
</script>

<template>
  <div class="user-page">
    <header class="page-header">
      <div class="page-header__content">
        <h1 class="page-header__title">{{ t("users.title") }}</h1>
        <p class="page-header__subtitle">{{ t("users.subtitle") }}</p>
      </div>
      <div class="page-header__actions">
        <CommonThemeToggle />
        <CommonLanguageSwitcher />
        <NuxtLink to="/dashboard" class="btn btn--ghost">
          ← {{ t("common.back") }}
        </NuxtLink>
      </div>
    </header>

    <!-- Actions -->
    <div class="actions-bar">
      <button class="btn btn--primary" @click="handleCreateUser">
        + {{ t("users.createUser") }}
      </button>
    </div>

    <!-- Table -->
    <div class="table-container">
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
                @click="handleEditUser(user)"
              >
                {{ t("common.edit") }}
              </button>
              <button
                class="btn btn--small btn--ghost"
                @click="handleToggleStatus(user.id)"
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

    <!-- Pagination -->
    <CommonPagination
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="totalRecords"
      :page-size="pageSize"
      @page-change="handlePageChange"
      @page-size-change="handlePageSizeChange"
    />

    <UserDialog
      :open="isDialogOpen"
      :user="selectedUser"
      :loading="isSaving"
      :error="saveError"
      @close="isDialogOpen = false"
      @submit="handleUserSubmit"
    />

    <CommonConfirmDialog
      :show="isConfirmOpen"
      :title="t('users.confirmToggleTitle')"
      :message="confirmMessage"
      type="warning"
      :loading="isToggling"
      @confirm="handleConfirmToggle"
      @cancel="isConfirmOpen = false"
    />
  </div>
</template>

<style scoped>
.user-page {
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

.actions-bar {
  margin-bottom: 1rem;
  display: flex;
  justify-content: flex-end;
}

.table-container {
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.data-table th,
.data-table td {
  padding: 1rem;
  border-bottom: 1px solid var(--color-border);
  color: var(--color-text-primary);
}

.data-table th {
  background: var(--color-surface);
  font-weight: 600;
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
.badge--info {
  background: #dbeafe;
  color: #1e40af;
  margin-right: 4px;
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
.btn--primary {
  background: #1a73e8;
  color: white;
}
.btn--ghost {
  background: transparent;
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
}
.btn--small {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}
.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading-state,
.empty-state {
  padding: 2rem;
  text-align: center;
  color: var(--color-text-secondary);
}
</style>
