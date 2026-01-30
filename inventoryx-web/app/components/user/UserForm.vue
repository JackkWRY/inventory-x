<script setup lang="ts">
import type { User, CreateUserRequest, UpdateUserRequest } from "~/types/user";

// Props
interface Props {
  user?: User | null; // If provided, edit mode
  loading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  user: null,
  loading: false,
});

// Emits
const emit = defineEmits<{
  submit: [data: CreateUserRequest | UpdateUserRequest];
  cancel: [];
}>();

// i18n
const { t } = useI18n();

const isEditMode = computed(() => !!props.user);

const form = reactive({
  username: "",
  email: "",
  password: "",
  firstName: "",
  lastName: "",
  roleName: "ROLE_USER",
});

// Roles (Matching DB values)
const roleOptions = computed(() => [
  { value: "STAFF", label: t("users.roles.STAFF") },
  { value: "ADMIN", label: t("users.roles.ADMIN") },
  { value: "MANAGER", label: t("users.roles.MANAGER") },
  { value: "USER", label: t("users.roles.USER") },
]);

// Validation
const isValid = computed(() => {
  const basic =
    form.username.length >= 3 &&
    form.email.includes("@") &&
    form.firstName.length > 0 &&
    form.lastName.length > 0;

  if (isEditMode.value) return basic;
  return basic && form.password.length >= 6;
});

// Watch for edit mode to populate form
watch(
  () => props.user,
  (newUser) => {
    if (newUser) {
      form.username = newUser.username;
      form.email = newUser.email;
      form.firstName = newUser.firstName;
      form.lastName = newUser.lastName;
      form.roleName = newUser.roles[0] || "ROLE_USER";
      form.password = "";
    } else {
      form.username = "";
      form.email = "";
      form.password = "";
      form.firstName = "";
      form.lastName = "";
      form.roleName = "ROLE_USER";
    }
  },
  { immediate: true },
);

const handleSubmit = () => {
  if (!isValid.value) return;

  if (isEditMode.value) {
    emit("submit", {
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email,
      roleName: form.roleName,
    } as UpdateUserRequest);
  } else {
    emit("submit", {
      username: form.username,
      password: form.password,
      email: form.email,
      firstName: form.firstName,
      lastName: form.lastName,
      roleName: form.roleName,
    } as CreateUserRequest);
  }
};
</script>

<template>
  <form class="user-form" @submit.prevent="handleSubmit">
    <div class="form-row" v-if="!isEditMode">
      <div class="form-group">
        <label class="form-label"
          >{{ t("users.username") }} <span class="required">*</span></label
        >
        <input
          v-model="form.username"
          class="form-input"
          :disabled="loading || isEditMode"
          required
          minlength="3"
        />
      </div>
      <div class="form-group">
        <label class="form-label"
          >{{ t("users.password") }} <span class="required">*</span></label
        >
        <input
          type="password"
          v-model="form.password"
          class="form-input"
          :disabled="loading"
          required
          minlength="6"
        />
      </div>
    </div>
    <div class="form-group" v-else>
      <label class="form-label">{{ t("users.username") }}</label>
      <input :value="form.username" class="form-input" disabled />
      <span class="form-hint">{{ t("users.usernameImmutable") }}</span>
    </div>

    <div class="form-row">
      <div class="form-group">
        <label class="form-label"
          >{{ t("users.firstName") }} <span class="required">*</span></label
        >
        <input
          v-model="form.firstName"
          class="form-input"
          :disabled="loading"
          required
        />
      </div>
      <div class="form-group">
        <label class="form-label"
          >{{ t("users.lastName") }} <span class="required">*</span></label
        >
        <input
          v-model="form.lastName"
          class="form-input"
          :disabled="loading"
          required
        />
      </div>
    </div>

    <div class="form-group">
      <label class="form-label"
        >{{ t("users.email") }} <span class="required">*</span></label
      >
      <input
        type="email"
        v-model="form.email"
        class="form-input"
        :disabled="loading"
        required
      />
    </div>

    <div class="form-group">
      <label class="form-label"
        >{{ t("users.role") }} <span class="required">*</span></label
      >
      <select v-model="form.roleName" class="form-input" :disabled="loading">
        <option
          v-for="role in roleOptions"
          :key="role.value"
          :value="role.value"
        >
          {{ role.label }}
        </option>
      </select>
    </div>

    <!-- Actions -->
    <div class="form-actions">
      <button
        type="button"
        class="btn btn--secondary"
        :disabled="loading"
        @click="emit('cancel')"
      >
        {{ t("common.cancel") }}
      </button>
      <button
        type="submit"
        class="btn btn--primary"
        :disabled="!isValid || loading"
      >
        <span v-if="loading" class="spinner"></span>
        {{
          loading
            ? t("common.saving")
            : isEditMode
              ? t("users.updateUser")
              : t("users.createUser")
        }}
      </button>
    </div>
  </form>
</template>

<style scoped>
.form-group {
  margin-bottom: 1rem;
}
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}
.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  margin-bottom: 0.375rem;
  color: var(--color-text-primary);
}
.required {
  color: #dc2626;
}
.form-input {
  width: 100%;
  padding: 0.625rem 0.875rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-surface);
  color: var(--color-text-primary);
  font-size: 0.9375rem;
  transition: all 0.2s;
}
.form-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
  outline: none;
}
.form-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: var(--color-bg);
}
.form-hint {
  font-size: 0.75rem;
  color: var(--color-text-secondary);
  display: block;
  margin-top: 0.25rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.5rem;
  padding-top: 1.25rem;
  border-top: 1px solid var(--glass-border);
}

.btn {
  padding: 0.625rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  border: none;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner {
  display: inline-block;
  width: 0.875rem;
  height: 0.875rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 0.5rem;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
