<script setup lang="ts">
/**
 * UserForm Component
 * 
 * Form for creating and editing users.
 * Uses global form and button classes for consistent styling.
 */
import type { User, CreateUserRequest, UpdateUserRequest } from "~/types/user";
import { useUserValidation } from "~/composables/useUserValidation";

// Props
interface Props {
  user?: User | null;
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
const { isValid } = useUserValidation(form, isEditMode);

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
        <label class="form-label">
          {{ t("users.username") }} <span class="text-danger">*</span>
        </label>
        <input
          v-model="form.username"
          class="form-input"
          :disabled="loading || isEditMode"
          required
          minlength="3"
        />
      </div>
      <div class="form-group">
        <label class="form-label">
          {{ t("users.password") }} <span class="text-danger">*</span>
        </label>
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
        <label class="form-label">
          {{ t("users.firstName") }} <span class="text-danger">*</span>
        </label>
        <input
          v-model="form.firstName"
          class="form-input"
          :disabled="loading"
          required
        />
      </div>
      <div class="form-group">
        <label class="form-label">
          {{ t("users.lastName") }} <span class="text-danger">*</span>
        </label>
        <input
          v-model="form.lastName"
          class="form-input"
          :disabled="loading"
          required
        />
      </div>
    </div>

    <div class="form-group">
      <label class="form-label">
        {{ t("users.email") }} <span class="text-danger">*</span>
      </label>
      <input
        type="email"
        v-model="form.email"
        class="form-input"
        :disabled="loading"
        required
      />
    </div>

    <div class="form-group">
      <label class="form-label">
        {{ t("users.role") }} <span class="text-danger">*</span>
      </label>
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
        <span v-if="loading" class="spinner spinner--sm spinner--light"></span>
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

<!-- No scoped styles needed - uses global form, button, and spinner classes from main.css -->
