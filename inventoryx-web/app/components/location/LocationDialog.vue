<script setup lang="ts">
import { type Location, LocationType, LocationStatus, type CreateLocationCommand, type UpdateLocationCommand } from "~/types/location";
import BaseModal from "~/components/common/BaseModal.vue";

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
      form.name = "";
      form.type = LocationType.WAREHOUSE;
      form.description = "";
      form.address = "";
      form.status = LocationStatus.ACTIVE;
    }
  },
  { immediate: true }
);

const dialogTitle = computed(() => 
  props.location ? t("locations.edit") : t("locations.create")
);

const handleSubmit = () => {
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
  <BaseModal
    :open="open"
    :title="dialogTitle"
    size="md"
    @close="emit('close')"
  >
    <!-- Error -->
    <template #error>
      <CommonErrorBanner v-if="error" :message="error" />
    </template>

    <!-- Form Body -->
    <template #body>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label class="form-label">{{ t("locations.name") }}</label>
          <input
            v-model="form.name"
            type="text"
            class="form-input"
            required
            :placeholder="t('locations.namePlaceholder')"
          />
        </div>

        <div class="form-group">
          <label class="form-label">{{ t("locations.type") }}</label>
          <select v-model="form.type" class="form-input">
            <option :value="LocationType.WAREHOUSE">Warehouse</option>
            <option :value="LocationType.STORE">Store</option>
            <option :value="LocationType.TRANSIT">Transit</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">{{ t("locations.address") }}</label>
          <textarea v-model="form.address" class="form-input" rows="2"></textarea>
        </div>

        <div class="form-group">
          <label class="form-label">{{ t("locations.description") }}</label>
          <textarea v-model="form.description" class="form-input" rows="2"></textarea>
        </div>

        <div v-if="location" class="form-group">
          <label class="form-label">{{ t("locations.status") }}</label>
          <select v-model="form.status" class="form-input">
            <option :value="LocationStatus.ACTIVE">Active</option>
            <option :value="LocationStatus.INACTIVE">Inactive</option>
          </select>
        </div>
      </form>
    </template>

    <!-- Footer -->
    <template #footer>
      <button type="button" class="btn btn--secondary" @click="emit('close')">
        {{ t("common.cancel") }}
      </button>
      <button 
        type="submit" 
        class="btn btn--primary" 
        :disabled="loading"
        @click="handleSubmit"
      >
        <span v-if="loading" class="spinner"></span>
        {{ location ? t("common.update") : t("common.create") }}
      </button>
    </template>
  </BaseModal>
</template>

<style scoped>
.spinner {
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
  to { transform: rotate(360deg); }
}
</style>

