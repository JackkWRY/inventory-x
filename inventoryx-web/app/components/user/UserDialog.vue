<script setup lang="ts">
import type { User, CreateUserRequest, UpdateUserRequest } from "~/types/user";
import UserForm from "./UserForm.vue";
import BaseModal from "~/components/common/BaseModal.vue";

interface Props {
  open: boolean;
  user?: User | null;
  loading?: boolean;
  error?: string | null;
}

const props = withDefaults(defineProps<Props>(), {
  user: null,
  loading: false,
  error: null,
});

const emit = defineEmits<{
  close: [];
  submit: [data: CreateUserRequest | UpdateUserRequest];
}>();

const { t } = useI18n();

const isEditMode = computed(() => !!props.user);
const title = computed(() =>
  isEditMode.value ? t("users.editUser") : t("users.createUser"),
);

const handleSubmit = (data: CreateUserRequest | UpdateUserRequest) => {
  emit("submit", data);
};
</script>

<template>
  <BaseModal
    :open="open"
    :title="title"
    size="md"
    @close="emit('close')"
  >
    <!-- Error -->
    <template #error>
      <div v-if="error" class="dialog__error">{{ error }}</div>
    </template>

    <!-- Body (Form) -->
    <template #body>
      <UserForm
        :user="user"
        :loading="loading"
        @submit="handleSubmit"
        @cancel="emit('close')"
      />
    </template>
  </BaseModal>
</template>

<style scoped>
/* Component uses UserForm for content, minimal custom styles needed */
</style>

