<script setup lang="ts">
import { type Location, LocationType, LocationStatus, type CreateLocationRequest, type UpdateLocationRequest } from "~/types/location";

const props = defineProps<{
  open: boolean;
  location?: Location | null;
  loading: boolean;
  error?: string | null;
}>();

const emit = defineEmits(["close", "submit"]);

const { t } = useI18n();

const form = reactive({
  name: "",
  type: LocationType.WAREHOUSE,
  description: "",
  address: "",
  status: LocationStatus.ACTIVE,
});

watch(
  () => props.location,
  (newLoc) => {
    if (newLoc) {
      form.name = newLoc.name;
      form.type = newLoc.type;
      form.description = newLoc.description || "";
      form.address = newLoc.address || "";
      form.status = newLoc.status;
    } else {
      // Reset
      form.name = "";
      form.type = LocationType.WAREHOUSE;
      form.description = "";
      form.address = "";
      form.status = LocationStatus.ACTIVE;
    }
  },
  { immediate: true }
);

const handleSubmit = () => {
    // For create
    const payload: CreateLocationRequest | UpdateLocationRequest = {
        name: form.name,
        type: form.type,
        description: form.description,
        address: form.address,
        ...(props.location ? { status: form.status } : {})
    };
    emit("submit", payload);
};
</script>

<template>
  <div v-if="open" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal">
      <div class="modal-header">
        <h3>{{ location ? t("locations.edit") : t("locations.create") }}</h3>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <div class="modal-body">
        <CommonErrorBanner v-if="error" :message="error" />

        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>{{ t("locations.name") }}</label>
            <input
              v-model="form.name"
              type="text"
              required
              :placeholder="t('locations.namePlaceholder')"
            />
          </div>

          <div class="form-group">
            <label>{{ t("locations.type") }}</label>
            <select v-model="form.type">
              <option :value="LocationType.WAREHOUSE">Warehouse</option>
              <option :value="LocationType.STORE">Store</option>
              <option :value="LocationType.TRANSIT">Transit</option>
            </select>
          </div>

          <div class="form-group">
             <label>{{ t("locations.address") }}</label>
             <textarea v-model="form.address" rows="2"></textarea>
          </div>

          <div class="form-group">
            <label>{{ t("locations.description") }}</label>
            <textarea v-model="form.description" rows="2"></textarea>
          </div>

          <div v-if="location" class="form-group">
            <label>{{ t("locations.status") }}</label>
            <select v-model="form.status">
              <option :value="LocationStatus.ACTIVE">Active</option>
              <option :value="LocationStatus.INACTIVE">Inactive</option>
            </select>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn btn--secondary" @click="$emit('close')">
              {{ t("common.cancel") }}
            </button>
            <button type="submit" class="btn btn--primary" :disabled="loading">
              <span v-if="loading" class="spinner-sm"></span>
              {{ location ? t("common.update") : t("common.create") }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  max-height: 90vh;
}

.modal-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.125rem;
  color: #111827;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6b7280;
}

.modal-body {
  padding: 1.5rem;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.25rem;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.875rem;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  border: none;
}

.btn--primary {
  background: var(--color-primary);
  color: white;
}

.btn--primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn--secondary {
  background: white;
  border: 1px solid #d1d5db;
  color: #374151;
}

.spinner-sm {
  display: inline-block;
  width: 12px;
  height: 12px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s linear infinite;
  margin-right: 0.5rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
