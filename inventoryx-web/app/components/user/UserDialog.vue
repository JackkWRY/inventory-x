<script setup lang="ts">
import type { User, CreateUserRequest, UpdateUserRequest } from "~/types/user";
import UserForm from "./UserForm.vue";

interface Props {
  open: boolean;
  user?: User | null; // If provided, edit mode
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

// i18n
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
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="open" class="dialog-backdrop" @click.self="emit('close')">
        <div class="dialog" role="dialog" aria-modal="true">
          <!-- Header -->
          <div class="dialog__header">
            <h2 class="dialog__title">{{ title }}</h2>
            <button class="dialog__close" @click="emit('close')">✕</button>
          </div>

          <!-- Error -->
          <div v-if="error" class="dialog__error">{{ error }}</div>

          <!-- Body (Form) -->
          <div class="dialog__body">
            <UserForm
              :user="user"
              :loading="loading"
              @submit="handleSubmit"
              @cancel="emit('close')"
            />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.dialog {
  background: var(--color-card);
  border-radius: 8px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
  max-width: 500px;
  width: 100%;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.dialog__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

.dialog__title {
  font-size: 1.25rem;
  font-weight: 500;
  margin: 0;
  color: var(--color-text-primary);
}

.dialog__close {
  background: none;
  border: none;
  font-size: 1.25rem;
  color: var(--color-text-secondary);
  cursor: pointer;
}

.dialog__body {
  padding: 1.5rem;
  overflow-y: auto;
}

.dialog__error {
  margin: 1rem 1.5rem 0;
  padding: 0.75rem 1rem;
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
  border-radius: 4px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
