<script setup lang="ts">
import type { User, CreateUserRequest, UpdateUserRequest } from "~/types/user";

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

const isEditMode = computed(() => !!props.user);
// i18n
const { t } = useI18n();

const title = computed(() =>
  isEditMode.value ? t("users.editUser") : t("users.createUser"),
);

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
  { value: "USER", label: t("users.roles.USER") }, // Changed VIEWER to USER to match previous file keys
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
  () => props.open,
  (isOpen) => {
    if (isOpen) {
      if (props.user) {
        form.username = props.user.username;
        form.email = props.user.email;
        form.firstName = props.user.firstName;
        form.lastName = props.user.lastName;
        form.roleName = props.user.roles[0] || "ROLE_USER";
        form.password = "";
      } else {
        form.username = "";
        form.email = "";
        form.password = "";
        form.firstName = "";
        form.lastName = "";
        form.roleName = "ROLE_USER";
      }
    }
  },
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

          <!-- Form -->
          <form class="dialog__body" @submit.prevent="handleSubmit">
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
              <select
                v-model="form.roleName"
                class="form-input"
                :disabled="loading"
              >
                <option
                  v-for="role in roleOptions"
                  :key="role.value"
                  :value="role.value"
                >
                  {{ role.label }}
                </option>
              </select>
            </div>
          </form>

          <!-- Footer -->
          <div class="dialog__footer">
            <button
              type="button"
              class="btn btn--secondary"
              :disabled="loading"
              @click="emit('close')"
            >
              {{ t("common.cancel") }}
            </button>
            <button
              type="submit"
              class="btn btn--primary"
              :disabled="!isValid || loading"
              @click="handleSubmit"
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
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
/* Reuse existing dialog styles or define them here */
/* For consistency, copying core styles from ReceiveStockDialog reference */
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

.dialog__footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid var(--color-border);
  background: var(--color-surface);
}

.dialog__error {
  margin: 1rem 1.5rem 0;
  padding: 0.75rem 1rem;
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
  border-radius: 4px;
}

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
  margin-bottom: 0.25rem;
  color: var(--color-text-primary);
}
.required {
  color: #dc2626;
}
.form-input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-surface);
  color: var(--color-text-primary);
}
.form-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.form-hint {
  font-size: 0.75rem;
  color: var(--color-text-secondary);
  display: block;
  margin-top: 0.25rem;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  border: none;
  font-weight: 500;
}
.btn--primary {
  background: #1a73e8;
  color: white;
}
.btn--secondary {
  background: var(--color-card);
  border: 1px solid var(--color-border);
  color: var(--color-text-primary);
}
.btn:disabled {
  opacity: 0.5;
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
