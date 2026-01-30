<script setup lang="ts">
import { useUserStore } from "~/stores/users";
import { storeToRefs } from "pinia";
import type { User, CreateUserRequest, UpdateUserRequest } from "~/types/user";
import UserDialog from "~/components/user/UserDialog.vue";
import UserList from "~/components/user/UserList.vue";

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

// Search state
const searchQuery = ref("");
let debounceTimeout: NodeJS.Timeout | null = null;

// Watch search query for changes
watch(searchQuery, () => {
  if (debounceTimeout) clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(() => {
    currentPage.value = 0;
    fetchData();
  }, 300);
});

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
  userStore.fetchUsers(currentPage.value, pageSize.value, searchQuery.value || undefined);
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
</script>

<template>
  <div class="user-page">
    <header class="page-header">
      <div class="page-header__content">
        <h1 class="page-header__title">{{ t("users.title") }}</h1>
        <p class="page-header__subtitle">{{ t("users.subtitle") }}</p>
      </div>
      <div class="page-header__actions">
        <NuxtLink to="/" class="btn btn--back">
          ← {{ t("common.back") }}
        </NuxtLink>
      </div>
    </header>

    <!-- User List Component -->
    <UserList
      v-model:search="searchQuery"
      :users="users"
      :loading="loading"
      @create="handleCreateUser"
      @edit="handleEditUser"
      @toggle="handleToggleStatus"
    />

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
</style>
