<script setup lang="ts">
import { type Location, LocationType, LocationStatus, type CreateLocationCommand, type UpdateLocationCommand } from "~/types/location";

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
    const payload: CreateLocationCommand | UpdateLocationCommand = {
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
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 1rem;
}

.modal {
  background: var(--glass-bg-strong);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--glass-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.05);
}

.modal-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--color-text-secondary);
  line-height: 1;
  transition: color 0.2s;
}

.close-btn:hover {
  color: var(--color-text-primary);
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
  color: var(--color-text-primary);
  margin-bottom: 0.375rem;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.625rem 0.875rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.875rem;
  background: var(--color-surface);
  color: var(--color-text-primary);
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.modal-actions {
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
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
